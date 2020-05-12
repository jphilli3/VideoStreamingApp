package controllers

import javax.inject._
import models._
import videostreaming.shared.SharedMessages
import akka.http.scaladsl.model.headers.LinkParams.title
import play.api.libs.json._

import play.api.db.slick.HasDatabaseConfigProvider
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.lang.ProcessBuilder.Redirect
import akka.stream.Materializer
import akka.actor.ActorSystem
import akka.actor.Props
import actors._
import javax.inject.{Inject, Named, Singleton}
import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
// import akka.event.Logging
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import akka.util.Timeout
import org.reactivestreams.Publisher
import play.api.mvc._
import play.api.libs.streams.ActorFlow
import scala.concurrent.{ExecutionContext, Future}
import videostreaming.shared.WebRtcProtocol.Disconnect



@Singleton
class StreamController @Inject()(@Named("streamWebSocketsActor") streamWebSocketsActor: ActorRef,
                                 @Named("streamsActor") streamsActor: ActorRef)
            (protected val dbConfigProvider: DatabaseConfigProvider, 
            cc: ControllerComponents) 
            (implicit ec: ExecutionContext, 
            system: ActorSystem, mat: Materializer) 
            extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {


    val manager = system.actorOf(Props[ChatManager], "Manager")

    implicit val messageDataReads = Json.reads[Message]
    implicit val messageDataWrites = Json.writes[Message]

    private val model = new MessageModel(db)

    def withSessionUsername(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
        request.session.get("username").map(f).getOrElse(Future.successful(Ok(views.html.login())))
    }

    // def withSessionStreamId(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    //     request.session.get("streamid").map(f).getOrElse(Future.successful(Ok(views.html.login())))
    // }

    def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
        request.body.asJson.map { body =>
            Json.fromJson[A](body) match {
                case JsSuccess(a, path) => f(a)
                case e @ JsError(_) => Future.successful(Redirect(routes.StreamController.stream()))
            }
        }.getOrElse(Future.successful(Redirect(routes.StreamController.stream())))
    }

    def stream() = Action.async { implicit request =>
        withSessionUsername { username =>
            //withSessionStreamId { streamid => 
                Future.successful(Ok(views.html.stream(username)))
            //}
        }
    }

    def logout = Action {
        Redirect("login").withNewSession
    } 

    def getMessages() = Action.async { implicit request => 
        model.getMessagesFor("12").map(messages => Ok(writeJson(messages)))
    }

    def sendNewMessagePost() = Action.async { implicit request => 
        println("Send Message")
        withSessionUsername { username => 
            withJsonBody[Message] { message =>  
                model.createNewMessage("12",username, message).map(count => Ok(Json.toJson(count > 0)))
            }
        }
    }

    def writeJson(messages: Seq[Message]) = { 
        Json.toJson(
            messages.map( message => Map(
            "streamid" -> message.streamid,
            "from" -> message.from,
            "message" -> message.message,
            "time" -> message.time
            ))
        )
    }

    // def streamingWebSocket = WebSocket.accept[String, String] { 
    //     println("streamingWebSocket!!!!!!!!!!!!!!!!")
    // }

   def webSocket = WebSocket.acceptOrResult[String, String] { request => 
   // def webSocket = WebSocket.acceptOrResult[String, String] { request => 
        println("WebSocket may!!!!!!!!!!!!!!!!")

        ActorFlow.actorRef { out => 
            ChatActor.props(out, manager)
        }
        println("WebSocket chat actor may!!!!!!!!!!!!!!!!")

        wsFutureFlow(request).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            println("Cannot create websocket" + e)
            Left(InternalServerError("Cannot create websocket"))
        }
    }

    def wsFutureFlow(request: RequestHeader): Future[Flow[String, String, NotUsed]] = {
      // Creates a source to be materialized as an actor reference.
        val source: Source[String, ActorRef] = {
        // If you want to log on a flow, you have to use a logging adapter.
        // http://doc.akka.io/docs/akka/2.4.4/scala/logging.html#SLF4J
        // val logging = Logging(actorSystem.eventStream, logger.getName)

        // Creating a source can be done through various means, but here we want
        // the source exposed as an actor so we can send it messages from other
        // actors.
            Source.actorRef[String](10, OverflowStrategy.dropTail)
        }

      // Creates a sink to be materialized as a publisher.  Fanout is false as we only want
      // a single subscriber here.
      val sink: Sink[String, Publisher[String]] = Sink.asPublisher(fanout = false)

      // Connect the source and sink into a flow, telling it to keep the materialized values,
      // and then kicks the flow into existence.
      val (proxyActor, publisher) = source.toMat(sink)(Keep.both).run()

      val webSocketActorFuture = createWsActor(request.id.toString, proxyActor)

      webSocketActorFuture.map { wsActor =>
        // source is what comes in: browser ws events -> play -> publisher -> userActor
        // sink is what comes out:  userActor -> websocketOut -> play -> browser ws events
        val flow: Flow[String, String, NotUsed] = {
            val sink = Sink.actorRef(wsActor, akka.actor.Status.Success(()))
            val source = Source.fromPublisher(publisher)
            Flow.fromSinkAndSource(sink, source)
        }

        // Unhook the user actor when the websocket flow terminates
        // http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#watchTermination
        flow.watchTermination() { (_, termination) =>
            termination.foreach { _ =>
                streamsActor.tell(Disconnect(request.id.toString), proxyActor)
                actorSystem.stop(wsActor)
            }
            NotUsed
        }
      }
    }

    def createWsActor(name: String, responseTargetActor: ActorRef): Future[ActorRef] = {
        // Use guice assisted injection to instantiate and configure the child actor.
        import akka.pattern.ask
        import scala.concurrent.duration._
        implicit val timeout = Timeout(100.millis)
        (streamWebSocketsActor ? Create(name, responseTargetActor)).mapTo[ActorRef]
    }
  }
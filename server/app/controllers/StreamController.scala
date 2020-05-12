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
import akka.event.Logging
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import akka.util.Timeout
import org.reactivestreams.Publisher
import play.api.mvc._
import play.api.libs.streams.ActorFlow

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class StreamController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents) (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {


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

   def webSocket = WebSocket.accept[String, String] { request => 
        ActorFlow.actorRef { out => 
            ChatActor.props(out, manager)
        }
    }

  }
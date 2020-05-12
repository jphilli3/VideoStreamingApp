package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, Props}
// import akka.actor.{Actor, ActorLogging, ActorRef, Props}
// import akka.event.LoggingReceive

import videostreaming.shared.WebRtcProtocol.{ConnectSuccess, JoinStream, LeaveStream, WebRtcMessage}
import upickle.default.{read, write}

case class Create(user: String, responseTargetActor: ActorRef)

class StreamWebSocketsActor @Inject() (@Named("streamsActor") streamsActor: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case Create(user, responseTargetActor) =>
      // log.info(s"creating new websocket actor for user $user")
      // get or create the websocket actor for the new user and send it back
      sender ! context.child(user).getOrElse {
        context.actorOf(Props(new StreamWebSocketActor(user, responseTargetActor, streamsActor)), user)
      }
  }
}

class StreamWebSocketActor (user: String, responseTargetActor: ActorRef, streamsActor: ActorRef) extends Actor {

  override def preStart(): Unit = {
    responseTargetActor ! write(ConnectSuccess(user))
  }

  var currentStream = ""

  override def receive: Receive = {
    case s: String => read[WebRtcMessage](s) match {
      case join @ JoinStream(_, stream) =>
        // log.info(s"websocket for user $user got join message, forwarding to stream $stream")
        currentStream = stream
        streamsActor.tell(join, responseTargetActor)
      case leave @ LeaveStream(_, stream) =>
        // log.info(s"websocket for user $user got leave message, forwarding to stream $stream")
        currentStream = ""
        streamsActor.tell(leave, responseTargetActor)
      // case rest if currentStream.nonEmpty =>
        // log.info(s"websocket for user $user got msg $rest")
        // streamsActor.tell(InStreamMessage(currentStream, rest), responseTargetActor)
    }
  }

}

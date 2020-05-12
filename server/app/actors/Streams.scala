package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
// import akka.event.LoggingReceive
import videostreaming.shared.WebRtcProtocol.{Disconnect, JoinStream, LeaveStream, WebRtcMessage, _}
import upickle.default._

import scala.collection.immutable.HashMap

class StreamsActor extends Actor {

  def receive = {
    case join @ JoinStream(_, stream) =>
      context.child(stream).getOrElse {
        context.actorOf(Props(new StreamActor(stream)), stream)
      }.tell(join, sender)
    case leave @ LeaveStream(_, stream) =>
      context.child(stream).foreach(_.tell(leave, sender))
    case disc @ Disconnect(_) =>
      context.children.foreach(_.tell(disc, sender))
    // case InStreamMessage(stream, msg) =>
      // context.child(stream).foreach(_ ! msg)
  }
}

// case class InStreamMessage(stream: String, msg: WebRtcMessage)

class StreamActor(name: String) extends Actor {

  var users: Map[User, ActorRef] = HashMap()

  override def receive: Receive = {
    case JoinStream(user, _) =>
      users = users + (user -> sender)
      sender ! write(JoinSuccess(users.keys.filter(_ != user).toSeq))
      // log.info(s"user $user joined stream $name, stream has occupants ${users.keySet}")
    case LeaveStream(user, _) =>
      removeUser(user)
    case Disconnect(userId) =>
      users.find(_._1.id == userId).map(_._1).foreach(removeUser)
    case o @ Offer(source, target, _) =>
      // log.info(s"stream $name got offer from $source to $target")
      users.get(target).foreach(_ ! write(o))
    case a @ Answer(source, target, _) =>
      // log.info(s"stream $name got answer from $source to $target")
      users.get(target).foreach(_ ! write(a))
    case ice @ IceCandidate(source, target, _, _, _) =>
      // log.info(s"stream $name got ice candidate from $source to $target")
      users.get(target).foreach(_ ! write(ice))
  }

  private def removeUser(user: User): Unit = {
    users.values.foreach(ar => ar ! write(LeaveSuccess(user)))
    users = users - user
    // log.info(s"user $user left stream $name")
    if(users.isEmpty) {
      // log.info(s"user $user last to leave stream $name, stopping actor")
      context.stop(self)
    }
  }

}
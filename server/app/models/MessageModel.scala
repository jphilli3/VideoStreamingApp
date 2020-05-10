package models

import collection.mutable
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt

case class Message(streamid: String, from: String, message: String, time: String)

class MessageModel (db: Database) (implicit ec: ExecutionContext)  {

    def getMessagesFor(streamid: String):  Future[Seq[Message]] = {
        db.run(
            (for {
                message <- Messages if message.streamid === streamid
            } yield {
                message
            }).sortBy(_.messageTime.desc).result
        ).map(messages => messages.map(message => Message(message.streamid, message.fromUser, message.userMessage, message.messageTime)))
    }

    def createNewMessage(streamid: String, username: String, message: Message): Future[Int] = {
        db.run(Messages += MessagesRow(-1, message.streamid, message.from, message.message, message.time))

    }

}
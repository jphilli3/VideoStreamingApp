package videostreaming.models

import play.api.libs.json.Json

case class User(username: String, password: String)

case class Message(streamId: String, from: String, message: String, time: String)

object ReadsAndWrites {
    implicit val userDataReadsUser = Json.reads[User]
    implicit val userDataWritesUser = Json.writes[User]

    implicit val userDataReadsMessage = Json.reads[Message]
    implicit val userDataWritesMessage = Json.writes[Message]
}

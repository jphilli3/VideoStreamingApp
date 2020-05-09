package videostreaming.models

import play.api.libs.json.Json

case class User(username: String, password: String)

object ReadsAndWrites {
    implicit val userDataReads = Json.reads[User]
    implicit val userDataWrites = Json.writes[User]
}

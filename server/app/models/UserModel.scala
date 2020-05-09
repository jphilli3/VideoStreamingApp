package models

import collection.mutable
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt

case class User(username: String, password: String)

class UserModel (db: Database) (implicit ec: ExecutionContext)  { 

    //private val users = mutable.Map[String, String]("TripPhillips" -> "12345678", "web" -> "apps", "mlewis" -> "prof")

    def validateUser(username: String, password: String): Future[Option[Int]] = {
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        matches.map(userRows => userRows.headOption.flatMap {
        userRow => if (BCrypt.checkpw(password, userRow.password)) Some(userRow.id) else None
        })
    }

    def createUser(username: String, password: String): Future[Option[Int]] = {
        print("Create Username: " + username)
        val matches = db.run(Users.filter(userRow => userRow.username === username).result)
        matches.flatMap { userRows =>
        if (userRows.isEmpty) {
            db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt())))
            .flatMap { addCount => 
                if (addCount > 0) db.run(Users.filter(userRow => userRow.username === username).result)
                .map(_.headOption.map(_.id))
                else Future.successful(None)
            }
        } else Future.successful(None)
        }
    }

}
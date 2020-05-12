package controllers

import javax.inject._
import models._
import videostreaming.shared.SharedMessages
import play.api.mvc._
import akka.http.scaladsl.model.headers.LinkParams.title
import play.api.libs.json._

import play.api.db.slick.HasDatabaseConfigProvider
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.lang.ProcessBuilder.Redirect
import scala.util.Random

@Singleton
class LoginSignupController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents) (implicit ec: ExecutionContext) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  implicit val userDataReads = Json.reads[User]
  implicit val userDataWrites = Json.writes[User]

  private val model = new UserModel(db)

  def loginWithJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Ok(views.html.login()))
      }
    }.getOrElse(Future.successful(Ok(views.html.login())))
  }

  def signupWithJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Ok(views.html.signup()))
      }
    }.getOrElse(Future.successful(Ok(views.html.signup())))
  }

  def login = Action { implicit request => 
    Ok(views.html.login())
  }

  def signup = Action { implicit request => 
    Ok(views.html.signup())
  }



  def validateSignupPost = Action.async { implicit request =>
    signupWithJsonBody[User] { ud => model.createUser(ud.username, ud.password).map { ouserId =>
       ouserId match {
          case Some(userid) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None =>
            Ok(Json.toJson(false))
        }
      }
    }
  }

  def validateLoginPost = Action.async { implicit request =>
    loginWithJsonBody[User] { ud =>
      model.validateUser(ud.username, ud.password).map { ouserId =>
        ouserId match {
          case Some(userid) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None =>
            Ok(Json.toJson(false))
        }
      }

    }
  }

}
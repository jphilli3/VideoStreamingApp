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

@Singleton
class StreamController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents) (implicit ec: ExecutionContext) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

    def withSessionUsername(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
            request.session.get("username").map(f).getOrElse(Future.successful(Ok(views.html.login())))
    }

    def stream = Action { implicit request =>
        Ok(views.html.stream())
    }

    def logout = Action {
        Redirect("login").withNewSession
    } 

}
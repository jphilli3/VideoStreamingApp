package controllers

import javax.inject._

import videostreaming.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  var isUserLoggedIn = false

  def index = Action { implicit request =>
    if (isUserLoggedIn) {
      Ok(views.html.stream())
    } else {
      Ok(views.html.login())
    }
  }

  def stream = Action { implicit request =>
    Ok(views.html.stream())
  }

  def login = Action { implicit request =>
    Ok(views.html.login())
  }

  def signup = Action { implicit request =>
    Ok(views.html.signup())
  }

}

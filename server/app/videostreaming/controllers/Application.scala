package videostreaming.controllers

import javax.inject._

import videostreaming.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  var isUserLoggedIn = true

  def index = Action { implicit request =>
    if (isUserLoggedIn) {
      Ok(views.html.stream())
    } else {
      Ok(views.html.index(SharedMessages.itWorks))
    }
  }

}

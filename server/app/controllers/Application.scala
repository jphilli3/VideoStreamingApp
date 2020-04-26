package controllers

import javax.inject._

import videostreaming.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  var userLoggedIn = true

  def index = Action {
    if (userLoggedIn) {
        Ok(views.html.index("J"))
    } else {
        Ok(views.html.index(SharedMessages.itWorks))
    }
  }

}

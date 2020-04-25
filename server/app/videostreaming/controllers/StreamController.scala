package videostreaming.controllers

import javax.inject._

import videostreaming.shared.SharedMessages
import play.api.mvc._

@Singleton
class StreamController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def stream = Action { implicit request =>
    Ok(views.html.stream())
  }

}
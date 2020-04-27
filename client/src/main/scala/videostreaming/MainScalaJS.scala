package videostreaming

// shared content by both client and server
import videostreaming.shared.SharedMessages
import org.scalajs.dom

import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._
import videostreaming.reactcomponents._

object MainScalaJS {

  def main(args: Array[String]): Unit = {

      if (dom.document.getElementById("stream") != null) {
        ReactDOM.render(
          StreamComponent(),
          dom.document.getElementById("react-root")
        )
      }

      if (dom.document.getElementById("login") != null) {
        ReactDOM.render(
          LoginSignupComponent(true),
          dom.document.getElementById("react-root")
        )
      }

      if (dom.document.getElementById("signup") != null) {
        ReactDOM.render(
          LoginSignupComponent(false),
          dom.document.getElementById("react-root")
        )
      }

  }
}

package videostreaming

// shared content by both client and server
import videostreaming.shared.SharedMessages
import org.scalajs.dom

import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._
import videostreaming.reactcomponents.StreamComponent

object MainScalaJS {

  def main(args: Array[String]): Unit = {
      println("MAIN")
      if (dom.document.getElementById("stream") != null) {
        ReactDOM.render(
          StreamComponent(),
          dom.document.getElementById("react-root")
        )
      }

  }
}

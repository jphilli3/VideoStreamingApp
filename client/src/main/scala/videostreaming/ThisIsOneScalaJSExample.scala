package videostreaming

// shared content by both client and server
import videostreaming.shared.SharedMessages
import org.scalajs.dom

import slinky.core._
import slinky.web.ReactDOM
import slinky.web.html._

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.videoStreamingAppWillWork
    
    println("Call the react stuff.")
    ReactDOM.render(
      h2("Hello, Stanley! This is actually a React Component written by Slinky in \"client directory\"."),
      dom.document.getElementById("root")
    )

  }
}

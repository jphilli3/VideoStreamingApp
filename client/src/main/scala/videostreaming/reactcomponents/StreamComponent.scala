package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window

@react class StreamComponent extends Component {
  type Props = Unit
  case class State(stream: String, streamMessage: String, streamID: String, currentUsername: String)

  val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value

  val currentUsername = document.getElementById("currentUser").asInstanceOf[html.Input].value

  def initialState: State = State("START", "Start my own stream.", "12342342343234", currentUsername)

  def render(): ReactElement = {
    div (className := "stream-page") (
      p (id := "stream-title", className := "stream-title") (
        "Stream Your Face"
      ),
      div (id := "stream-container", className := "stream-container") ( 
        label (id := "streamuser-label", className := "streamuser-label") (
          "Current User: " + state.currentUsername
        ),
        button (id := "settings-button", className := "settings-button", onClick := (_ => { logout() })) (
          //settings button
          "Logout"
        ),
        label (id := "streamid-label", className := "streamid-label") (
          "Stream ID: " + state.streamID
        ),
        canvas (id := "stream-view", className := "stream-view") (
          //stream video view
        ),
      ),
      div (id := "stream-messages-container-top", className := "stream-messages-container-top") (
        div (id := "stream-search-container", className := "stream-search-container") (
          input (id := "streamid-input", className := "streamid-input", placeholder := "Enter Stream ID") (
            //stream id input
          ),
          button (id := "streamid-search-button", className := "streamid-search-button", onClick := (_ => { searchStreamID() })) (
            img (src := document.getElementById("searchImage").asInstanceOf[html.Input].value),
          ),
        ),
        div (id := "start-stream-container", className := "start-stream-container") (
          label (id := "start-stream-container-label", className := "start-stream-container-label") ( 
            state.streamMessage //"I dont have an account" label
          ),
          button (id := "start-stream-container-button", className := "start-stream-container-button", onClick := (_ => { toggleStream() })) (
            state.stream
          ),
        ),
      ),
      div (id := "stream-messages-container-bottom", className := "stream-messages-container-bottom") (
        div (id := "stream-messages-table-container", className := "stream-messages-table-container") (
          label (id := "stream-messages-table-label", className := "stream-messages-table-label") (
            "Messages"
          ),
          table (id := "stream-messages-table", className := "stream-messages-table") (
            //all messages table
          ),
        ),
        div (id := "stream-create-messages-container", className := "stream-create-messages-container") (
          label (id := "create-message-title", className := "create-message-title") (
            "Create Message"
          ),
          textarea (id := "create-message-textarea", className := "create-message-textarea") (
            //message input
          ),
          button (id := "create-message-button", className := "create-message-button", onClick := (_ => {  sendMessage() })) (
            "SEND"
          ),
        ) ,
      ),
    )
  } 

  def logout() {
    window.location.replace(logoutRoute)
  }

  def sendMessage() {

  }

  def searchStreamID() {

  }

  def toggleStream() {
     if (state.stream == "STOP") {
        setState(state.copy(stream = "START", streamMessage = "Start my own stream.", streamID = "11233123"))
      } else {
        setState(state.copy(stream = "STOP", streamMessage = "Stop my stream.", streamID = "11233123"))
      }
  }

  //*****Add functionality******

}
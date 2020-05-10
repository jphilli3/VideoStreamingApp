package videostreaming.reactcomponents

import videostreaming.models.ReadsAndWrites._
import videostreaming.models.Message
import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window
import scalajs.js
import org.scalajs.dom.experimental.RequestInit
import org.scalajs.dom.experimental.RequestMode
import scala.scalajs.js.Thenable.Implicits._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import org.scalajs.dom.experimental.Headers
import org.scalajs.dom.experimental.Fetch
import org.scalajs.dom.experimental.HttpMethod
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import java.util.Date


@react class StreamComponent extends Component {

  implicit val ec = ExecutionContext.global

  type Props = Unit
  case class State(
    streamID: String, 
    currentUsername: String,
    messages: Seq[Message],
    newMessage: String,
    detailStart: String, 
    detailMessage: String, 
  )

  def initialState: State = State("123412341234", "", Seq.empty, "", "START", "Start my own stream.")

  val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value
  val streamRoute = document.getElementById("streamRoute").asInstanceOf[html.Input].value
  val sendMessageRoute = document.getElementById("sendMessageRoute").asInstanceOf[html.Input].value
  val getMessagesRoute = document.getElementById("getMessagesRoute").asInstanceOf[html.Input].value

  val currentUsername = document.getElementById("currentUser").asInstanceOf[html.Input].value
  val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value

  override def componentDidMount(): Unit = {
    setState(state.copy(currentUsername=currentUsername))
    getMessages(state.streamID)
  }

  def render(): ReactElement = {
    div (className := "stream-page") (
      p (id := "stream-title", className := "stream-title") (
        "Stream Your Face"
      ),
      div (id := "stream-container", className := "stream-container") ( 
        button (id := "settings-button", className := "settings-button", onClick := (_ => { logout() })) (
          //settings button
          "Logout"
        ),
        label (id := "streamuser-label", className := "streamuser-label") (
          "Current User: " + state.currentUsername 
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
            state.detailMessage //"I dont have an account" label
          ),
          button (id := "start-stream-container-button", className := "start-stream-container-button", onClick := (_ => { toggleStream() })) (
            state.detailStart
          ),
        ),
      ),
      div (id := "stream-messages-container-bottom", className := "stream-messages-container-bottom") (
        div (id := "stream-messages-table-container", className := "stream-messages-table-container") (
          label (id := "stream-messages-table-label", className := "stream-messages-table-label") (
            "Messages"
          ),
          table (id := "stream-messages-table", className := "stream-messages-table") (
            thead(id := "table-head") ( 
              tr() (
                td("Message"),
                td("From"),
                td("Time"),
              ),
            ),
            tbody(id := "table-body") (
              state.messages.zipWithIndex.map {
                case (message, index) => {
                  tr(key := index.toString()) (
                    td(message.message),
                    td(message.from),
                    td(message.time),
                  ),
                }
              }
            ),
          ),
        ),
        div (id := "stream-create-messages-container", className := "stream-create-messages-container") (
          label (id := "create-message-title", className := "create-message-title") (
            "Create Message"
          ),
          textarea (id := "create-message-textarea", className := "create-message-textarea", onChange := (_ => { updateFieldStates() })) (
            //message input
          ),
          button (id := "create-message-button", className := "create-message-button", onClick := (_ => {  sendMessage() })) (
            "SEND"
          ),
        ) ,
      ),
    )
  }  

  def updateFieldStates() {

    val message = document.getElementById("create-message-textarea").asInstanceOf[html.TextArea].value
    setState(state.copy(newMessage = message))

  }

  def logout() {
    window.location.replace(logoutRoute)
  }

  def getMessages(streamid: String) {

     Fetch.fetch(getMessagesRoute)
      .flatMap(res => res.text())
      .map { data => 
        println(data)
        Json.fromJson[Seq[Message]](Json.parse(data)) match {
          case JsSuccess(messages, path) =>
            println(messages)
            setState(state.copy(messages = messages))
          case e @ JsError(_) =>
            println("Fetch error: " + e)
        }
    }

  }

  def sendMessage() {

    val headers = new Headers()
    headers.set("Content-Type", "application/json")
    headers.set("Csrf-Token", csrfToken)
    val time = new Date().toString()
    val data = Message("123412341234", currentUsername, state.newMessage, time)
    Fetch.fetch(sendMessageRoute, RequestInit(method = HttpMethod.POST, mode = RequestMode.cors, headers = headers, body = Json.toJson(data).toString))
    .flatMap(result => result.text())
    .map { data => 
      Json.fromJson[Boolean](Json.parse(data)) match {
        case JsSuccess(bool,path) =>
          if (bool) {
              window.location.replace(streamRoute)
          } else {
              println("Failed to send message.")
          }
        case e @ JsError(_) => 
          println("Fetch error: " + e)
      }
    }
  }

  def searchStreamID() {

  }

  def toggleStream() {
     if (state.detailStart == "STOP") {
        setState(state.copy(detailStart = "START", detailMessage = "Start my own stream.", streamID = "123412341234"))
      } else {
        setState(state.copy(detailStart = "STOP", detailMessage = "Stop my stream.", streamID = "123412341234"))
      }
  }

  //*****Add functionality******

}
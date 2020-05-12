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
import java.util.Timer
import java.util.TimerTask
// streaming related
import videostreaming.resources.{MediaDevices, MediaTrackSupportedConstraints}
import org.scalajs.dom.experimental.mediastream.{MediaDeviceKind, MediaDeviceInfo, MediaStream, MediaStreamConstraints}
// import org.scalajs.dom.experimental.webrtc._
import org.scalajs.dom.raw.{Event, EventTarget}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.scalajs.js.{Array, Dynamic}
import org.scalajs.dom.raw.WebSocket
import org.scalajs.dom.raw.MessageEvent
import org.scalajs.dom.raw.CloseEvent
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.raw.HTMLVideoElement
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.experimental.webrtc.RTCPeerConnection
import org.scalajs.dom.experimental.webrtc.RTCIceCandidate
import org.scalajs.dom.experimental.webrtc.RTCPeerConnectionIceEvent
import org.scalajs.dom.experimental.webrtc.RTCSessionDescription
import org.scalajs.dom.experimental.webrtc.RTCSessionDescriptionInit
import org.scalajs.dom.experimental.webrtc.RTCSdpType
import org.scalajs.dom.experimental.webrtc.RTCIceCandidateInit


object Conf {
  implicit val ec = ExecutionContext.global

  val constraints = MediaStreamConstraints(video = true, audio = true)
  val mediaDevices: MediaDevices = window.navigator.asInstanceOf[Dynamic].mediaDevices.asInstanceOf[MediaDevices]
  val audioOutputId: Future[Array[MediaDeviceInfo]] = mediaDevices.enumerateDevices().toFuture.map(_.filter(_.kind == MediaDeviceKind.audiooutput))
}


@react class StreamComponent extends Component {

  implicit val ec = ExecutionContext.global

  type Props = Unit
  case class State(
    streamID: String, 
    currentUsername: String,
    searchStreamID: String,
    messages: Seq[Message],
    newMessage: String,
    detailStart: String, 
    detailMessage: String,
    websocket: WebSocket,
    localStream: MediaStream 
  )

  def initialState: State = State("", "", "", Seq.empty, "", "START", "Start my own stream.", null, null)

  val logoutRoute = document.getElementById("logoutRoute").asInstanceOf[html.Input].value
  val streamRoute = document.getElementById("streamRoute").asInstanceOf[html.Input].value
  val sendMessageRoute = document.getElementById("sendMessageRoute").asInstanceOf[html.Input].value
  val getMessagesRoute = document.getElementById("getMessagesRoute").asInstanceOf[html.Input].value

  val currentUsername = document.getElementById("currentUser").asInstanceOf[html.Input].value

  val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value

  val wsRoute = document.getElementById("wsRoute").asInstanceOf[html.Input].value

  def generateStreamId(): Int = {
    val rand = scala.util.Random
    (rand.nextDouble() * 1000000000).toInt
  }

  val streamid = generateStreamId().toString()

  val localPeerConnection = new RTCPeerConnection()

  override def componentDidMount(): Unit = {

    getMessages()
    connectWebSocket()
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
        div (id := "stream-div", className := "stream-div") (
          video (id := "stream-view", className := "stream-view") (

          )
        ),
      ),
      div (id := "stream-messages-container-top", className := "stream-messages-container-top") (
        div (id := "stream-search-container", className := "stream-search-container") (
          input (id := "streamid-input", className := "streamid-input", placeholder := "Enter Stream ID") (
            //stream id input
          ),
          button (id := "streamid-search-button", className := "streamid-search-button", onClick := (_ => { searchStreamID() }), onChange := (_ => { updateFieldStates() })) (
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
          textarea (id := "create-message-textarea", className := "create-message-textarea", value := state.newMessage, onChange := (_ => { updateFieldStates() })) (
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
    val streamSearch = document.getElementById("streamid-input").asInstanceOf[html.Input].value
    setState(state.copy(newMessage = message, searchStreamID = streamSearch))

  }

  def logout() {
    window.location.replace(logoutRoute)
  }

  def getMessages() {

     Fetch.fetch(getMessagesRoute)
      .flatMap(res => res.text())
      .map { data => 
        Json.fromJson[Seq[Message]](Json.parse(data)) match {
          case JsSuccess(messages, path) =>
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
    val data = Message("12", state.currentUsername, state.newMessage, time)
    Fetch.fetch(sendMessageRoute, RequestInit(method = HttpMethod.POST, mode = RequestMode.cors, headers = headers, body = Json.toJson(data).toString))
    .flatMap(result => result.text())
    .map { data => 
      Json.fromJson[Boolean](Json.parse(data)) match {
        case JsSuccess(bool,path) =>
          if (bool) {
              state.websocket.send("new stream message")
              setState(state.copy(newMessage = ""))
          } else {
              println("Failed to send message.")
          }
        case e @ JsError(_) => 
          println("Fetch error: " + e)
      }
    }
  }

  def requestMediaUsage() {

   // Conf.mediaDevices.getUserMedia(Conf.constraints).toFuture.onComplete {
    Conf.mediaDevices.getUserMedia(Conf.constraints).toFuture.onComplete {
      case Success(stream) =>
        
        handleSuccesfulMediaUsage(stream)

        val startStopButton = document.getElementById("start-stream-container-button").asInstanceOf[html.Button]
        startStopButton.onclick = {(me: MouseEvent) => 
            println("Video Ended")
            stream.getVideoTracks().foreach(_.stop())
            stream.getAudioTracks().foreach(_.stop())
        }
      case Failure(ex) => {
        println(s"error getting user media ${ex.toString}")
        endStream()
      }
    }

  }

  def handleSuccesfulMediaUsage(stream: MediaStream): Unit = {

    setState(state.copy(localStream = stream))

    val video = document.getElementById("stream-view").asInstanceOf[HTMLVideoElement]
    video.asInstanceOf[js.Dynamic].srcObject = stream
    video.play()

    handlePeerConnection()

  }

  def handlePeerConnection(): Unit = {
    localPeerConnection.onicecandidate = (event: RTCPeerConnectionIceEvent) => handleIceCanditate(event)
    localPeerConnection.addStream(state.localStream)
  }

  def handleIceCanditate(event: RTCPeerConnectionIceEvent): Unit = {
      state.websocket.send(event.candidate.toString()) 

  }

  def createOffer(): Unit = {
    localPeerConnection.createOffer().toFuture.onComplete{
      case Success(description) =>
        localPeerConnection.setLocalDescription(description)
        println(s"created offer, set it as local description, sending it to $target...")
        state.websocket.send("Offer")
      case Failure(ex) => println(s"error creating offer ${ex.toString}")
    }
  }

  def receiveOffer(): Unit = {
    println(s"received offer from $target, setting as remote description")
    //localPeerConnection.setRemoteDescription(new RTCSessionDescription(RTCSessionDescriptionInit(RTCSdpType.offer, offer.sdp)))
    localPeerConnection.createAnswer().toFuture.onComplete{
      case Success(description) =>
        localPeerConnection.setLocalDescription(description)
        println(s"created answer, set it as local description, sending it to $target...")
        state.websocket.send("Answer")
      case Failure(ex) => println(s"error creating answer ${ex.toString}")
    }
  }

  def receiveAnswer(): Unit = {
    println(s"received answer from $target, setting as remote description")
    //localPeerConnection.setRemoteDescription(new RTCSessionDescription(RTCSessionDescriptionInit(RTCSdpType.answer, answer.sdp)))
  }

  def startStream() {
    requestMediaUsage()
    connectWebSocket()
    setState(state.copy(detailStart = "STOP", detailMessage = "Stop my stream.", streamID = "123412341234"))
  }

  def endStream() {
    val video = document.getElementById("stream-view").asInstanceOf[HTMLVideoElement]
    video.pause()
    video.src = ""
    setState(state.copy(detailStart = "START", detailMessage = "Start my own stream.", streamID = "123412341234"))  

  }

  def toggleStream() {
      if (state.detailStart == "START") {
        startStream()
      } else {
        endStream()
      }
  }

  def searchStreamID() {
      val searchID = state.searchStreamID
      connectWebSocket()
  }

  def connectWebSocket() {
      //val ws = new WebSocket(wsRoute.replace("http","wss")) Remote
      val ws = new WebSocket(wsRoute.replace("http","ws")) 
      setState(state.copy(streamID=streamid, currentUsername=currentUsername, websocket=ws))
      println(ws.protocol)
      println("URL" + ws.url.toString())
      ws.onopen = (oe: Event) => handleOpenedConnection(oe,ws)
      ws.onmessage = (me: MessageEvent) => handleIncomingMessages(me,ws)
      ws.onclose = (ce: CloseEvent) => handleClose(ce,ws)

  }

  def handleClose(ce: CloseEvent, ws: WebSocket): Unit = {
    println("WebSocket closed: " + ce)
  }

  def handleOpenedConnection(oe: Event, ws: WebSocket): Unit = {
    
    println("Opened Connection" + oe.toString())
    setState(state.copy(websocket = ws))
  }

  implicit def pingWebSocketWithTimer(f: () => Unit): TimerTask = {
        return new TimerTask {
          def run() = f()
        }
  }

  def ping(start: Date) { 
    def timerTask() = state.websocket.send("ping") 
    val timer = new Timer()
    timer.schedule(pingWebSocketWithTimer(timerTask),100,10)
  }

  def handleIncomingMessages(me: MessageEvent, ws: WebSocket): Unit = {
      println(me.data)
      if (me.data == "new stream message") {
        getMessages()
      }
  }



  //*****Add functionality******

}
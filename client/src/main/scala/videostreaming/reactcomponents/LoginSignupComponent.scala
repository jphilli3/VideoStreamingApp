package videostreaming.reactcomponents

import videostreaming.models.ReadsAndWrites._
import videostreaming.models.User
import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window
import org.scalajs.dom.ext.Ajax
import play.api.libs.json.Json
import scala.scalajs.js.annotation.JSExportTopLevel
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

@react class LoginSignupComponent extends Component {

  implicit val ec = ExecutionContext.global

  case class Props(login: Boolean)
  case class State(
    username: String,
    password1: String,
    password2: String,
    title: String, 
    detailLabel: String, 
    detailButtonLabel: String, 
    password2hidden: Boolean,
    errorMessage: String
    )

  def initialState: State = {
    if (props.login) {
      State("", "", "", "Login", "I have not created an account.", "SIGNUP", true, "")
    } else {
      State("", "", "", "Signup", "I have not created an account.", "LOGIN", false, "")
    }
  }

  val streamRoute = document.getElementById("streamRoute").asInstanceOf[html.Input].value
  val validateLoginRoute = document.getElementById("validateLoginRoute").asInstanceOf[html.Input].value
  val validateSignupRoute = document.getElementById("validateSignupRoute").asInstanceOf[html.Input].value

  val csrfToken = document.getElementById("csrfToken").asInstanceOf[html.Input].value
  val chevronImage = document.getElementById("chevronImage").asInstanceOf[html.Input].value

  def render(): ReactElement = {
    div (className := "loginsignup-page") (
      p (id := "stream-title", className := "stream-title") (
        "Stream Your Face"
      ),
      div (id := "loginsignup-container", className := "loginsignup-container") (
        label (id := "loginsignup-title", className := "loginsignup-title") (
          state.title
        ),
        input (id := "username-field", className := "loginsignup-field", placeholder := "Username", onChange := (_ => { updateFieldStates() })) (
          //username input
        ), br(),
        input (id := "password1-field", className := "loginsignup-field", placeholder := "Password", `type` := "password", onChange := (_ => { updateFieldStates() })) (
          //password input
        ), br(),
        input (id := "password2-field", className := "loginsignup-field", placeholder := "Password", hidden := state.password2hidden, `type` := "password", onChange := (_ => { updateFieldStates() })) (
          //password input
        ), br(),
        input (id := "error-message", className := "error-message", value := state.errorMessage, readOnly),
        button (id := "loginsignup-button", className := "rounded-button", onClick := (_ => { validateLoginSignupAction() })) (
            img (src := chevronImage)
        ),
      ),
      div (id := "loginsignup-toggle-container", className := "dark-detail-container") ( //toggle loginsignup containter
        label (id := "dark-detail-container-label", className := "dark-detail-container-label") ( 
          state.detailLabel //"I dont have an account" label
        ),
        button (id := "dark-detail-container-button", className := "dark-detail-container-button", onClick := (_ => { loginsignupToggle() })) (
          state.detailButtonLabel
        ),
      )  
    )
  }

  def updateFieldStates() {
    val username = document.getElementById("username-field").asInstanceOf[html.Input].value
    val password1 = document.getElementById("password1-field").asInstanceOf[html.Input].value
    val password2 = document.getElementById("password2-field").asInstanceOf[html.Input].value
    if (state.title == "Signup") {
      setState(state.copy(username = username, password1 = password1, password2 = password2))
    } else {
      setState(state.copy(username = username, password1 = password1))
    }

  }

  def loginsignupToggle() {
     if (state.title == "Signup") {
      setState(state.copy(title = "Login", detailLabel = "I have not created an account.", detailButtonLabel = "SIGNUP", password2hidden = true))
    } else {
      setState(state.copy(title = "Signup", detailLabel = "I have created an account.", detailButtonLabel = "LOGIN", password2hidden = false))
    }
  }

  def validateLoginSignupAction(): Unit = {

    val username = document.getElementById("username-field").asInstanceOf[html.Input].value
    val password1 = document.getElementById("password1-field").asInstanceOf[html.Input].value
    val password2 = document.getElementById("password2-field").asInstanceOf[html.Input].value

    val headers = new Headers()
    headers.set("Content-Type", "application/json")
    headers.set("Csrf-Token", csrfToken)

    val data = User(username,password1)

    if (state.title == "Login") {
        Fetch.fetch(validateLoginRoute, RequestInit(method = HttpMethod.POST, mode = RequestMode.cors, headers = headers, body = Json.toJson(data).toString))
        .flatMap(result => result.text())
        .map { data => 
          Json.fromJson[Boolean](Json.parse(data)) match {
            case JsSuccess(bool,path) =>
              if (bool) {
                  window.location.replace(streamRoute)
              } else {
                  setState(state.copy(errorMessage = "Invalid username/password."))
              }
            case e @ JsError(_) => 
              println("Fetch error: " + e)
          }
        }
      } else {
        if (password1 == password2) {
          Fetch.fetch(validateSignupRoute, RequestInit(method = HttpMethod.POST, mode = RequestMode.cors, headers = headers, body = Json.toJson(data).toString))
          .flatMap(result => result.text())
          .map { data => 
            Json.fromJson[Boolean](Json.parse(data)) match {
              case JsSuccess(bool,path) =>
                if (bool) {
                    window.location.replace(streamRoute);
                } else {
                    setState(state.copy(errorMessage = "Username already exists."))
                }
              case e @ JsError(_) => 
                println("Fetch error: " + e)
            }
          }
        } else {
          setState(state.copy(errorMessage = "Passwords do not match."))
        }
      }
  }

}
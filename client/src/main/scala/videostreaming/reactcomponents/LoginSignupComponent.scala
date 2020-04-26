package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.document
import org.scalajs.dom.html
import slinky.web.svg.view
import org.scalajs.dom.window

@react class LoginSignupComponent extends Component {
  type Props = Unit
  case class State(title: String, detailLabel: String, detailButtonLabel: String, password2hidden: Boolean)

  def initialState: State = State("Login", "I have not created an account.", "SIGNUP", true)

  val streamRoute = document.getElementById("streamRoute").asInstanceOf[html.Input].value

  def render(): ReactElement = {
    div (className := "loginsignup-page") (
      p (id := "stream-title", className := "stream-title") (
        "Stream Your Face"
      ),
      div (id := "loginsignup-container", className := "loginsignup-container") (
        label (id := "loginsignup-title", className := "loginsignup-title") (
          state.title
        ),
        input (id := "username-field", className := "loginsignup-field", placeholder := "Username") (
          //username input
        ), br(),
        input (id := "password1-field", className := "loginsignup-field", placeholder := "Password") (
          //password input
        ), br(),
        input (id := "password2-field", className := "loginsignup-field", placeholder := "Password", hidden := state.password2hidden) (
          //password input
        ), br(),
        button (id := "loginsignup-button", className := "rounded-button", onClick := (_ => { loginsignupAction() })) (
          //login/signup button
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

  def loginsignupToggle() {
     if (state.title == "Signup") {
      setState(state.copy(title = "Login", detailLabel = "I have not created an account.", detailButtonLabel = "SIGNUP", password2hidden = true))
    } else {
      setState(state.copy(title = "Signup", detailLabel = "I have created an account.", detailButtonLabel = "LOGIN", password2hidden = false))
    }
  }

  def loginsignupAction() {
      window.location.replace(streamRoute)
  }
}
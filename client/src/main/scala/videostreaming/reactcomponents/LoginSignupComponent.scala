package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class LoginSignupComponent extends Component {
  type Props = Unit
  case class State(title: String, detail: String)

  def initialState: State = State("Login", "I have not created an account.")

  def render(): ReactElement = {
    div (
      p (id := "stream-title", className := "stream-title") (
        "Stream Your Face"
      ),
      div (id := "loginsignup-container", className := "loginsignup-container") (
        p (id := "loginsignup-title", className := "loginsignup-title") (
          initialState.title
        ),
        input (id := "username-field", className := "loginsignup-field", placeholder := "Username") (
          //username input
        ),
        input (id := "password1-field", className := "loginsignup-field", placeholder := "Password") (
          //password input
        ),
        input (id := "password2-field", className := "loginsignup-field", placeholder := "Password") (
          //password input
        ),
        button (id := "loginsignup-button", className := "rounded-button", onClick := (_ => { loginsignupAction(true) })) (
          //login/signup button
        ),
      ),
      div (id := "loginsignup-toggle-container", className := "dark-detail-container") ( //toggle loginsignup containter
        p (id := "dark-detail-container-label", className := "dark-detail-container-label") ( 
          initialState.detail //"I dont have an account" label
        ),
        button (id := "dark-detail-container-button", className := "dark-detail-container-button", onClick := (_ => { loginsignupToggle() })) (
          initialState.title.toUpperCase()
        ),
      )  
    )
  }

  def loginsignupToggle() {
    println("loginsignup toggle.")
  }

  def loginsignupAction(login: Boolean) {
    println("loginsignup clicked.")
  }
}
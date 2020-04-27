package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._
import org.scalajs.dom.document
import org.scalajs.dom.html
import org.scalajs.dom.window

@react class LoginSignupComponent extends Component {
  case class Props(login: Boolean)
  case class State(title: String, detailLabel: String, detailButtonLabel: String, password2hidden: Boolean)

  def initialState: State = {
    if (props.login) {
      State("Login", "I have not created an account.", "SIGNUP", true)
    } else {
      State("Signup", "I have not created an account.", "LOGIN", false)
    }
  }

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
            img (src := document.getElementById("chevronImage").asInstanceOf[html.Input].value)
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

  // import slinky.web.svg._

  // def createSVG(): ReactElement = {
  //     svg (width := "19px", height := "30px", viewBox := "0 0 19 30", version := "1.1", xmlns := "http://www.w3.org/2000/svg", xmlnsXlink := "http://www.w3.org/1999/xlink") (
  //         defs (
  //             polygon(id := "path-1", points := "25 15 21.475 18.525 32.925 30 21.475 41.475 25 45 40 30")
  //         ),
  //         g (id := "Material-Light-Theme-🌕", stroke := "none", strokeWidth := "1", fill := "none", fillRule := "evenodd") (
  //           g (id := "Login-View", transform := "translate(-1161.000000, -644.000000)") (
  //             rect (fill := "#FFFFFF", x := "0", y := "0", width := "1440", height := "1024"),
  //             g (id := "icon/navigation/chevron_right_24px", transform := "translate(1140.000000, 629.000000)") (
  //               mask (id := "mask-2", fill := "white") (
  //                 use (xlinkHref := "#path-1") (),
  //               ),
  //               use (id := "-↳Color", fill := "#000000", fillRule := "evenodd", xmlnsXlink := "#path-1"),
  //             ),
  //         ),
  //       ),
  //     ),
  // }

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
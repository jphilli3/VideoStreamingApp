package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class LoginSignupComponent extends Component {
  type Props = Unit
  case class State(text: String)

  def initialState: State = State("")

  def render(): ReactElement = {
    div (
      h3 (
        //view title
      ),
      div ( //loginsignup container
        h1 (
          // Login/Signup Title
        ),
        input (
          //username input
        ),
        input (
          //password input
        ),
        button (
          //login/signup button
        ),
      ),
      div ( //toggle loginsignup containter
        p (
          //"I dont have an account" label
        ),
        button (
          //toggle button
        ),
      )  
    )
  }
}
package videostreaming.reactcomponents

import slinky.core.annotations.react
import slinky.core.Component
import slinky.core.facade.ReactElement
import slinky.web.html._

@react class StreamComponent extends Component {
  type Props = Unit
  case class State(text: String)

  def initialState: State = State("")

  def render(): ReactElement = {
    div (
      h1(
        //view title
      ),
      div ( //stream containter 
        button (
          //settings button
        ),
        h3 (
          //stream id
        ),
        canvas (
          //stream video view
        ),
      ),
      div ( //message container
        input (
          //stream id input
        ),
        button (
          //stream id search
        ),
        div ( //start stream container
          h5 ( 
            //start stream label
          ),
          button (
            //start stream button
          ),
        ),
        div ( //message display container
          h5 (
            //messages title
          ),
          table (
            //all messages table
          ),
        ),
        div ( //create message container
          h6 (
            //create message title
          ),
          input (
            //message input
          ),
          button (
            //send button
          )
        )
      )
    )
  }

  //*****Add functionality******


}
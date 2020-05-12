package videostreaming.shared

object WebRtcProtocol {

  case class User(username: String)

  sealed trait WebRtcMessage

  case class ConnectSuccess(username: String) extends WebRtcMessage

  case class JoinStream(user: User, streamid: String) extends WebRtcMessage
  case class LeaveStream(user: User, streamid: String) extends WebRtcMessage

  case class JoinSuccess(others: Seq[User]) extends WebRtcMessage
  case class LeaveSuccess(user: User) extends WebRtcMessage
  case class Disconnect(userId: String) extends WebRtcMessage

  case class Offer(source: User, target: User, sdp: String) extends WebRtcMessage
  case class Answer(source: User, target: User, sdp: String) extends WebRtcMessage
  case class IceCandidate(source: User, target: User, candidate: String, sdpMid: String, sdpMLineIndex: Double) extends WebRtcMessage

}
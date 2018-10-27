package ai.bale.distributedKeyValue

import ai.bale.Helper.ExtendedString
import ai.bale.protos.keyValue._

final case class States(received: Map[String, Any] = Map()) {

  implicit def String2ExtendedString(s: String): ExtendedString = new ExtendedString(s)
  def add(msg: SetRequest): States = copy( received + (msg.key -> msg.value))

  def remove(msg: RemoveRequest): States = {
    if (received.contains(msg.key)) copy(received - msg.key)
    else copy(received)
  }

  def get(msg: GetRequest): GetReply = {
    if (received.contains(msg.key)) GetReply(Some(received(msg.key).toString))
    else GetReply(None)
  }

  def increase(msg: IncreaseRequest): States = {
    if (received.contains(msg.key)) {
      if (received(msg.key).toString.isNumber) {
        val newValue = received(msg.key).toString.toInt + 1
        remove(RemoveRequest(msg.key)).add(SetRequest(msg.key, newValue.toString))
      }
      else copy(received)
    }
    else copy(received)
  }

  override def toString: String = received.toString
}
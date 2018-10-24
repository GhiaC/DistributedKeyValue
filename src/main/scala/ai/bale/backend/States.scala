package backend

import backend.Helper.ExtendedString
import messages.{FailedJob, _}
import ai.bale.protos.keyValue._

final case class States(received: Map[String, Any] = Map()) {
  implicit def String2ExtendedString(s: String): ExtendedString = new ExtendedString(s)

  def add(msg: SetRequest): States = copy(received + (msg.key -> msg.value))

  def remove(msg: RemoveRequest): States = {
    if (received.contains(msg.key)) copy(received - msg.key)
    else copy(received)
  }

  def getItem(msg: GetRequest): ResultMessage = {
    if (received.contains(msg.key)) SuccessJob(received(msg.key).toString)
    else FailedJob("invalid key")
  }

  def increase(msg: IncreaseRequest): States = {
    if (received.contains(msg.key)) {
      if (received(msg.key).toString.isNumber) {
        val newValue = received(msg.key).toString.toInt + 1
        remove(RemoveRequest(msg.key)).add(SetRequest(msg.key, newValue toString))
      } else
        copy(received)
    } else
      copy(received)
  }

  def getAll: ResultMessage = {
    SuccessJob(received.toString())
  }

  override def toString: String = received.toString
}

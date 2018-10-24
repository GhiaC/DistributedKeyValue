package backend

import ai.bale.inter.Helper.ExtendedString
import messages.{FailedJob, _}

object Snapshot {
  implicit def String2ExtendedString(s: String): ExtendedString = new ExtendedString(s)

  final case class States(received: Map[String, Any] = Map()) {
    def add(msg: Set): States = copy(received + (msg.key -> msg.value))

    def remove(msg: Remove): States = {
      if (received.contains(msg.key)) copy(received - msg.key)
      else copy(received)
    }

    def getItem(msg: GetItem): ResultMessage = {
      if (received.contains(msg.key)) SuccessJob(received(msg.key).toString)
      else FailedJob("invalid key")
    }

    def increase(msg: Increase): States = {
      if (received.contains(msg.key)) {
        if (received(msg.key).toString.isNumber) {
          val newValue = received(msg.key).toString.toInt + 1
          remove(Remove(msg.key)).add(Set(msg.key, newValue))
        } else
          copy(received)
      } else
        copy(received)
    }

    def getAll: ResultMessage = {
      SuccessJob(received.toString())
    }

    override def toString: String

    = received.toString
  }

}

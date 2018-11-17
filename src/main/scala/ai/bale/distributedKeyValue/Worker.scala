package ai.bale.distributedKeyValue

import ai.bale.Helper.ExtendedString
import akka.actor._
import akka.persistence.{PersistentActor, SnapshotOffer}
import ai.bale.protos.keyValue._

import scala.collection.mutable

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  //  var map = mutable.HashMap.empty[String, Any]

  var map: mutable.TreeMap[String, Any] = mutable.TreeMap()

  implicit def String2ExtendedString(s: String): ExtendedString = new ExtendedString(s)

  def receiveCommand: Receive = {
    case _: SnapshotRequest =>
      saveSnapshot(map)
      sender() ! Ack("Success")

    case msg: SetRequest =>
      val replyTo = sender()
      persistAsync(msg) { msg =>
        map += (msg.key -> msg.value)
        replyTo ! Ack("Added")
      }

    case msg: RemoveRequest =>
      val replyTo = sender()
      persistAsync(msg) { msg =>
        map.remove(msg.key)
        replyTo ! Ack("Removed")
      }

    case msg: IncreaseRequest =>
      val replyTo = sender()
      persistAsync(msg) { msg =>
        map.getOrElse(msg.key, None) match {
          case None =>
            replyTo ! IncreaseReply(None)
          case oldValue =>
            if (oldValue.toString.isNumber) {
              val newValue = oldValue.toString.toInt + 1
              map += (msg.key -> newValue)
              replyTo ! IncreaseReply(Some(newValue.toString))
            } else
              replyTo ! IncreaseReply(Some(oldValue.toString))
        }
      }

    case msg: GetRequest =>
      val replyTo = sender()
      map.getOrElse(msg.key, None) match {
        case None =>
          replyTo ! GetReply(None)
        case reply =>
          replyTo ! GetReply(Some(reply.toString))
      }
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: mutable.TreeMap[String, Any]) => map = s
    case msg: SetRequest => map += (msg.key -> msg.value)
    case msg: RemoveRequest => map.remove(msg.key)
    case msg: IncreaseRequest =>
      map.getOrElse(msg.key, None) match {
        case oldValue =>
          if (oldValue.toString.isNumber) {
            val newValue = oldValue.toString.toInt + 1
            map += (msg.key -> newValue)
          }
      }
  }
}

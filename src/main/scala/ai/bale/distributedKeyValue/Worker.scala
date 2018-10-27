package ai.bale.distributedKeyValue

import akka.actor._
import akka.persistence.{PersistentActor, SnapshotOffer}
import ai.bale.protos.keyValue._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  private var states = States()

  def receiveCommand: Receive = {
    case SnapshotRequest => saveSnapshot(states)

    case msg: SetRequest =>
      val replyTo = sender()
      persist(msg) { msg =>
        states = states.add(msg)
        replyTo ! Ack("Added")
      }

    case msg: RemoveRequest =>
      val replyTo = sender()
      persist(msg) { msg =>
        states = states.remove(msg)
        replyTo ! Ack("Removed")
      }

    case msg: IncreaseRequest =>
      val replyTo = sender()
      persist(msg) { m =>
        states = states.increase(msg)
        replyTo ! IncreaseReply(states.get(GetRequest(msg.key)).result)
      }

    case msg: GetRequest =>
      val replyTo = sender()
      replyTo ! states.get(GetRequest(msg.key))
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) =>
      context.system.log.info("offered state = " + s)
      states = s
    case msg: SetRequest => states = states.add(msg)
    case msg: RemoveRequest => states = states.remove(msg)
    case msg: IncreaseRequest => states = states.increase(msg)
  }
}

package backend

import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import ai.bale.protos.keyValue._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  private var states = new States()

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + states)
    case "snap" => saveSnapshot(states)
    case msg: SetRequest =>
      val replyTo = sender()
      persist(msg) { m =>
        states = states.add(msg)
        replyTo ! Ack("Added")
      }

    case msg: RemoveRequest =>
      val replyTo = sender()
      persist(msg) { m =>
        states = states.remove(msg)
        replyTo ! Ack("Removed")
      }

    case msg: IncreaseRequest =>
      val replyTo = sender()
      persist(msg) { m =>
        states = states.increase(msg)
        replyTo ! Ack("Success")
      }

    case msg: GetRequest =>
      val replyTo = sender()
      states.getItem(msg) match {
        case Some(msg) => replyTo ! msg
        case _ => replyTo ! new Exception("invalid key!")
      }

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

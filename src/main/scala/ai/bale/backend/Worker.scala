package backend

import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import messages._
import ai.bale.protos.keyValue._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  private var states = new States()

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + states)
    case "snap" => saveSnapshot(states)
    case msg: SetRequest =>
      persist(msg) { m =>
        states = states.add(msg)
        sender() ! SuccessJob("Added")
      }

    case msg: RemoveRequest =>
      persist(msg) { m =>
        states = states.remove(msg)
        sender() ! SuccessJob("Removed")
      }

    case msg: IncreaseRequest =>
      persist(msg) { m =>
        states = states.increase(msg)
        sender() ! SuccessJob("Success")
      }

    case msg: GetRequest => sender() ! states.getItem(msg)

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

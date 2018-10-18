package backend

import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import backend.Snapshot.States
import messages._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  var states = States()

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + states)
    case "snap" => saveSnapshot(states)
    case SaveSnapshotSuccess(metadata) => // ...
    case SaveSnapshotFailure(metadata, reason) => // ...
    case msg: Set =>
      persist(msg) { m =>
        states = states.add(Set(m.key, m.value))
      }
      sender() ! SuccessJob("Added")

    case msg: Remove =>
      persist(msg) { m =>
        states = states.remove(Remove(m.key))
      }
      sender() ! SuccessJob("Removed")

    case msg: Increase =>
      persist(msg) { m =>
        states = states.increase(Increase(m.key))
      }
      sender() ! SuccessJob("Success")

    case msg: GetItem => sender() ! states.getItem(GetItem(msg.key))

    case GetAll => sender() ! states.getAll
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) =>
      context.system.log.info("offered state = " + s)
      states = s

    case msg: Set => states = states.add(Set(msg.key, msg.value))

    case msg: Remove => states = states.remove(Remove(msg.key))
  }
}

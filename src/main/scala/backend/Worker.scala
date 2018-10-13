package backend

import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import backend.Snapshot.States
import messages._

import scala.util.Random

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = "PersistenceId-" + new Random().nextInt(1000)

  val persist = new Persist

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + persist.state)
    case "snap" => saveSnapshot(persist.state)
    case SaveSnapshotSuccess(metadata) => // ...
    case SaveSnapshotFailure(metadata, reason) => // ...
    case msg: Set =>
      persist(msg) { m =>
        persist.state = persist.state.updatedAdd(Set(m.key, m.value))
      }
      sender() ! SuccessJob("Added")

    case msg: Remove =>
      persist(msg) { m =>
        persist.state = persist.state.updatedRemove(Remove(m.key))
      }
      sender() ! SuccessJob("Removed")

    case msg: GetItem => sender() ! persist.state.getItem(GetItem(msg.key))

    case GetAll => sender() ! persist.state.getAll
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) =>
      context.system.log.info("offered state = " + s)
      persist.state = s

    case msg: Set => persist.state = persist.state.updatedAdd(Set(msg.key, msg.value))

    case msg: Remove => persist.state = persist.state.updatedRemove(Remove(msg.key))
  }
}

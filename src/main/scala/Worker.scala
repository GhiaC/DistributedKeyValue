import Snapshot.States
import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import messages._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = "PersistenceId-" + self.path.name

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + Persist.state)
    case "snap" => saveSnapshot(Persist.state)
    //    case SaveSnapshotSuccess(metadata) => // ...
    //    case SaveSnapshotFailure(metadata, reason) => // ...
    case msg: messages.Set =>
      persist(msg) { m =>
        Persist.state = Persist.state.updatedAdd(messages.Set(m.key, m.value))
      }
      sender() ! SuccessJob("Added")

    case msg: Remove =>
      persist(msg) { m =>
        Persist.state = Persist.state.updatedRemove(Remove(m.key))
      }
      sender() ! SuccessJob("Removed")

    case msg: GetItem => sender() ! Persist.state.getItem(GetItem(msg.key))

    case GetAll => sender() ! Persist.state.getAll
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) =>
      context.system.log.info("offered state = " + s)
      Persist.state = s

    case msg: messages.Set => Persist.state = Persist.state.updatedAdd(messages.Set(msg.key, msg.value))

    case msg: Remove => Persist.state = Persist.state.updatedRemove(Remove(msg.key))
  }
}

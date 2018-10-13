import Snapshot.States
import akka.actor._
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import messages._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = "PersistenceId-" + self.path.name

//  println(self.path.toString)

  def receiveCommand: Receive = {
    case "print" => println("current state = " + Persist.state)
    case "snap" => saveSnapshot(Persist.state)
    case SaveSnapshotSuccess(metadata) => // ...
    case SaveSnapshotFailure(metadata, reason) => // ...
    case msg: Set =>
      persist(msg) { m =>
        Persist.state = Persist.state.updatedAdd(Set(m.key, m.value))
      }
      sender() ! SuccessJob("Added")

    case msg: Remove =>
      persist(msg) { m =>
        Persist.state = Persist.state.updatedRemove(Remove(m.key))
      }
      sender() ! SuccessJob("Removed")

    case msg: GetItem =>
      sender() ! SuccessJob(Persist.state.getItem(GetItem(msg.key)).toString)
    //      context.system.log.info(Shard.state.getItem(G(msg.key)))

    case GetAll =>
      sender() ! SuccessJob(Persist.state.getAll.toString)
    //      context.system.log.info(Shard.state.getAll)
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) =>
      context.system.log.info("offered state = " + s)
      Persist.state = s
    case msg: Set =>
      Persist.state = Persist.state.updatedAdd(Set(msg.key, msg.value))
    case msg: Remove =>
      Persist.state = Persist.state.updatedRemove(Remove(msg.key))
  }
}

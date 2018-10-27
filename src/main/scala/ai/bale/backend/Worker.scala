package ai.bale.backend

import akka.actor._
import akka.persistence.{PersistentActor, SnapshotOffer}
import ai.bale.protos.keyValue._

class Worker extends PersistentActor with ActorLogging {

  def persistenceId: String = context.self.path.toString

  private var states = States()

  def receiveCommand: Receive = {
    case "print" => context.system.log.info("current state = " + states)
    case "snap" => saveSnapshot(states)
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
        states.get(GetRequest(msg.key)) match {
          case Some(replyMessage) => replyTo ! IncreaseReply(replyMessage.result)
          case _ => replyTo ! new Exception("invalid key!")
        }
      }

    case msg: GetRequest =>
      val replyTo = sender()
      states.get(msg) match {
        case Some(replyMessage) => replyTo ! replyMessage
        case _ => replyTo ! new Exception("invalid key!")
      }
  }

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _: IllegalArgumentException ⇒ SupervisorStrategy.Resume
    case _: ActorInitializationException ⇒ SupervisorStrategy.Stop
    case _: DeathPactException ⇒ SupervisorStrategy.Stop
    case _: Exception ⇒ SupervisorStrategy.Restart
  }

  def receiveRecover: Receive = {
    case SnapshotOffer(_, s: States) => context.system.log.info("offered state = " + s)
    case msg: SetRequest => states = states.add(msg)
    case msg: RemoveRequest => states = states.remove(msg)
    case msg: IncreaseRequest => states = states.increase(msg)
  }
}

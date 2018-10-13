import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.event.LoggingAdapter
import messages.{OperatorMessage, RestartMeException, ResumeMeException, StopMeException}

class Supervisor extends Actor {

  val log: LoggingAdapter = context.system.log

  override def preStart(): Unit = {
    log.info("The Supervisor is ready to supervise")
  }

  override def postStop(): Unit = log.info("Bye Bye from the Supervisor")

  override def supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
    case _: RestartMeException => Restart
    case _: ResumeMeException => Resume
    case _: StopMeException => Stop
  }

  val SuperWorker: ActorRef = context.actorOf(Props(new ClusterMember), "SuperWorker")

  override def receive: Receive = {
    case msg: OperatorMessage =>
      SuperWorker forward msg
    case elseMessage =>
      log.warning(elseMessage.toString)
  }
}

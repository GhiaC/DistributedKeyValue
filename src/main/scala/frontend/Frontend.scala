package frontend

import java.util.concurrent.TimeUnit

import ai.bale.inter.Helper
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.sharding.ClusterSharding
import akka.pattern._
import akka.util.Timeout
import messages._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Success

object Frontend {
  def main(args: Array[String]): Unit = {
    try {
      implicit val system: ActorSystem = ActorSystem("ClusterSystem", Helper.createConfig(2552, "frontend", "frontend"))
      implicit val actor: ActorRef = system.actorOf(Props(classOf[Frontend]), "Client")
      implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)
      implicit val ec: ExecutionContextExecutor = system.dispatcher

      implicit def printer(m: Any): Unit = system.log.info(m.toString)

      getCommand()
    }
    catch {
      case msg: Exception =>
        println(msg)
    }
  }

  @scala.annotation.tailrec
  def getCommand()(implicit actor: ActorRef, timeout: Timeout, printer: Any => Unit, ec: ExecutionContextExecutor): Unit = {
    val console = scala.io.StdIn.readLine()
    (actor ? Helper.commandToOperatorMessage(console)).mapTo[ResultMessage] onComplete {
      case Success(value) => printer(value.toString)
      case e => printer(e.toString)
    }
    getCommand()
  }
}

class Frontend extends Actor {
  val cluster = ClusterSharding(context.system)

  var backendNodes: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef] //TODO refactor to fp

  def printer(m: Any): Unit = context.system.log.info(m.toString)

  import java.util.concurrent.TimeUnit

  implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  def receive: PartialFunction[Any, Unit] = {
    case result: ResultMessage => //debug //useless
      printer(result)

    case BackendRegistration if !backendNodes.contains(sender()) =>
      context watch sender()
      backendNodes = backendNodes :+ sender()
      printer("{} Added", sender())

    case Terminated(a) =>
      backendNodes = backendNodes.filterNot(_ == a)
      printer("{} Terminated", sender())

    case _: OperatorMessage if backendNodes.isEmpty =>
      sender() ! FailedJob("Service unavailable, try again later")

    case msg: OperatorMessage =>
      forwardAndSharing(msg, sender())
  }

  var jobCounter = 0 // ???

  def forwardAndSharing(msg: OperatorMessage, replyTo: ActorRef
                        , previousValue: ResultMessage = SuccessJob("")
                        , counter: Int = backendNodes.size): Future[Any] = {
    jobCounter += 1
    (backendNodes(jobCounter % backendNodes.size) ? msg).mapTo[ResultMessage] map {
      case currentValue: SuccessJob =>
        if ((currentValue == previousValue) && counter == 0)
          replyTo ! previousValue
        else if (counter != 0)
          forwardAndSharing(msg, replyTo, currentValue, counter - 1)
        else
          replyTo ! FailedJob("values not equal")
      case e: FailedJob =>
        replyTo ! e
    } recover {
      case e: Throwable =>
        replyTo ! FailedJob(counter + " node is not available " + e)
    }
  }
}
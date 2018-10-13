import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.sharding.ClusterSharding
import akka.util.Timeout
import messages._
import scala.concurrent.ExecutionContextExecutor

object Frontend {
  def main(args: Array[String]): Unit = {
    try {
      val system = ActorSystem("ClusterSystem", Helper.createConfig(2552, "frontend", "frontend"))
      val actor = system.actorOf(Props(classOf[Frontend]), "Client")

      while (true) { // TODO transfer to tailRecursive
        val console = scala.io.StdIn.readLine()
        val operator = console.split(" ")
        if (operator(0) == "get" && operator.length == 2) {
          actor ! GetItem(operator(1))
        } else if (operator(0) == "set" && operator.length == 3) {
          actor ! Set(operator(1), operator(2))
        } else if (operator(0) == "remove" && operator.length == 2) {
          actor ! Remove(operator(1))
        } else if (operator(0) == "getall" && operator.length == 1) {
          actor ! GetAll
        } else {
          actor ! console
        }
      }
    } catch {
      case msg: Exception =>
        println(msg)
    }

  }
}

class Frontend extends Actor {
  val cluster = ClusterSharding(context.system)

  var backendNodes: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef] //TODO transfer to fp
  var jobCounter = 0

  def printer(m: Any): Unit = context.system.log.info(m.toString)

  import java.util.concurrent.TimeUnit

  import akka.pattern._

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  def receive: PartialFunction[Any, Unit] = {
    case _: OperatorMessage if backendNodes.isEmpty =>
      sender() ! FailedJob("Service unavailable, try again later")
      printer(FailedJob("Service unavailable, try again later")) //TODO remove

    case msg: OperatorMessage =>
      jobCounter += 1
      val service = backendNodes(jobCounter % backendNodes.size)
      val result = service.?(msg)(timeout).mapTo[ResultMessage]
      result onComplete { value => //TODO needs refactor
        printer(value.toString)
      }


    case BackendRegistration if !backendNodes.contains(sender()) =>
      context watch sender()
      backendNodes = backendNodes :+ sender()
      printer("{} Added", sender())

    case Terminated(a) =>
      backendNodes = backendNodes.filterNot(_ == a)
      printer("{} Terminated", sender())

    case msg =>
      printer(msg.toString)
  }
}

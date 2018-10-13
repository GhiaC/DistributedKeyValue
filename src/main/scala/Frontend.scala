import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.cluster.sharding.ClusterSharding
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import messages._

import scala.concurrent.ExecutionContextExecutor

object Frontend {
  def main(args: Array[String]): Unit = {

    try {
      val system = ActorSystem("ClusterSystem", createConfig(2552, "frontend", "frontend"))
      val actor = system.actorOf(Props(classOf[Frontend]), "Client")

      while (true) {
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

  def createConfig(port: Int, role: String, resources: String): Config = {
    ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """)
      .withFallback(
        ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
      withFallback(ConfigFactory.load(resources))
  }

}

class Frontend extends Actor {
  //  val servicePathElements: immutable.Seq[String] = servicePath match {
  //    case RelativeActorPath(elements) => elements
  //    case _ => throw new IllegalArgumentException(
  //      "servicePath [%s] is not a valid relative actor path" format servicePath)
  //  }
  //  val service = context.actorSelection(RootActorPath(address) / servicePathElements)

  val cluster = ClusterSharding(context.system)

  var backendNodes: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef] //TODO transfer to fp
  var jobCounter = 0

  def printer(m: Any): Unit = println(m)

  import java.util.concurrent.TimeUnit

  import akka.pattern._

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  //context.system.log.info
  def receive: PartialFunction[Any, Unit] = {
    case _: OperatorMessage if backendNodes.isEmpty =>
      //      sender() ! FailedJob("Service unavailable, try again later")
      println(FailedJob("Service unavailable, try again later"))

    case msg: OperatorMessage =>
      jobCounter += 1
      val service = backendNodes(jobCounter % backendNodes.size)
      val result = service.?(msg)(timeout).mapTo[ResultMessage]
      result onComplete { value =>
        println(value)
      }


    case BackendRegistration if !backendNodes.contains(sender()) =>
      context watch sender()
      backendNodes = backendNodes :+ sender()
      println(sender(), " Added")

    case Terminated(a) =>
      backendNodes = backendNodes.filterNot(_ == a)
      println(sender(), " Terminated")

    case msg =>
      printer(msg.toString)
  }
}

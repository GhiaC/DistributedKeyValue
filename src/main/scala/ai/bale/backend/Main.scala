package backend

import ai.bale.Helper
import ai.bale.protos.keyValue._
import akka.actor._
import io.grpc.ServerBuilder
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.{ExecutionContext, Future}

object Main {
  def main(args: Array[String]): Unit = {
    Seq(2551, 2554, 2557, 2560) foreach { port =>
      setupNode("ClusterSystem", port)
    }
  }
  
  def setupNode(actorName: String, port: Int): Unit = {
    implicit val system = ActorSystem(actorName, Helper.createConfig(port, "backend", "application"))
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val listener = system.actorOf(Props[DeadActorListener], "deadLetters")
    system.eventStream.subscribe(listener, classOf[UnhandledMessage])
    system.eventStream.subscribe(listener, classOf[DeadLetter])
    Future {
      val worker: ActorRef = ClusterExtension(system).shardRegion
      ServerBuilder.forPort(port + 100).
        addService(KeyValueGrpc.bindService(new KeyValueImpl(worker), ec)).build.start
      system.log.info("Started port {}", port)
    }
  }
}
package backend

import ai.bale.Helper
import ai.bale.protos.keyValue._
import akka.actor
import akka.actor.{ActorSystem, Props}
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContextExecutor


object Main {
  def main(args: Array[String]): Unit = {
    Seq(2551, 2554) foreach { port =>
      setupNode("ClusterSystem", port)
      Thread.sleep(1000)
    }
  }

  def setupNode(actorName: String, port: Int): Unit = {
    val system = ActorSystem(actorName, Helper.createConfig(port, "backend","application"))
//    val migration = EventsByTagMigration(system)
//    migration.createTables()
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val worker: actor.ActorRef = system.actorOf(Props[ClusterMember], "ClusterMember")
    ServerBuilder.forPort(port + 100).
      addService(KeyValueGrpc.bindService(new KeyValueImpl(worker), ec)).build.start
    system.log.info("Started port {}", port)
  }
}
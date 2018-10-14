package backend

import ai.bale.inter.Helper
import akka.actor.{ActorSystem, Props}
import akka.persistence.cassandra.EventsByTagMigration

object Backend {
  def main(args: Array[String]): Unit = {
    Seq(0, 0) foreach { port =>
      setupNode("ClusterSystem", port)
    }
  }

  def setupNode(actorName: String, port: Int): Unit = {
    try {
      val system = ActorSystem(actorName, Helper.createConfig(port, "backend", "backend"))
      val migration = EventsByTagMigration(system)
      migration.createTables()
      system.log.info("Started port {}", port)
      system.actorOf(Props[Supervisor], "Worker")
    } catch {
      case ex: Exception =>
        println(ex) //????
    }
  }
}

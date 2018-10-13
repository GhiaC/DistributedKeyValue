import akka.actor.{ActorSystem, Props}
import akka.persistence.cassandra.EventsByTagMigration
import com.typesafe.config.{Config, ConfigFactory}

object Backend {
  def main(args: Array[String]): Unit = {
    Seq(0, 0) foreach { port =>
      setupNode("ClusterSystem", port)
    }
  }

  def setupNode(actorName: String, port: Int): Unit = {
    try {
      val system = ActorSystem(actorName, createConfig(port, "backend", "backend"))
      val migration = EventsByTagMigration(system)
      migration.createTables()
      system.log.info("Started port {}", port)
      system.actorOf(Props[ClusterMember], "Worker")
    } catch {
      case ex: Exception =>
        println(ex) //????
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

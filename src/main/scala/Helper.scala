import com.typesafe.config.{Config, ConfigFactory}

object Helper {
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

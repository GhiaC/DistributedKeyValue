package ai.bale.inter

import com.typesafe.config.{Config, ConfigFactory}
import messages._

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

  def commandToOperatorMessage(console: String): OperatorMessage = {
    val operator: Array[String] = console.split(" ")
    if (operator(0) == "get" && operator.length == 2)
      GetItem(operator(1))
    else if (operator(0) == "set" && operator.length == 3)
      Set(operator(1), operator(2))
    else if (operator(0) == "remove" && operator.length == 2)
      Remove(operator(1))
    else
      GetAll
  }
}

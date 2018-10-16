package ai.bale.inter

import com.typesafe.config.{Config, ConfigFactory}
import ai.bale.protos.keyValue._
import scala.concurrent.Future

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

  def commandToOperatorMessage(console: String): Option[Any] = {
    val operator: Array[String] = console.split(" ")
    if (operator(0) == "get" && operator.length == 2)
      Some(GetRequest(operator(1)))
    else if (operator(0) == "set" && operator.length == 3)
      Some(SetRequest(operator(1), operator(2)))
    else if (operator(0) == "remove" && operator.length == 2)
      Some(RemoveRequest(operator(1)))
    else
      None
  }

  def sendRequest(request: Any, stub: KeyValueGrpc.KeyValueStub): Future[Any] = {
    request match {
      case req: GetRequest => stub.getValue(req)
      case req: SetRequest => stub.setKey(req)
      case req: RemoveRequest => stub.removeKey(req)
    }
  }
}

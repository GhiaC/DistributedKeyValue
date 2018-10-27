package ai.bale

import com.typesafe.config.{Config, ConfigFactory}
import ai.bale.protos.keyValue._

import scala.concurrent.Future

object Helper {

  class ExtendedString(s: String) {
    def isNumber: Boolean = s forall Character.isDigit
  }

  def createConfig(port: Int, role: String, seedNodes: String = "", resources: String): Config = {
    ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """)
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]")).
      withFallback(ConfigFactory.parseString(s"akka.cluster.seed-nodes = [$seedNodes]")).
      withFallback(ConfigFactory.load(resources))
  }

//  def sendRequest(request: Any, stub: KeyValueGrpc.KeyValueStub): Future[Any] = { // return Any
//    request match {
//      case req: GetRequest => stub.get(req)
//      case req: SetRequest => stub.set(req)
//      case req: RemoveRequest => stub.remove(req)
//      case req: IncreaseRequest => stub.increase(req)
//    }
//  }
}

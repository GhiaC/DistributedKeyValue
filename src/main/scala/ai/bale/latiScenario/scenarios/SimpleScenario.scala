package ai.bale.latiScenario.scenarios

import ai.bale.Client
import ai.bale.lati.scenario.AbstractScenario
import ai.bale.protos.keyValue._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class SimpleScenario extends AbstractScenario {

  var client: Client.type = _

  override def beforeScenario()(implicit ec: ExecutionContext): Future[Any] = Future {
    client = Client
  }

  override def scenario()(implicit ec: ExecutionContext): Future[Any] = {
    val (key, value) = (Random.alphanumeric.take(20).mkString, "1")
    client.stub.set(SetRequest(key, value.toString))
  }

  override def afterScenario()(implicit ec: ExecutionContext): Future[Any] = Future {
  }
}

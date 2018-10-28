package ai.bale.latiScenario.scenarios

import ai.bale.Client
import ai.bale.lati.scenario.AbstractScenario
import ai.bale.protos.keyValue._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class SimpleScenario extends AbstractScenario {

  var client: Client = _

  override def beforeScenario()(implicit ec: ExecutionContext): Future[Any] = Future {
    client = new Client
    Future.successful()
  }

  override def scenario()(implicit ec: ExecutionContext): Future[Any] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextInt(100))
    for {
      _ <- client.stub.set(SetRequest(key, value.toString))
      _ <- client.stub.remove(RemoveRequest(key))
      _ <- client.stub.get(GetRequest(key))
    } yield ()
  }


  override def afterScenario()(implicit ec: ExecutionContext): Future[Any] = {
    client.channel.shutdown()
    Future.successful()
  }
}

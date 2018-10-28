package ai.bale.distributedKeyValue

import ai.bale.protos.keyValue._
import akka.actor.ActorSystem
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

class KeyValue(actorName: String) {
  implicit val system: ActorSystem = ActorSystem(actorName)

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val service = new KeyValueImpl(system)
  val randomPort: Int = 25000 //new Random().nextInt(5000)
  ServerBuilder.forPort(randomPort).addService(KeyValueGrpc.bindService(service, ec)).build.start
  system.log.info("GRPC Started on {} port", randomPort)
}

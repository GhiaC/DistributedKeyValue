package ai.bale.distributedKeyValue

import ai.bale.protos.keyValue._
import akka.actor.ActorSystem
import akka.persistence.cassandra.EventsByTagMigration
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

class KeyValue(actorName: String) {
  implicit val system: ActorSystem = ActorSystem(actorName)

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val migration = EventsByTagMigration(system)
  migration.createTables()

  val service = new KeyValueImpl(system)
  val randomPort: Int = 21000 //new Random().nextInt(5000)
  ServerBuilder.forPort(randomPort).addService(KeyValueGrpc.bindService(service, ec)).build.start
  system.log.info("GRPC Started on {} port", randomPort)
}

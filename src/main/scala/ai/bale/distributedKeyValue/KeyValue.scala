package ai.bale.distributedKeyValue

import ai.bale.protos.keyValue._
import akka.actor.ActorSystem
import akka.persistence.cassandra.EventsByTagMigration
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{Server, ServerBuilder}

import scala.concurrent.ExecutionContextExecutor

class KeyValue(actorName: String) {
  private val conf: Config = ConfigFactory.load
  private val gRPCServerPort = conf.getInt("server.gRPCServerPort")

  implicit val system: ActorSystem = ActorSystem(actorName)

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val migration = EventsByTagMigration(system)
  migration.createTables()

  val service = new KeyValueImpl(system)

  val gRPCServer: Server = ServerBuilder.forPort(gRPCServerPort).addService(KeyValueGrpc.bindService(service, ec)).build.start
  gRPCServer.awaitTermination()

  system.log.info("gRPC Started on {} port", gRPCServerPort)
}

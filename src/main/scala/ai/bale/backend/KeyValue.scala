package ai.bale.backend

import ai.bale.Helper
import ai.bale.protos.keyValue._
import akka.actor.{ActorRef, ActorSystem, DeadLetter, Props, UnhandledMessage}
import io.grpc.ServerBuilder

import scala.concurrent.{ExecutionContextExecutor, Future}

class KeyValue(actorName: String, port: Int, seedNodeSeq: Seq[String] = Seq()) {
  implicit val system: ActorSystem = ActorSystem(actorName, Helper.createConfig(port, "backend", seedNodeSeq.mkString("\"","\",\"","\""), "application"))
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val deadActorListener: ActorRef = system.actorOf(Props[DeadActorListener], "deadLetters")
  system.eventStream.subscribe(deadActorListener, classOf[UnhandledMessage])
  system.eventStream.subscribe(deadActorListener, classOf[DeadLetter])

  val shardRegion: ActorRef = ClusterExtension(system).shardRegion

   val worker = new KeyValueImpl(shardRegion)

  ServerBuilder.forPort(port + 100).addService(KeyValueGrpc.bindService(worker, ec)).build.start

  def set(msg: SetRequest): Future[Ack] = worker.set(msg)

  def get(msg: GetRequest): Future[GetReply] = worker.get(msg)

  def remove(msg: RemoveRequest): Future[Ack] = worker.remove(msg)

  def increase(msg: IncreaseRequest): Future[IncreaseReply] = worker.increase(msg)

  system.log.info("Started port {}", port)

}

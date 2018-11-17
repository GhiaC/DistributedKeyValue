package ai.bale.distributedKeyValue

import java.util.concurrent.TimeUnit

import ai.bale.protos.keyValue._
import akka.actor.ActorSystem
import akka.event.Logging
import akka.util.Timeout
import io.grpc.Status

import scala.concurrent.{ExecutionContext, Future}

class KeyValueImpl(actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends KeyValueGrpc.KeyValue {
  implicit val timeout: Timeout = Timeout(30, TimeUnit.SECONDS)
  private val logger = Logging.getLogger(actorSystem, this)

  val workerExtension = WorkerExtension(actorSystem)

  private val internalErrorStatus = Future.failed(
    Status
      .INTERNAL
      .augmentDescription("internal error!!")
      .asRuntimeException()
  )

  override def set(request: SetRequest): Future[Ack] = {
    workerExtension.set(request) recoverWith {
      case e =>
        logger.error(e.getMessage)
        internalErrorStatus
    }
  }

  override def get(request: GetRequest): Future[GetReply] = {
    workerExtension.get(request) recoverWith {
      case e =>
        logger.error(e.getMessage)
        internalErrorStatus
    }
  }

  override def remove(request: RemoveRequest): Future[Ack] = {
    workerExtension.remove(request) recoverWith {
      case e =>
        logger.error(e.getMessage)
        internalErrorStatus
    }
  }

  override def increase(request: IncreaseRequest): Future[IncreaseReply] = {
    workerExtension.increase(request) recoverWith {
      case e =>
        logger.error(e.getMessage)
        internalErrorStatus
    }
  }

  override def snapshot(request: SnapshotRequest): Future[Ack] = {
    workerExtension.snapshot(request) recoverWith {
      case e =>
        logger.error(e.getMessage)
        internalErrorStatus
    }
  }
}
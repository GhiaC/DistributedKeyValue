package ai.bale.distributedKeyValue

import java.util.concurrent.TimeUnit

import ai.bale.protos.keyValue._
import akka.actor.ActorSystem
import akka.util.Timeout
import io.grpc.Status

import scala.concurrent.{ExecutionContext, Future}

class KeyValueImpl(actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends KeyValueGrpc.KeyValue {
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  val workerExtension = WorkerExtension(actorSystem)
  private val internalErrorStatus = Future.failed(
    Status
      .INTERNAL
      .augmentDescription("internal error!")
      .asRuntimeException()
  )

  override def set(request: SetRequest): Future[Ack] = {
    workerExtension.set(request).map {
      msg: Ack => msg
    } recoverWith {
      case _: Throwable => internalErrorStatus
    }
  }

  override def get(request: GetRequest): Future[GetReply] = {
    workerExtension.get(request).map {
      msg: GetReply => msg
    } recoverWith {
      case _: Throwable => internalErrorStatus
    }
  }

  override def remove(request: RemoveRequest): Future[Ack] = {
    workerExtension.remove(request).map {
      msg: Ack => msg
    } recoverWith {
      case _: Throwable => internalErrorStatus
    }
  }

  override def increase(request: IncreaseRequest): Future[IncreaseReply] = {
    workerExtension.increase(request).map {
      msg: IncreaseReply => msg
    } recoverWith {
      case _: Throwable => internalErrorStatus
    }
  }

  override def snapshot(request: SnapshotRequest): Future[Ack] = {
    workerExtension.snapshot(request).map {
      msg: Ack => msg
    } recoverWith {
      case _: Throwable => internalErrorStatus
    }
  }
}
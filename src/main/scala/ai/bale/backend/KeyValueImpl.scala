package ai.bale.backend

import java.util.concurrent.TimeUnit

import akka.actor
import akka.pattern._
import akka.util.Timeout
import ai.bale.protos.keyValue._

import scala.concurrent.{ExecutionContext, Future}

 class KeyValueImpl(workerActor: actor.ActorRef)(implicit ec: ExecutionContext) extends KeyValueGrpc.KeyValue {
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def set(request: SetRequest): Future[Ack] = {
    (workerActor ? request).map {
      case msg: Ack => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def get(request: GetRequest): Future[GetReply] = {
    (workerActor ? request).map {
      case msg: GetReply => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def remove(request: RemoveRequest): Future[Ack] = {
    (workerActor ? request).map {
      case msg: Ack => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def increase(request: IncreaseRequest): Future[IncreaseReply] = {
    (workerActor ? request).map {
      case msg: IncreaseReply => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }
}
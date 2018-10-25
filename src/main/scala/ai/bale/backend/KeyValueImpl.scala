package backend

import java.util.concurrent.TimeUnit

import ai.bale.protos.keyValue._
import akka.actor
import akka.util.Timeout
import akka.pattern._
import scala.concurrent.{ExecutionContext, Future}

private class KeyValueImpl(workerActor: actor.ActorRef)(implicit ec: ExecutionContext) extends KeyValueGrpc.KeyValue {
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def setKey(request: SetRequest): Future[Ack] = {
    (workerActor ? request).map {
      case msg: Ack => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def getValue(request: GetRequest): Future[GetReply] = {
    (workerActor ? request).map {
      case msg: GetReply => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def removeKey(request: RemoveRequest): Future[Ack] = {
    (workerActor ? request).map {
      case msg: Ack => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }

  override def increaseValue(request: IncreaseRequest): Future[Ack] = {
    (workerActor ? request).map {
      case msg: Ack => msg
    } recoverWith {
      case throwable: Throwable => Future.failed(throwable)
    }
  }
}
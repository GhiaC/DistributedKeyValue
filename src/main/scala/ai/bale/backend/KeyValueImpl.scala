package backend

import java.util.concurrent.TimeUnit

import ai.bale.protos.keyValue._
import akka.actor
import akka.util.Timeout
import messages.{FailedJob, SuccessJob}
import akka.pattern._
import scala.concurrent.{ExecutionContext, Future}

private class KeyValueImpl(workerActor: actor.ActorRef)(implicit ec: ExecutionContext) extends KeyValueGrpc.KeyValue {
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def setKey(request: SetRequest): Future[SetReply] = {
    (workerActor ? request).map {
      case msg: SuccessJob =>
        SetReply(msg.result)
    }.recoverWith {
      case throwable: Throwable =>
        Future.failed(throwable)
    }
  }

  override def getValue(request: GetRequest): Future[GetReply] = {
    (workerActor ? request).map {
      case msg: SuccessJob =>
        GetReply( msg.result)
    }.recoverWith {
      case throwable: Throwable =>
        Future.failed(throwable)
    }
  }

  override def removeKey(request: RemoveRequest): Future[RemoveReply] = {
    (workerActor ? request).map {
      case msg: SuccessJob =>
        RemoveReply(msg.result)
    }.recoverWith {
      case throwable: Throwable =>
        Future.failed(throwable)
    }
  }

  override def increaseValue(request: IncreaseRequest): Future[IncreaseReply] = {
    (workerActor ? request).map {
      case msg: SuccessJob =>
        IncreaseReply(msg.result)
    }.recoverWith {
      case throwable: Throwable =>
        Future.failed(throwable)
    }
  }
}
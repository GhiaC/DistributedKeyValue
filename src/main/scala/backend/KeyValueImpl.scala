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
  override def setKey(req: SetRequest): Future[SetReply] = {
    (workerActor ? messages.Set(req.key, req.value)).map {
      case msg: SuccessJob =>
        SetReply(msg.result)
      case msg: FailedJob =>
        SetReply(msg.reason)
    }
  }

  override def getValue(request: GetRequest): Future[GetReply] = {
    (workerActor ? messages.GetItem(request.key)).map {
      case msg: SuccessJob =>
        GetReply("Success",msg.result)
      case msg: FailedJob =>
        GetReply("Failed",msg.reason)
    }
  }

  override def removeKey(request: RemoveRequest): Future[RemoveReply] = {
    (workerActor ? messages.Remove(request.key)).map {
      case msg: SuccessJob =>
        RemoveReply(msg.result)
      case msg: FailedJob =>
        RemoveReply(msg.reason)
    }
  }
}
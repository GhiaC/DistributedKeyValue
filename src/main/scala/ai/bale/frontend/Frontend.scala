package frontend

import java.util.concurrent.TimeUnit

import backend.Helper
import ai.bale.protos.keyValue.KeyValueGrpc
import akka.actor.ActorSystem
import akka.util.Timeout
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Success

object Frontend {

  implicit val system: ActorSystem = ActorSystem("ClusterSystem", Helper.createConfig(2555, "frontend"))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)

  implicit def printer(m: Any): Unit = system.log.info(m.toString)

  implicit val channel: ManagedChannel =
    ManagedChannelBuilder.forAddress("localhost", 2654).usePlaintext(true).build
  implicit val stub: KeyValueGrpc.KeyValueStub = KeyValueGrpc.stub(channel)

  def main(args: Array[String]): Unit = {
    doCommandAndPrintResult()
  }

  def doCommand(console: String): Future[Any] = {
    Helper.commandToOperatorMessage(console) match {
      case Some(msg) => Helper.sendRequest(msg, stub)
      case None => Future("Error, invalid input!")

    }
  }

  @scala.annotation.tailrec
  def doCommandAndPrintResult(console: String = scala.io.StdIn.readLine(), iterate: Boolean = true)
                             (implicit stub: KeyValueGrpc.KeyValueStub, timeout: Timeout, printer: Any => Unit, ec: ExecutionContextExecutor): Unit = {
    doCommand(console) onComplete {
      case Success(value) => printer(value)
      case f => printer(f)
    }
    if (iterate) doCommandAndPrintResult()
  }
}
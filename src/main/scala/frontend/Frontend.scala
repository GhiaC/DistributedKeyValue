package frontend

import java.util.concurrent.TimeUnit

import ai.bale.inter.Helper
import ai.bale.protos.keyValue.KeyValueGrpc
import akka.actor.ActorSystem
import akka.util.Timeout
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Success

object Frontend {
  def main(args: Array[String]): Unit = {
    try {
      implicit val system: ActorSystem = ActorSystem("ClusterSystem", Helper.createConfig(2555, "frontend", "frontend"))
      implicit val ec: ExecutionContextExecutor = system.dispatcher
      implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)

      implicit def printer(m: Any): Unit = system.log.info(m.toString)

      implicit val channel: ManagedChannel =
        ManagedChannelBuilder.forAddress("localhost", 2654).usePlaintext(true).build

      getCommand()
    }
    catch {
      case msg: Exception =>
        println(msg)
    }
  }

  @scala.annotation.tailrec
  def getCommand()(implicit managedChannel: ManagedChannel, timeout: Timeout, printer: Any => Unit, ec: ExecutionContextExecutor): Unit = {
    val console = scala.io.StdIn.readLine()
    val stub: KeyValueGrpc.KeyValueStub = KeyValueGrpc.stub(managedChannel)
    Helper.commandToOperatorMessage(console) match {
      case Some(msg) => val f: Future[Any] = Helper.sendRequest(msg, stub)
        f onComplete {
          case Success(value) => printer(value.toString)
          case e => printer(e.toString)
        }
      case None =>
        printer("Error, invalid input!")
    }
    getCommand()
  }
}
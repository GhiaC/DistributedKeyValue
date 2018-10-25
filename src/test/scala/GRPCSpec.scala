import ai.bale.protos.keyValue._
import frontend.Frontend
import org.scalatest.{Assertion, AsyncFlatSpec}

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent._

class GRPCSpec extends AsyncFlatSpec {

  behavior of "Frontend of Distributed key value"

  scenario1()
  scenario2()
  scenario3()
  scenario4()

  def scenario4(): Unit = {
    val key = new Random().nextString(3)
    val value = new Random().nextInt(100)
    s"Result of `set $key $value` command" should "SetReply(\"Added\")" in {
      commandTest(s"set $key $value", Ack("Added"))
    }
    "Result of get after send one hundred increase message" should "GetReply(" + (value + 100) + "))" in {
      val myFutures: IndexedSeq[Future[Any]] = (1 to 20) flatMap { x =>
        (1 to 5) map { _ =>
          Frontend.doCommand(s"increase $key")
        }
      }
      Future.sequence(myFutures) flatMap { _ =>
        commandTest(s"get $key", GetReply((value + 100).toString))
      }
    }
  }

  def scenario3(): Unit = {
    val key = new Random().nextString(3)
    val value = new Random().nextInt(100)
    val increase = new Random().nextInt(100)
    s"Result of `set $key $value` command" should "Ack(\"Added\")" in {
      commandTest(s"set $key $value", Ack("Added"))
    }

    s"Result of get after send ${increase} increase message" should "GetReply(" + (value + increase) + "))" in {
      val myFutures: IndexedSeq[Future[Any]] = (1 to increase) map { _ =>
        Frontend.doCommand(s"increase $key")
      }
      Future.sequence(myFutures) flatMap { _ =>
        commandTest(s"get $key", GetReply((value + increase).toString))
      }
    }
  }

  def scenario1() {
    val key = new Random().nextString(5)
    val value = new Random().nextString(40)
    s"Result of `set $key $value` command" should "SetReply(\"Added\")" in {
      commandTest(s"set $key $value"
        , Ack("Added"))
    }

    s"Result of `get $key` command" should "GetReply("+value+")" in {
      commandTest(s"get $key", GetReply(value))
    }

    "Result of remove command" should "Ack(\"Removed\")" in {
      commandTest(s"remove $key", Ack("Removed"))
    }

    "Result of get command" should "Exception(\"invalid key!\")" in {
      Frontend.doCommand(s"get $key") map { response =>
        fail
      } recoverWith {
        case e: Exception =>
          assert(e.getMessage == "INTERNAL: java.lang.Exception: invalid key! (of class java.lang.Exception)")
      }
    }
  }


  def scenario2() {
    val key = new Random().nextString(10)
    val value = new Random().nextString(20)

    s"Result of `set $key $value` command" should "Ack(\"Added\")" in {
      commandTest(s"set $key $value"
        , Ack("Added"))
    }
    s"Result of `get $key` command" should s"GetReply($key)" in {
      commandTest(s"get $key", GetReply(value))
    }
  }

  def commandTest(command: String, reply: Any): Future[Assertion] = {
    Frontend.doCommand(command) map { response =>
      assert(response === reply)
    } recoverWith {
      case throwable: Throwable =>
        assert(throwable.getMessage === reply)
    }
  }
}

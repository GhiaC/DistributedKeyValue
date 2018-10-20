import ai.bale.protos.keyValue.{GetReply, RemoveReply, SetReply}
import frontend.Frontend
import org.scalatest.{Assertion, AsyncFlatSpec}

import scala.concurrent.Future
import scala.util.Random

class GRPCSpec extends AsyncFlatSpec {

  behavior of "Frontend of Distributed key value"

  scenario4()

  def scenario4(): Unit = {
    val key = new Random().nextString(3)
    val value = new Random().nextInt(100)
    s"Result of `set $key $value` command" should "SetReply(\"Added\")" in {
      commandTest(s"set $key $value", SetReply("Added"))
    }
    "Result of get after send increase message" should "GetReply(\"Success\", " + (value + 100) + "))" in {
      val myFutures: IndexedSeq[Future[Any]] = (1 to 20) flatMap { x =>
        (1 to 5) map { _ =>
          Frontend.doCommand(s"increase $key")
        }
      }
      Future.sequence(myFutures) flatMap { _ =>
        commandTest(s"get $key", GetReply("Success", (value + 100).toString))
      }
    }
  }

  def scenario3(): Unit = {
    val key = new Random().nextString(3)
    val value = new Random().nextInt(100)
    s"Result of `set $key $value` command" should "SetReply(\"Added\")" in {
      commandTest(s"set $key $value", SetReply("Added"))
    }

    "Result of get after send increase message" should "GetReply(\"Success\", " + (value + 100) + "))" in {
      val myFutures: IndexedSeq[Future[Any]] = (1 to 100) map { _ =>
        Frontend.doCommand(s"increase $key")
      }
      Future.sequence(myFutures) flatMap { _ =>
        commandTest(s"get $key", GetReply("Success", (value + 100).toString))
      }
    }
  }

  def scenario1() {
    "Result of `set a TestValueTestValueTestValueTestValueTestValue` command" should "SetReply(\"Added\")" in {
      commandTest("set a TestValueTestValueTestValueTestValueTestValue"
        , SetReply("Added"))
    }

    "Result of `get a` command" should "GetReply(\"Success\",\"TestValueTestValueTestValueTestValueTestValue\")" in {
      commandTest("get a", GetReply("Success", "TestValueTestValueTestValueTestValueTestValue"))
    }

    "Result of remove command" should "RemoveReply(\"Removed\")" in {
      commandTest("remove a", RemoveReply("Removed"))
    }

    "Result of get command" should "GetReply(\"Failed\",\"invalid key!\")" in {
      commandTest("get a", GetReply("Failed", "invalid key"))
    }
  }

  def scenario2() {
    "Result of `set abcddKey abcdValue` command" should "SetReply(\"Added\")" in {
      commandTest("set abcddKey abcdValue"
        , SetReply("Added"))
    }
    "Result of `get abcddKey` command" should "GetReply(\"Success\",\"abcdValue\")" in {
      commandTest("get abcddKey", GetReply("Success", "abcdValue"))
    }
  }

  def commandTest(command: String, reply: Any): Future[Assertion] = {
    Frontend.doCommand(command) map { response =>
      assert(response === reply)
    }
  }
}

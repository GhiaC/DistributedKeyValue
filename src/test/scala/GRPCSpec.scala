import ai.bale.protos.keyValue.{GetReply, RemoveReply, SetReply}
import frontend.Frontend
import org.scalatest.{Assertion, AsyncFlatSpec}

import scala.concurrent.Future

class GRPCSpec extends AsyncFlatSpec {

  behavior of "frontend"

  scenario2()

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

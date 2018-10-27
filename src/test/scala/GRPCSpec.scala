import ai.bale.Client
import ai.bale.protos.keyValue._
import org.scalatest
import org.scalatest.{Assertion, AsyncFlatSpec}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

class GRPCSpec extends AsyncFlatSpec {

  val stub: KeyValueGrpc.KeyValueStub = new Client().stub

  behavior of "Frontend of Distributed key value"
  it should "return exception when get removed key" in GetRemovedKey
  it should "check simple set and get request" in simpleSetAndGet
  it should "Result of get after send many increase message" in sendRandomIncrease
  it should "Result of get after send one hundred increase message" in sendOneHundredIncreaseRequest


  def sendOneHundredIncreaseRequest(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextInt(100))
    stub.set(SetRequest(key, value.toString))
    val myFutures: IndexedSeq[Future[Any]] = (1 to 20) flatMap { _ =>
      (1 to 5) map { _ =>
        stub.increase(IncreaseRequest(key))
      }
    }
    Future.sequence(myFutures) flatMap { _ => sendGetRequestAndCompareReply(GetRequest(key), (value + 100) toString) }
  }

  def sendGetRequestAndCompareReply(msg: GetRequest, value: Any): Future[scalatest.Assertion] = {
    stub.get(msg) map {
      getReply => assert(getReply == GetReply(value toString))
    }
  }

  def sendRandomIncrease(): Future[Assertion] = {
    val random = new Random()
    val (key, value, increase) = (random.nextString(3), random.nextInt(100), random.nextInt(100))
    stub.set(SetRequest(key, value.toString))
    val myFutures: IndexedSeq[Future[Any]] = (1 to increase) map { _ =>
      stub.increase(IncreaseRequest(key))
    }
    Future.sequence(myFutures) flatMap { _ =>
      sendGetRequestAndCompareReply(GetRequest(key), value + increase toString)
    }
  }

  def GetRemovedKey(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextString(100))
    stub.set(SetRequest(key, value.toString))
    stub.remove(RemoveRequest(key))
    stub.get(GetRequest(key)) map { _ =>
      fail
    } recoverWith {
      case e: Exception =>
        assert(e.getMessage == "INTERNAL: java.lang.Exception: invalid key! (of class java.lang.Exception)")
    }
  }

  def simpleSetAndGet(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextString(100))
    stub.set(SetRequest(key, value.toString)) flatMap { _ =>
      sendGetRequestAndCompareReply(GetRequest(key), value toString)
    }
  }
}

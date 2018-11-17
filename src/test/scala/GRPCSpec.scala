import ai.bale.Client
import ai.bale.protos.keyValue._
import org.scalatest
import org.scalatest.{Assertion, AsyncFlatSpec}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

class GRPCSpec extends AsyncFlatSpec {

  private val client: Client.type = Client
  private val stub: KeyValueGrpc.KeyValueStub = client.stub
  private val blockingStub: KeyValueGrpc.KeyValueBlockingStub = client.blockingStub

  behavior of "Frontend of Distributed key value"
  it should "return exception when get removed key" in GetRemovedKey
  it should "check simple set and get request" in simpleSetAndGet
  it should "Result of get after send many increase message" in sendRandomIncrease
  it should "Result of get after send one hundred increase message" in sendOneHundredIncreaseRequest
  it should "check snapshotRequest" in sendSnapshotRequest


  def sendOneHundredIncreaseRequest(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextInt(100))
    stub.set(SetRequest(key, value.toString))
    val myFutures: IndexedSeq[Future[Any]] = (1 to 20) flatMap { _ =>
      (1 to 5) map { _ =>
        stub.increase(IncreaseRequest(key))
      }
    }
    Future.sequence(myFutures) flatMap { _ => sendGetRequestAndCompareReply(GetRequest(key), GetReply(Some((value + 100) toString))) }
  }

  def sendGetRequestAndCompareReply(msg: GetRequest, value: Any): Future[scalatest.Assertion] = {
    val getResponse = blockingStub.get(msg)
    assert(getResponse == value)
  }

  def sendRandomIncrease(): Future[Assertion] = {
    val random = new Random()
    val (key, value, increase) = (random.nextString(3), random.nextInt(100), random.nextInt(100))
    stub.set(SetRequest(key, value.toString))
    val myFutures: IndexedSeq[Future[Any]] = (1 to increase) map { _ =>
      stub.increase(IncreaseRequest(key))
    }
    Future.sequence(myFutures) flatMap { _ =>
      sendGetRequestAndCompareReply(GetRequest(key), GetReply(Some(value + increase toString)))
    }
  }

  def GetRemovedKey(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextString(100))
    blockingStub.set(SetRequest(key, value.toString))
    blockingStub.remove(RemoveRequest(key))
    sendGetRequestAndCompareReply(GetRequest(key), GetReply(None))
  }

  def simpleSetAndGet(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextString(100))
    blockingStub.set(SetRequest(key, value.toString))
    sendGetRequestAndCompareReply(GetRequest(key), GetReply(Some(value toString)))
  }

  def sendSnapshotRequest(): Future[Assertion] = {
    val random = new Random()
    val (key, value) = (random.nextString(3), random.nextString(100))
    blockingStub.set(SetRequest(key, value.toString))
    val snapshotResponse = blockingStub.snapshot(SnapshotRequest(key))
    assert(snapshotResponse == Ack("Success"))
  }

}

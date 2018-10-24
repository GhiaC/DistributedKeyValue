package backend

import akka.actor._
import akka.cluster.sharding._
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import messages._
import java.util.concurrent.TimeUnit
import akka.pattern._
import scala.concurrent.ExecutionContext
import ai.bale.protos.keyValue._

class ClusterMember extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("Starting ShardRegion {}", context.system.name)
  }

  val conf: Config = ConfigFactory.load()
  private val numberOfKeyPerActor = conf.getInt("myconf.number-of-key-per-actor")

  private val numberOfShards = conf.getInt("myconf.number-of-shards")

  private val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg@SetRequest(key, _) => ((key.hashCode() % numberOfKeyPerActor) toString, msg)
    case msg@RemoveRequest(key) => (key.hashCode() % numberOfKeyPerActor toString, msg)
    case msg@GetRequest(key) => ((key.hashCode() % numberOfKeyPerActor) toString, msg)
    case msg@IncreaseRequest(key) => ((key.hashCode() % numberOfKeyPerActor) toString, msg)
  }

  private val extractShardId: ShardRegion.ExtractShardId = {
    case SetRequest(key, _) => (key.hashCode() % numberOfShards).toString
    case RemoveRequest(key) => (key.hashCode % numberOfShards).toString
    case GetRequest(key) => (key.hashCode() % numberOfShards).toString
    case IncreaseRequest(key) => (key.hashCode() % numberOfShards).toString
    case ShardRegion.StartEntity(id) => (id.toLong % numberOfShards).toString
  }

  val shardRegion: ActorRef = ClusterSharding(context.system).start(
    typeName = "Worker",
    entityProps = Props[Worker],
    settings = ClusterShardingSettings(context.system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

  implicit val ec: ExecutionContext = context.dispatcher

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  def receive: PartialFunction[Any, Unit] = {
    case msg => (shardRegion ? msg) pipeTo sender()
  }
}

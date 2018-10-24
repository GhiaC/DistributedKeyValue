package backend

import akka.actor._
import akka.cluster.sharding._
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import messages._
import java.util.concurrent.TimeUnit
import akka.pattern._
import scala.concurrent.ExecutionContext
import java.util.concurrent.TimeUnit

import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}
import java.util.concurrent.TimeUnit

import scala.concurrent.{ExecutionContext, Future}
import ai.bale.protos.keyValue._

object ClusterMemberExtension
  extends ExtensionId[ClusterMemberExtension]
    with ExtensionIdProvider {

  override def lookup: ClusterMemberExtension.type = ClusterMemberExtension

  override def createExtension(system: ExtendedActorSystem) = new ClusterMemberExtension

  override def get(system: ActorSystem): ClusterMemberExtension = super.get(system)
}

class ClusterMemberExtension extends Extension {
  val cluster: actor.ActorRef = context.system.actorOf(Props[ClusterMember], "ClusterMember")
}

private[this] class ClusterMember extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info("Starting ShardRegion {}", context.system.name)
  }

  val conf: Config = ConfigFactory.load()
  private val numberOfEntity = conf.getInt("myconf.number-of-entity")

  private val numberOfShards = conf.getInt("myconf.number-of-shards")

  private val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg@SetRequest(key, _) => ((key.hashCode() % numberOfEntity) toString, msg)
    case msg@RemoveRequest(key) => (key.hashCode() % numberOfEntity toString, msg)
    case msg@GetRequest(key) => ((key.hashCode() % numberOfEntity) toString, msg)
    case msg@IncreaseRequest(key) => ((key.hashCode() % numberOfEntity) toString, msg)
  }

  private val extractShardId: ShardRegion.ExtractShardId = {
    case SetRequest(key, _) => (key.hashCode() % numberOfShards).toString
    case RemoveRequest(key) => (key.hashCode % numberOfShards).toString
    case GetRequest(key) => (key.hashCode() % numberOfShards).toString
    case IncreaseRequest(key) => (key.hashCode() % numberOfShards) toString
    case ShardRegion.StartEntity(id) => (id.toLong % numberOfShards).toString
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case _: IllegalArgumentException => SupervisorStrategy.Resume
    case _: ActorInitializationException => SupervisorStrategy.Stop
    case _: DeathPactException => SupervisorStrategy.Stop
    case _: Exception => SupervisorStrategy.Restart
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
    case msg: SetRequest =>
      (shardRegion ? msg) pipeTo sender()
    case msg: GetRequest =>
      (shardRegion ? msg) pipeTo sender()
    case msg: RemoveRequest =>
      (shardRegion ? msg) pipeTo sender()
    case msg: IncreaseRequest =>
      (shardRegion ? msg) pipeTo sender()
  }
}

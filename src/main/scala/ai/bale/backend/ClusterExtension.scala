package ai.bale.backend

import akka.actor._
import akka.cluster.sharding._
import com.typesafe.config.{Config, ConfigFactory}
import ai.bale.protos.keyValue._

object ClusterExtension
  extends ExtensionId[ClusterExtension]
    with ExtensionIdProvider {

  override def lookup: ClusterExtension.type = ClusterExtension

  override def createExtension(system: ExtendedActorSystem) = new ClusterExtension(system)

  override def get(system: ActorSystem): ClusterExtension = super.get(system)
}

class ClusterExtension(system: ExtendedActorSystem) extends Extension {
  val conf: Config = ConfigFactory.load()
  private val numberOfEntity = conf.getInt("cluster-sharding.number-of-entity")

  private val oneKeyPerActor = conf.getString("cluster-sharding.one-key-per-actor")

  private val numberOfShards = conf.getInt("cluster-sharding.number-of-shards")

  private def getEntityId(key: String): String = {
    oneKeyPerActor match {
      case "on" =>
        key.hashCode() toString
      case _ =>
        (key.hashCode() % numberOfEntity) toString
    }
  }

  private val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg@SetRequest(key, _) => (getEntityId(key), msg)
    case msg@RemoveRequest(key) => (getEntityId(key), msg)
    case msg@GetRequest(key) => (getEntityId(key), msg)
    case msg@IncreaseRequest(key) => (getEntityId(key), msg)
  }

  private val extractShardId: ShardRegion.ExtractShardId = {
    case SetRequest(key, _) => (key.hashCode() % numberOfShards).toString
    case RemoveRequest(key) => (key.hashCode % numberOfShards).toString
    case GetRequest(key) => (key.hashCode() % numberOfShards).toString
    case IncreaseRequest(key) => (key.hashCode() % numberOfShards).toString
    case ShardRegion.StartEntity(id) => (id.toLong % numberOfShards).toString
  }

  val shardRegion: ActorRef = ClusterSharding(system).start(
    typeName = "Worker",
    entityProps = Props[Worker],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)
}

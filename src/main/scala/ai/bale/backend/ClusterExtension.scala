package backend

import akka.actor._
import akka.cluster.sharding._
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import java.util.concurrent.TimeUnit
import akka.pattern._
import scala.concurrent.ExecutionContext
import ai.bale.protos.keyValue._

object ClusterMemberExtension
  extends ExtensionId[ClusterMemberExtension]
    with ExtensionIdProvider {

  override def lookup: ClusterMemberExtension.type = ClusterMemberExtension

  override def createExtension(system: ExtendedActorSystem) = new ClusterMemberExtension(system)

  override def get(system: ActorSystem): ClusterMemberExtension = super.get(system)
}

class ClusterMemberExtension(system: ExtendedActorSystem) extends Extension {
  val conf: Config = ConfigFactory.load()
  private val numberOfEntity = conf.getInt("myconf.number-of-entity")

  private val oneKeyPerActor = conf.getString("myconf.one-key-per-actor")

  private val numberOfShards = conf.getInt("myconf.number-of-shards")

  private def getNumberOfEntity(key: String): String = {
    oneKeyPerActor match {
      case "on" =>
        key.hashCode() toString
      case _ =>
        (key.hashCode() % numberOfEntity) toString
    }
  }

  private val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg@SetRequest(key, _) => (getNumberOfEntity(key), msg)
    case msg@RemoveRequest(key) => (getNumberOfEntity(key), msg)
    case msg@GetRequest(key) => (getNumberOfEntity(key), msg)
    case msg@IncreaseRequest(key) => (getNumberOfEntity(key), msg)
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

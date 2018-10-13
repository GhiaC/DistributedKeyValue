import java.util.concurrent.TimeUnit

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.util.Timeout
import messages.{BackendRegistration, OperatorMessage, ResultMessage}

import scala.concurrent.Future

class ClusterMember extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  private val entity = context.system.actorOf(Props[Worker], "entity")

  import akka.pattern._

  def receive: PartialFunction[Any, Unit] = {
    case MemberUp(m) => register(m)

    case op: OperatorMessage =>
      val result: Future[ResultMessage] = (entity ? op).mapTo[ResultMessage]
      result pipeTo (sender())
      println("op msg")

    case state: CurrentClusterState ⇒
      state.members.filter(_.status == MemberStatus.Up) foreach register

    //TODO register after reachable

    case elseMessage =>
      context.system.log.info(elseMessage.toString)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
      context.actorSelection(RootActorPath(member.address) / "user" / "Client") !
        BackendRegistration
    }
}

package backend

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.pattern._
import akka.util.Timeout
import messages._

import scala.concurrent.ExecutionContextExecutor

class ClusterMember extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
  implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  private val entity = context.system.actorOf(Props[Worker], "entity")

  def receive: PartialFunction[Any, Unit] = {
    case MemberUp(m) => register(m)

    case op: OperatorMessage => (entity ? op).mapTo[ResultMessage] pipeTo sender()

    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up) foreach register

    //TODO register after reachable

    case elseMessage => context.system.log.info(elseMessage.toString)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "Client") ! BackendRegistration

}

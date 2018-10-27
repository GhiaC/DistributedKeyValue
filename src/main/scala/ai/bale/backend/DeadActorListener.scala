package ai.bale.backend

import akka.actor._

class DeadActorListener extends Actor with ActorLogging {
  def receive: PartialFunction[Any, Unit] = {
    case u: UnhandledMessage => log.info("Unhandled message " + u.message)
    case d: DeadLetter => log.info("dead message " + d.message)
  }
}
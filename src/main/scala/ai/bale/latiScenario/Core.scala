package ai.bale.latiScenario

import ai.bale.lati.starter.LatiCoreStarter
import ai.bale.latiScenario.scenarios.SimpleScenario

object Core extends App {
  val scenarios = List(classOf[SimpleScenario])
  LatiCoreStarter.start(scenarios)
}

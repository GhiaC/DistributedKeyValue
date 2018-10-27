package ai.bale.backend

object Main {
  def main(args: Array[String]): Unit = {
    val seedNodes = Seq(
      "akka.tcp://ClusterSystem@127.0.0.1:2371",
      "akka.tcp://ClusterSystem@127.0.0.1:2372",
      "akka.tcp://ClusterSystem@127.0.0.1:2373",
//      "akka.tcp://ClusterSystem@127.0.0.1:2374"
    )

    seedNodes.indices.foreach { i => new KeyValue("ClusterSystem", 2371 + i, seedNodes.slice(0, i + 1)) }
  }
}
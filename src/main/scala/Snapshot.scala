import messages._

object Snapshot {

  final case class States(received: Map[String, String] = Map()) {
    def updatedAdd(kv: Set): States = copy(received + (kv.key -> kv.value))

    def updatedRemove(v: Remove): States = {
      if (received.contains(v.key)) {
        copy(received - v.key)
      } else {
        copy(received)
      }
    }

    def getItem(k: GetItem): ResultMessage = {
      if (received.contains(k.key)) {
        SuccessJob(received(k.key))
      } else {
        FailedJob("invalid key")
      }
    }

    def getAll: ResultMessage = {
      SuccessJob(received.toString())
    }

    override def toString: String = received.toString
  }

}

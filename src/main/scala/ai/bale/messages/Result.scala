package messages

sealed trait ResultMessage

case class SuccessJob(result: String) extends ResultMessage

case class FailedJob(reason: String) extends ResultMessage
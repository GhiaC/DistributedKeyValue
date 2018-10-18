package messages

sealed trait OperatorMessage

case class Set(key: String, value: Any) extends OperatorMessage

case class Remove(key: String) extends OperatorMessage

case class GetItem(key: String) extends OperatorMessage

case class Increase(key: String) extends OperatorMessage

case object GetAll extends OperatorMessage

case object BackendRegistration extends OperatorMessage
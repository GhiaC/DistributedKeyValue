package ai.bale

object Helper {

  class ExtendedString(s: String) {
    def isNumber: Boolean = s forall Character.isDigit
  }
}

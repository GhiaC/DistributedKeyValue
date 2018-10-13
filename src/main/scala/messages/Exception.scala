package messages

class RestartMeException extends Exception("RESTART")

class ResumeMeException extends Exception("RESUME")

class StopMeException extends Exception("STOP")
package eyesy

import eyesy.timer.{Clock, EyesyTimer, Ticks}
import slogging.{ConsoleLoggerFactory, LazyLogging, LogLevel, LoggerConfig}

object Main extends App with LazyLogging {

  import eyesy.timer.TimerState._

  def main(): Unit = {
    LoggerConfig.factory = ConsoleLoggerFactory()
    LoggerConfig.level = LogLevel.TRACE

    val settings = Settings(
      workTime = Ticks(40),
      breakTime = Ticks(5),
      longBreak = LongBreakSettings.Off,
      postponeTime = Ticks(5),
      idleChecking = IdleCheckingSettings.Off
    )
    val clock = Clock()

    val timer = new EyesyTimer(settings, clock)
    timer.listen { state =>
      logger.debug(state.toString)
    }
    timer.currentState match {
      case state: Stop => state.run()
      case _ =>
    }
  }

}

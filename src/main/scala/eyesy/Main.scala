package eyesy

import eyesy.timer.{Clock, EyesyTimer, Ticks}
import slogging.{ConsoleLoggerFactory, LazyLogging, LogLevel, LoggerConfig}

import scala.scalajs.js.JSApp

object Main extends JSApp with LazyLogging {

  import eyesy.timer.TimerState._

  def main(): Unit = {
    LoggerConfig.factory = ConsoleLoggerFactory()
    LoggerConfig.level = LogLevel.TRACE

    val settings = Settings(
      workTime = Ticks(15),
      breakTime = Ticks(5),
      longBreak = LongBreakSettings.Off,
      postponeTime = Ticks(5)
    )
    val clock = Clock()

    val timer = new EyesyTimer(settings, clock)
    timer.onStateChanged { state =>
      logger.debug(state.toString)
    }
    timer.currentState match {
      case state: Stop => state.run()
      case _ =>
    }
  }

}

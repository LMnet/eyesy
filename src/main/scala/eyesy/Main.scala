package eyesy

import eyesy.timer.Clock
import eyesy.ui.AppContainer
import org.scalajs.dom
import preact.Preact
import slogging.{ConsoleLoggerFactory, LazyLogging, LogLevel, LoggerConfig}

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    LoggerConfig.factory = ConsoleLoggerFactory()
    LoggerConfig.level = LogLevel.TRACE

    val clock = Clock()
    val settingsStorage = new BrowserLocalStorageSettingsStorage()(clock.singleTickDuration)

    val appDiv = dom.document.getElementById("app")
    val app = AppContainer(settingsStorage, clock.singleTickDuration)
    Preact.render(app, appDiv)

//    val timer = new EyesyTimer(settings, clock)
//    timer.listen { state =>
//      logger.debug(state.toString)
//    }
//    timer.currentState match {
//      case state: Stop => state.run()
//      case _ =>
//    }
  }

}

package eyesy.timer

import chrome.events.EventSourceController
import eyesy.Settings
import slogging.LazyLogging

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success, Try}

class EyesyTimer(settings: Settings, clock: Clock) extends EventSourceController[TimerState] with LazyLogging {

  private var state: TimerState = TimerState.Stop()(TimerState.Context(settings, clock))

  private val idleChecker = IdleChecker(settings.idleChecking)

  private def onNewState(newState: Try[TimerState]): Unit = {
    newState match {
      case Success(timerState) =>
        state = timerState
        emit(timerState)
        timerState.newState.onComplete(onNewState)
      case Failure(e) =>
        logger.error("EyesyTimer state error", e)
    }
  }

  def currentState: TimerState = state


  state.newState.onComplete(onNewState)
  idleChecker.onStateChanged.listen { idleState =>
    idleChecker.handleNewIdleState(idleState, state)
  }
}

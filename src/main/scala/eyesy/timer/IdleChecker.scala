package eyesy.timer

import chrome.events.{EventSource, Subscription}
import chrome.idle.bindings.State
import eyesy.IdleCheckingSettings
import eyesy.timer.TimerState.{Break, Pause}

import scala.concurrent.Future

sealed trait IdleChecker {
  val onStateChanged: EventSource[State]
  def handleNewIdleState(idleState: State, timerState: TimerState): Unit
}

object IdleChecker {

  def apply(settings: IdleCheckingSettings): IdleChecker = {
    if (settings.enabled) new NoOpIdleChecker
    else new IdleCheckerImpl(settings.pauseAfter, settings.stopOnSystemLocked)
  }

  private class NoOpIdleChecker extends IdleChecker {

    val onStateChanged: EventSource[State] = new EventSource[State] {
      override def listen(fn: (State) => Unit): Subscription = {
        new Subscription {
          override def cancel(): Unit = ()
        }
      }
    }

    def handleNewIdleState(idleState: State, timerState: TimerState): Unit = ()
  }

  private class IdleCheckerImpl(pauseAfter: Ticks, stopOnSystemLocked: Boolean) extends IdleChecker {

    import chrome.idle.{Idle => ChromeIdle}
    import chrome.idle.bindings.{State => IdleState}

    // Ticks and real seconds are not correspond to each other,
    // but in real runtime 1 tick always will be 1 second
    ChromeIdle.setDetectionInterval(pauseAfter.value)

    val onStateChanged: EventSource[State] = ChromeIdle.onStateChanged

    def handleNewIdleState(idleState: State, timerState: TimerState): Unit = {
      idleState match {
        case IdleState.IDLE => timerState match {
          case work: TimerState.Work => work.pause(Pause.Reason.Idle)
          case break: TimerState.Break => break.withConfirmation(Future.unit)
          case _ =>
        }
        case IdleState.ACTIVE => timerState match {
          case pause @ TimerState.Pause(Pause.Reason.Idle, _) => pause.resume()
          case break: TimerState.Break if break.state == Break.State.Ticking => break.withoutConfirmation()
          case _ =>
        }
        case IdleState.LOCKED => timerState match {
          case stoppable: StoppableTimerState if stopOnSystemLocked => stoppable.stop()
          case _ =>
        }
      }
    }
  }
}

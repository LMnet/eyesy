package eyesy.timer

import eyesy.{LongBreakSettings, Settings}

import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

sealed trait TimerState {

  implicit val context: TimerState.Context

  protected val newStatePromise: Promise[TimerState] = Promise[TimerState]()
  protected[timer] val newState: Future[TimerState] = newStatePromise.future
}

sealed trait PausableTimerState { self: TimerState => }
sealed trait StoppableTimerState { self: TimerState =>
  def stop(): Unit
}

object TimerState {

  case class Stop()(implicit val context: Context) extends TimerState {

    context.clock.stop()
    context.restart()

    def run(): Unit = {
      newStatePromise.success(Work())
    }
  }

  case class Work(workDuration: Option[Ticks] = None)(implicit val context: Context)
    extends TimerState with PausableTimerState with StoppableTimerState {

    context.clock.start()

    private var remainingTime: Ticks = workDuration.getOrElse(context.settings.workTime)

    context.clock.onTick { () =>
      remainingTime -= Ticks.single

      if (remainingTime.negativeOrZero) {
        newStatePromise.success(Break(context.nextBreakDuration))
      }
    }

    def stop(): Unit = {
      newStatePromise.success(Stop())
    }

    def pause(reason: Pause.Reason): Unit = {
      newStatePromise.success(Pause(reason, Work(Some(remainingTime))))
    }

    def restart(): Unit = {
      context.restart()
      newStatePromise.success(Work())
    }

    def postpone(): Unit = {
      remainingTime += context.settings.postponeTime
    }
  }

  case class Break(
    breakDuration: Ticks, confirmation: Option[Future[Unit]] = None
  )(implicit val context: Context) extends TimerState with PausableTimerState with StoppableTimerState {

    context.clock.start()

    private var remainingTime: Ticks = breakDuration

    private def finish() = {
      context.breakFinished()
      newStatePromise.success(Work())
    }

    context.clock.onTick { () =>
      remainingTime -= Ticks.single

      if (remainingTime.negativeOrZero) {
        confirmation match {
          case Some(confirm) =>
            stateVar = Break.State.WaitingConfirmation
            confirm.foreach { _ =>
              finish()
            }
          case None => finish()
        }
      }
    }

    private var stateVar: Break.State = Break.State.Ticking

    def state: Break.State = stateVar

    def stop(): Unit = {
      newStatePromise.success(Stop())
    }

    def pause(): Unit = {
      newStatePromise.success(Pause(Pause.Reason.Manual, Break(remainingTime, confirmation)))
    }

    def restart(): Unit = {
      context.restart()
      newStatePromise.success(Work())
    }

    def withConfirmation(confirm: Future[Unit]): Unit = {
      if (confirmation.isEmpty) {
        newStatePromise.success(Break(remainingTime, Some(confirm)))
      }
    }

    def withoutConfirmation(): Unit = {
      if (confirmation.nonEmpty) {
        newStatePromise.success(Break(remainingTime))
      }
    }
  }

  object Break {
    sealed trait State
    object State {
      case object Ticking extends State
      case object WaitingConfirmation extends State
    }
  }

  case class Pause(
    reason: Pause.Reason, previousState: TimerState with PausableTimerState
  )(implicit val context: Context) extends TimerState with StoppableTimerState {

    context.clock.stop()

    def resume(): Unit = {
      context.clock.start()
      newStatePromise.success(previousState)
    }

    def stop(): Unit = {
      newStatePromise.success(Stop())
    }

    def restart(): Unit = {
      context.restart()
      newStatePromise.success(Work())
    }
  }

  object Pause {
    sealed trait Reason
    object Reason{
      case object Idle extends Reason
      case object Manual extends Reason
    }
  }

  case class Context(settings: Settings, clock: Clock) {

    private var breaksAlreadyFinished: Int = 0

    def breakFinished(): Unit = {
      settings.longBreak match {
        case LongBreakSettings.On(breakAmountBeforeLongBreak, _) =>
          breaksAlreadyFinished += 1
          if (breaksAlreadyFinished > breakAmountBeforeLongBreak) {
            breaksAlreadyFinished = 0
          }
        case LongBreakSettings.Off =>
      }
    }

    def restart(): Unit = {
      breaksAlreadyFinished = 0
    }

    def nextBreakDuration: Ticks = {
      settings.longBreak match {
        case LongBreakSettings.On(breakAmountBeforeLongBreak, longBreakTime) =>
          if (breakAmountBeforeLongBreak >= breaksAlreadyFinished) {
            longBreakTime
          } else {
            settings.breakTime
          }
        case LongBreakSettings.Off =>
          settings.breakTime
      }
    }
  }
}

package eyesy.timer

import eyesy.{LongBreakSettings, Settings}

import scala.concurrent.{Future, Promise}

sealed trait TimerState {

  implicit val context: TimerState.Context

  protected val newStatePromise: Promise[TimerState] = Promise[TimerState]()
  protected[timer] val newState: Future[TimerState] = newStatePromise.future
}

sealed trait PausableTimerState extends TimerState

object TimerState {

  case class Stop()(implicit val context: Context) extends TimerState {

    context.clock.stop()
    context.restart()

    def run(): Unit = {
      newStatePromise.success(Work())
    }
  }

  case class Work()(implicit val context: Context) extends PausableTimerState {

    context.clock.start()

    private var remainingTime: Ticks = context.settings.workTime

    context.clock.onTick { () =>
      remainingTime -= Ticks.single

      if (remainingTime.negativeOrZero) {
        newStatePromise.success(Break(context.nextBreakDuration))
      }
    }

    def stop(): Unit = {
      newStatePromise.success(Stop())
    }

    def pause(): Unit = {
      newStatePromise.success(Pause(this))
    }

    def restart(): Unit = {
      context.restart()
      newStatePromise.success(Work())
    }

    def postpone(): Unit = {
      remainingTime += context.settings.postponeTime
    }
  }

  case class Break(breakDuration: Ticks)(implicit val context: Context) extends PausableTimerState {

    context.clock.start()

    private var remainingTime: Ticks = breakDuration

    context.clock.onTick { () =>
      remainingTime -= Ticks.single

      if (remainingTime.negativeOrZero) {
        context.breakFinished()
        newStatePromise.success(Work())
      }
    }

    def stop(): Unit = {
      newStatePromise.success(Stop())
    }

    def pause(): Unit = {
      newStatePromise.success(Pause(this))
    }

    def restart(): Unit = {
      context.restart()
      newStatePromise.success(Work())
    }
  }

  case class Pause(previousState: PausableTimerState)(implicit val context: Context) extends TimerState {

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

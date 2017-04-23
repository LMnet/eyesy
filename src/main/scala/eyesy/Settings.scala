package eyesy

import eyesy.timer.Ticks

case class Settings(
  workTime: Ticks,
  breakTime: Ticks,
  longBreak: LongBreakSettings,
  postponeTime: Ticks,
  idleChecking: IdleCheckingSettings
)

sealed trait LongBreakSettings
object LongBreakSettings {
  case object Off extends LongBreakSettings
  case class On(breakAmountBeforeLongBreak: Int, longBreakTime: Ticks) extends LongBreakSettings
}

sealed trait IdleCheckingSettings
object IdleCheckingSettings {
  case object Off extends IdleCheckingSettings
  case class On(pauseAfter: Ticks, stopOnSystemLocked: Boolean = true) extends IdleCheckingSettings {
    require(pauseAfter.value >= 15, "Chrome idle detection interval must not be less than 15")
  }
}

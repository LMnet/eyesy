package eyesy

import eyesy.timer.{SingleTickDuration, Ticks}

import concurrent.duration._

object Settings {

  def default(implicit singleTickDuration: SingleTickDuration): Settings = Settings(
    workTime = 20.minutes,
    breakTime = 3.minutes,
    postponeTime = 1.minute,
    longBreak = LongBreakSettings(enabled = true, 3, 5.minutes),
    idleChecking = IdleCheckingSettings(enabled = true, 30.seconds, stopOnSystemLocked = true)
  )
}

case class Settings(
  workTime: Ticks,
  breakTime: Ticks,
  postponeTime: Ticks,
  longBreak: LongBreakSettings,
  idleChecking: IdleCheckingSettings
)

case class LongBreakSettings(
  enabled: Boolean,
  breakAmountBeforeLongBreak: Int,
  longBreakTime: Ticks
)

case class IdleCheckingSettings(
  enabled: Boolean,
  pauseAfter: Ticks,
  stopOnSystemLocked: Boolean
) {
  require(pauseAfter.value >= 15, "Chrome idle detection interval must not be less than 15")
}

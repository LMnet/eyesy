package eyesy

import eyesy.timer.Ticks

case class Settings(
  workTime: Ticks,
  breakTime: Ticks,
  longBreak: LongBreakSettings,
  postponeTime: Ticks
)

sealed trait LongBreakSettings
object LongBreakSettings {
  case object Off extends LongBreakSettings
  case class On(breakAmountBeforeLongBreak: Int, longBreakTime: Ticks) extends LongBreakSettings
}

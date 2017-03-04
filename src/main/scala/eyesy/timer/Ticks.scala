package eyesy.timer

import scala.concurrent.duration.{Duration, FiniteDuration}

case class Ticks(value: Int) extends AnyVal {
  def -(that: Ticks): Ticks = Ticks(this.value - that.value)
  def +(that: Ticks): Ticks = Ticks(this.value + that.value)
  def negativeOrZero: Boolean = value <= 0
}

object Ticks {
  val single = Ticks(1)
}

case class SingleTickDuration(value: FiniteDuration) {
  require(value != Duration.Zero, "SingleTickDuration should be finite and non zero duration")
}

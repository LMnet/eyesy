package eyesy.timer

import scala.concurrent.duration._

case class Ticks(value: Int) extends AnyVal {
  def -(that: Ticks): Ticks = Ticks(this.value - that.value)
  def +(that: Ticks): Ticks = Ticks(this.value + that.value)
  def negativeOrZero: Boolean = value <= 0

  def format(implicit singleTickDuration: SingleTickDuration): String = {
    def formatTime(time: Long): String = {
      if (time >= 10) time.toString else s"0${time.toString}"
    }

    val duration = singleTickDuration.value * value.toLong
    if (duration >= 1.minute) {
      val minutes = duration.toMinutes
      val seconds = duration.toSeconds - (minutes * 60)
      s"${formatTime(minutes)}:${formatTime(seconds)}"
    } else {
      s"00:${formatTime(duration.toSeconds)}"
    }
  }
}

object Ticks {

  val single = Ticks(1)

  implicit def fromDuration(duration: FiniteDuration)(implicit singleTickDuration: SingleTickDuration): Ticks = {
    Ticks((duration / singleTickDuration.value).toInt)
  }
}

case class SingleTickDuration(value: FiniteDuration) {
  require(value != Duration.Zero, "SingleTickDuration should be finite non-zero duration")
}

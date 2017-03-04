package eyesy.timer

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle

case class Clock(singleTickDuration: SingleTickDuration = SingleTickDuration(1 second)) {

  private var setIntervalHandle = Option.empty[SetIntervalHandle]

  private var subscriber = Option.empty[() => Unit]

  def start(): Unit = {
    setIntervalHandle match {
      case Some(_) =>
      case None =>
        val handle = js.timers.setInterval(singleTickDuration.value) {
         subscriber.foreach { cb =>
           cb()
         }
        }
        setIntervalHandle = Some(handle)
    }
  }

  def stop(): Unit = {
    setIntervalHandle match {
      case Some(handle) => js.timers.clearInterval(handle)
      case None =>
    }
  }

  def onTick(cb: () => Unit): Unit = {
    subscriber = Some(cb)
  }
}

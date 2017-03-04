package eyesy.timer

import java.util.UUID

import eyesy.Settings
import slogging.LazyLogging

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success, Try}

object EyesyTimer {

  object SubscriptionId {
    def apply(): SubscriptionId = SubscriptionId(UUID.randomUUID())
  }

  case class SubscriptionId(value: UUID)
}

class EyesyTimer(settings: Settings, clock: Clock) extends LazyLogging {

  import EyesyTimer._

  private val subscribers = mutable.Map.empty[SubscriptionId, TimerState => Unit]

  private var state: TimerState = TimerState.Stop()(TimerState.Context(settings, clock))

  private def onNewState(newState: Try[TimerState]): Unit = {
    newState match {
      case Success(timerState) =>
        state = timerState
        subscribers.foreach { case (_, cb) =>
          cb(timerState)
        }
        timerState.newState.onComplete(onNewState)
      case Failure(e) =>
        logger.error("EyesyTimer state error", e)
    }
  }
  state.newState.onComplete(onNewState)

  def currentState: TimerState = state

  def onStateChanged(cb: TimerState => Unit): SubscriptionId = {
    val id = SubscriptionId()
    subscribers.update(id, cb)
    id
  }

  def removeSubscription(id: SubscriptionId): Unit = {
    subscribers.remove(id)
  }

}

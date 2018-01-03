package eyesy

import eyesy.timer.{SingleTickDuration, Ticks}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom
import slogging.LazyLogging

import scala.concurrent.Future

object SettingsStorage {

  import io.circe.generic.semiauto._

  implicit val settingsEncoder: Encoder[Settings] = deriveEncoder
  implicit val settingsDecoder: Decoder[Settings] = deriveDecoder

  implicit val ticksEncoder: Encoder[Ticks] = Encoder.encodeInt.contramap[Ticks](_.value)
  implicit val ticksDecoder: Decoder[Ticks] = Decoder.decodeInt.map(Ticks(_))

  implicit val longBreakSettingsEncoder: Encoder[LongBreakSettings] = deriveEncoder
  implicit val longBreakSettingsDecoder: Decoder[LongBreakSettings] = deriveDecoder

  implicit val idleCheckingSettingsEncoder: Encoder[IdleCheckingSettings] = deriveEncoder
  implicit val idleCheckingSettingsDecoder: Decoder[IdleCheckingSettings] = deriveDecoder
}

trait SettingsStorage {
  def singleTickDuration: SingleTickDuration
  def load(): Future[Settings]
  def save(settings: Settings): Future[Unit]
}

class BrowserLocalStorageSettingsStorage()(implicit val singleTickDuration: SingleTickDuration)
  extends SettingsStorage with LazyLogging {

  import SettingsStorage._

  private val key = "eyesy.settings"

  def load(): Future[Settings] = {
    Option(dom.window.localStorage.getItem(key)).map { savedString =>
      decode[Settings](savedString)
    } match {
      case Some(Right(res)) =>
        logger.debug(s"Loaded settings: $res")
        Future.successful(res)
      case Some(Left(e)) =>
        logger.error(s"Error while loading settings", e)
        Future.failed(e)
      case None =>
        val default = Settings.default
        logger.debug(s"There is no saved settings - using default: $default")
        Future.successful(default)
    }
  }

  def save(settings: Settings): Future[Unit] = {
    logger.debug(s"Saving settings: $settings")
    dom.window.localStorage.setItem(key, settings.asJson.noSpaces)
    Future.unit
  }
}

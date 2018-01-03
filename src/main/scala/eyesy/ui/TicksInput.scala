package eyesy.ui

import eyesy.timer.{SingleTickDuration, Ticks}
import eyesy.ui.TicksInput._
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent
import concurrent.duration._

import scala.util.matching.Regex

object TicksInput {

  case class Props(
    label: String,
    ticks: Ticks,
    onChange: Either[String, Ticks] => Unit,
    size: Size = Normal,
    customValidation: Ticks => Either[String, Ticks] = x => Right(x)
  )(implicit val singleTickDuration: SingleTickDuration)

  case class State(
    rawInput: String,
    error: Option[String]
  )

  sealed abstract class Size(val cssClass: String)
  case object Small extends Size("is-small")
  case object Normal extends Size("is-normal")
  case object Medium extends Size("is-medium")
  case object Large extends Size("is-large")

  val format: Regex = raw"(\d\d):(\d\d)".r

  def parse(value: String)(implicit singleTickDuration: SingleTickDuration): Either[String, Ticks] = {
    value match {
      case format(minutesStr, secondsStr) =>
        val minutes = minutesStr.toInt.minutes
        val seconds = secondsStr.toInt.seconds
        if (seconds >= 60.seconds) {
          Left("Seconds can't be bigger than 59")
        } else {
          Right(Ticks.fromDuration(minutes + seconds))
        }
      case _ =>
        Left("Time should be in 'xx:xx' format")
    }
  }
}

@PreactComponent[Props, State]
class TicksInput(initialProps: Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  initialState(State(
    rawInput = initialProps.ticks.format,
    error = None
  ))

  private def onInput(event: Event): Unit = {
    val rawInput = event.currentTarget.value

    val res: Either[String, Ticks] = for {
      parsed <- parse(rawInput)
      validated <- props.customValidation(parsed)
    } yield validated

    setState(State(rawInput, res match {
      case Right(_) => None
      case Left(error) => Some(error)
    }))
    props.onChange(res)
  }

  def render(): VNode = {
    import preact.dsl.tags._

    div(`class` := "field is-horizontal",
      div(`class` := s"field-label ${props.size.cssClass}",
        label(`class` := "label", props.label)
      ),
      div(`class` := "field-body",
        div(`class` := "field",
          div(`class` := "control",
            input(`class` := s"input ${props.size.cssClass} ${if (state.error.isDefined) "is-danger" else ""}",
              `type` := "text",
              maxlength := 5,
              size := 5,
              value := state.rawInput,
              onchange := onInput _
            )
          ),
          state.error match {
            case Some(err) =>
              p(`class` := "help is-danger", err)
            case None =>
              Entry.EmptyChild
          }
        )
      )
    )
  }
}

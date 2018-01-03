package eyesy.ui

import eyesy.LongBreakSettings
import eyesy.timer.SingleTickDuration
import eyesy.ui.LongBreakControl._
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

object LongBreakControl {

  case class Props(
    settings: LongBreakSettings,
    onChange: Either[String, LongBreakSettings] => Unit
  )(implicit val singleTickDuration: SingleTickDuration)
}

@PreactComponent[Props, Unit]
class LongBreakControl(initialProps: Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  def render(): VNode = {
    import preact.dsl.tags._

    val checkbox = {
      div(`class` := "field is-horizontal",
        div(`class` := "field-label",
          label(`class` := "label", "Long breaks")
        ),
        div(`class` := "field-body",
          div(`class` := "field",
            div(`class` := "control",
              label(`class` := "checkbox",
                input(`type` := "checkbox",
                  if (props.settings.enabled) checked else Entry.EmptyAttribute,
                  onchange := { event: Event =>
                    props.onChange(Right(props.settings.copy(enabled = event.currentTarget.checked)))
                  }
                ),
                " Enabled"
              )
            )
          )
        )
      )
    }

    def numberValidation(value: Int): Either[String, Int] = {
      if (value <= 0) Left("Breaks amount should be greater than zero")
      else Right(value)
    }

    val controls = props.settings match {
      case LongBreakSettings(true, breakAmountBeforeLongBreak, longBreakTime) =>
        Seq(
          NumberInput("Breaks amount before long break", breakAmountBeforeLongBreak, numberValidation, {
            case Right(number) => props.onChange(Right(props.settings.copy(breakAmountBeforeLongBreak = number)))
            case Left(error) => props.onChange(Left(error))
          }),
          TicksInput("Long break time", longBreakTime, {
            case Right(ticks) => props.onChange(Right(props.settings.copy(longBreakTime = ticks)))
            case Left(error) => props.onChange(Left(error))
          }, TicksInput.Small)
        )
      case _ =>
        Seq.empty
    }

    div(`class` := "field", Entry.Children(checkbox +: controls))
  }
}

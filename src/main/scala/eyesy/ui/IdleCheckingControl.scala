package eyesy.ui

import eyesy.IdleCheckingSettings
import eyesy.timer.{SingleTickDuration, Ticks}
import eyesy.ui.IdleCheckingControl._
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

object IdleCheckingControl {

  case class Props(
    settings: IdleCheckingSettings,
    onChange: Either[String, IdleCheckingSettings] => Unit
  )(implicit val singleTickDuration: SingleTickDuration)
}

@PreactComponent[Props, Unit]
class IdleCheckingControl(initialProps: Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  def render(): VNode = {
    import preact.dsl.tags._

    val checkbox = {
      div(`class` := "field is-horizontal",
        div(`class` := "field-label",
          label(`class` := "label", "Pause on idle")
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

    def idleValidation(ticks: Ticks): Either[String, Ticks] = {
      if (ticks.value >= 15) Right(ticks)
      else Left("Idle interval should be bigger than 15 seconds (Chrome API restriction)")
    }

    val controls = props.settings match {
      case IdleCheckingSettings(true, pauseAfter, stopOnSystemLocked) =>
        Seq(
          TicksInput("Pause after", pauseAfter, {
            case Right(number) => props.onChange(Right(props.settings.copy(pauseAfter = number)))
            case Left(error) => props.onChange(Left(error))
          }, TicksInput.Small, idleValidation),
          div(`class` := "field is-horizontal",
            div(`class` := "field-label is-small",
              label(`class` := "label", "Stop on system locked")
            ),
            div(`class` := "field-body",
              div(`class` := "field",
                div(`class` := "control",
                  label(`class` := "checkbox",
                    input(`type` := "checkbox",
                      if (stopOnSystemLocked) checked else Entry.EmptyAttribute,
                      onchange := { event: Event =>
                        props.onChange(Right(props.settings.copy(stopOnSystemLocked = event.currentTarget.checked)))
                      }
                    ),
                    " Enabled"
                  )
                )
              )
            )
          )
        )
      case _ =>
        Seq.empty
    }

    div(`class` := "field", Entry.Children(checkbox +: controls))
  }
}

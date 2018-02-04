package eyesy.ui

import eyesy.Settings
import eyesy.timer.SingleTickDuration
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

import scala.concurrent.Future

object SettingsPage {

  case class Props(
    settings: Settings,
    onSave: Settings => Future[Unit]
  )(implicit val singleTickDuration: SingleTickDuration)

  case class State(
    settings: Settings,
    isWorkTimeInvalid: Boolean = false,
    isBreakTimeInvalid: Boolean = false,
    isPostponeTimeInvalid: Boolean = false,
    isLongBreaksInvalid: Boolean = false,
    isIdleCheckingInvalid: Boolean = false
  ) {
    def containsInvalidInput: Boolean = {
      isWorkTimeInvalid ||
        isBreakTimeInvalid ||
        isPostponeTimeInvalid ||
        isLongBreaksInvalid ||
        isIdleCheckingInvalid
    }
  }
}

import SettingsPage._

@PreactComponent[SettingsPage.Props, SettingsPage.State]
class SettingsPage(initialProps: SettingsPage.Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  initialState(State(initialProps.settings))

  def render(): VNode = {
    import preact.dsl.tags._

    div(Entry.Children(Seq(
      h1(`class` := "title", "Settings"), // TODO: styles
      TicksInput("Work time", state.settings.workTime, {
        case Right(ticks) => setState(state.copy(
          settings = state.settings.copy(workTime = ticks),
          isWorkTimeInvalid = false
        ))
        case Left(_) => setState(state.copy(isWorkTimeInvalid = true))
      }),
      TicksInput("Break time", state.settings.breakTime, {
        case Right(ticks) => setState(state.copy(
          settings = state.settings.copy(breakTime = ticks),
          isBreakTimeInvalid = false
        ))
        case Left(_) => setState(state.copy(isBreakTimeInvalid = true))
      }),
      TicksInput("Postpone time", state.settings.postponeTime, {
        case Right(ticks) => setState(state.copy(
          settings = state.settings.copy(postponeTime = ticks),
          isPostponeTimeInvalid = false
        ))
        case Left(_) => setState(state.copy(isPostponeTimeInvalid = true))
      }),
      LongBreakControl(state.settings.longBreak, {
        case Right(longBreakSettings) => setState(state.copy(
          settings = state.settings.copy(longBreak = longBreakSettings),
          isLongBreaksInvalid = false
        ))
        case Left(_) => setState(state.copy(isLongBreaksInvalid = true))
      }),
      IdleCheckingControl(state.settings.idleChecking, {
        case Right(idleCheckingSettings) => setState(state.copy(
          settings = state.settings.copy(idleChecking = idleCheckingSettings),
          isIdleCheckingInvalid = false
        ))
        case Left(_) => setState(state.copy(isIdleCheckingInvalid = true))
      }),
      div(`class` := "field is-horizontal",
        div(`class` := s"field-label"),
        div(`class` := "field-body",
          div(`class` := "field is-grouped",
            div(`class` := "control",
              button(`class` := "button is-danger", "Cancel") // TODO: behavior
            ),
            div(`class` := "control",
              button(`class` := "button is-success", "Save",
                if (state.containsInvalidInput) disabled else Entry.EmptyAttribute,
                onclick := { _: Event =>
                  if (!state.containsInvalidInput) {
                    props.onSave(state.settings) // TODO: saving error?
                  }
                }
              )
            )
          )
        )
      )
    )))
  }
}

package eyesy.ui

import eyesy.{Settings, SettingsStorage}
import eyesy.timer.SingleTickDuration
import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success, Try}

object SettingsPage {

  case class Props(
    settingsStorage: SettingsStorage,
    singleTickDuration: SingleTickDuration
  )

  case class State(
    settings: Option[Try[Settings]],
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

@PreactComponent[SettingsPage.Props, SettingsPage.State]
class SettingsPage(initialProps: SettingsPage.Props) {

  import SettingsPage._

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  initialState {
    initialProps.settingsStorage.load().onComplete { res =>
      setState(State(Some(res)))
    }
    State(None)
  }

  private def onSave(settings: Settings): Unit = {
    props.settingsStorage.save(settings)
  }

  def render(): VNode = {
    import preact.dsl.tags._

    section(`class` := "section",
      state.settings match {
        case Some(Success(settings)) =>
          Entry.Children(Seq(
            h1(`class` := "title", "Settings"), // TODO
            TicksInput("Work time", settings.workTime, {
              case Right(ticks) => setState(state.copy(
                settings = Some(Success(settings.copy(workTime = ticks))),
                isWorkTimeInvalid = false
              ))
              case Left(_) => setState(state.copy(isWorkTimeInvalid = true))
            }),
            TicksInput("Break time", settings.breakTime, {
              case Right(ticks) => setState(state.copy(
                settings = Some(Success(settings.copy(breakTime = ticks))),
                isBreakTimeInvalid = false
              ))
              case Left(_) => setState(state.copy(isBreakTimeInvalid = true))
            }),
            TicksInput("Postpone time", settings.postponeTime, {
              case Right(ticks) => setState(state.copy(
                settings = Some(Success(settings.copy(postponeTime = ticks))),
                isPostponeTimeInvalid = false
              ))
              case Left(_) => setState(state.copy(isPostponeTimeInvalid = true))
            }),
            LongBreakControl(settings.longBreak, {
              case Right(longBreakSettings) => setState(state.copy(
                settings = Some(Success(settings.copy(longBreak = longBreakSettings))),
                isLongBreaksInvalid = false
              ))
              case Left(_) => setState(state.copy(isLongBreaksInvalid = true))
            }),
            IdleCheckingControl(settings.idleChecking, {
              case Right(idleCheckingSettings) => setState(state.copy(
                settings = Some(Success(settings.copy(idleChecking = idleCheckingSettings))),
                isIdleCheckingInvalid = false
              ))
              case Left(_) => setState(state.copy(isIdleCheckingInvalid = true))
            }),
            div(`class` := "field is-horizontal",
              div(`class` := s"field-label"),
              div(`class` := "field-body",
                div(`class` := "field is-grouped",
                  div(`class` := "control",
                    button(`class` := "button is-danger", "Cancel") // TODO
                  ),
                  div(`class` := "control",
                    button(`class` := "button is-success", "Save",
                      if (state.containsInvalidInput) disabled else Entry.EmptyAttribute,
                      onclick := { _: Event =>
                        if (!state.containsInvalidInput) onSave(settings)
                      }
                    )
                  )
                )
              )
            )
          ))

        case Some(Failure(e)) =>
          div(s"Something wrong: $e") // TODO

        case None =>
          div("Loading...") // TODO
      }
    )
  }
}

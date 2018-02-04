package eyesy.ui

import eyesy.timer.SingleTickDuration
import eyesy.{Settings, SettingsStorage}
import preact.Preact.VNode
import preact.macros.PreactComponent

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success, Try}

object AppContainer {

  case class Props(
    settingsStorage: SettingsStorage,
    singleTickDuration: SingleTickDuration
  )

  sealed trait Page
  object Page {
    case object Settings extends Page
    case object Timer extends Page
  }

  case class State(
    currentPage: Page,
    settings: Option[Try[Settings]]
  )
}

import eyesy.ui.AppContainer._

@PreactComponent[AppContainer.Props, AppContainer.State]
class AppContainer(initialProps: AppContainer.Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  //TODO: при старте приложения будет запускаться эта страница, загружаться настройки, потом открываться таймер с ссылкой на настройки. Логику по сохранению и загрузке настроек из хранилища перенести сюда.

  initialState {
    initialProps.settingsStorage.load().onComplete { res =>
      setState(state.copy(settings = Some(res)))
    }
    State(Page.Settings, None)
  }

  def render(): VNode = {
    import preact.dsl.tags._

    section(`class` := "section",
      state.settings match {
        case Some(Success(settings)) =>
          SettingsPage(settings, onSave = { settings =>
            props.settingsStorage.save(settings)
          })

        case Some(Failure(e)) =>
          div(s"Something wrong: $e") // TODO

        case None =>
          div("Loading...") // TODO
      }
    )
  }
}

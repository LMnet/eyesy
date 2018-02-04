package eyesy.ui

import eyesy.Settings
import eyesy.timer.SingleTickDuration
import preact.Preact.VNode
import preact.macros.PreactComponent

object TimerPage {

  case class Props(
    settings: Settings
  )(implicit val singleTickDuration: SingleTickDuration)
}

import TimerPage._

@PreactComponent[Props, Unit]
class TimerPage(initialProps: Props) {

  implicit val singleTickDuration: SingleTickDuration = initialProps.singleTickDuration

  def render(): VNode = {
    import preact.dsl.tags._

    //TODO: вёрстка очень сырая. Заюзать гриды?
    div(`class` := "timer-page",
      div(`class` := "timer", "05:16"),
      div(`class` := "main-button",
        i(`class` := "timer-page-icon main-icon fas fa-play-circle") // fa-pause-circle
      ),
      div(`class` := "stop-button",
        i(`class` := "timer-page-icon fas fa-stop-circle")
      ),
      div(`class` := "postpone-button",
        i(`class` := "timer-page-icon fas fa-plus-circle")
      ),
      div(`class` := "settings-page-button",
        a("Settings")
      )
    )
  }
}

package eyesy.ui

import org.scalajs.dom.Event
import preact.Preact.VNode
import preact.macros.PreactComponent

object NumberInput {

  case class Props(
    label: String,
    value: Int,
    validation: Int => Either[String, Int],
    onChange: Either[String, Int] => Unit
  )

  case class State(
    rawInput: Int,
    error: Option[String]
  )
}

@PreactComponent[NumberInput.Props, NumberInput.State]
class NumberInput(initialProps: NumberInput.Props) {

  import NumberInput._

  initialState(State(
    rawInput = initialProps.value,
    error = None
  ))

  private def onChange(event: Event): Unit = {
    val rawInput = event.currentTarget.valueAsNumber

    val res = props.validation(rawInput)

    setState(State(rawInput, res match {
      case Right(_) => None
      case Left(error) => Some(error)
    }))
    props.onChange(res)

  }

  def render(): VNode = {
    import preact.dsl.tags._

    div(`class` := "field is-horizontal",
      div(`class` := "field-label is-small",
        label(`class` := "label", props.label)
      ),
      div(`class` := "field-body",
        div(`class` := "field",
          div(`class` := "control",
            input(`class` := s"input is-small ${if (state.error.isDefined) "is-danger" else ""}",
              `type` := "number",
              min := "1",
              value := state.rawInput.toString,
              onchange := onChange _
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

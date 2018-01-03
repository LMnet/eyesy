package eyesy

import org.scalajs.dom.EventTarget
import org.scalajs.dom.raw.HTMLInputElement

package object ui {

  implicit def eventTarget2HtmlInputElement(eventTarget: EventTarget): HTMLInputElement = {
    eventTarget match {
      case x: HTMLInputElement => x
      case x => throw new IllegalArgumentException(s"Waiting for HTMLInputElement, but got $x")
    }
  }
}

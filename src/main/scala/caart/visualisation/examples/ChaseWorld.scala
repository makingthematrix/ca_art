package caart.visualisation.examples

import caart.Arguments
import caart.engine.fields.RGB
import caart.engine.{Automaton, Board}
import caart.examples.Chase
import caart.visualisation.{UserEvent, UserEventType, World}
import javafx.scene.paint.Color

import scala.util.Random

final class ChaseWorld(override val args: Arguments) extends World[Chase] {
  override val auto: Automaton[Chase] = Chase.automaton(args.dim)

  override protected def updateFromEvent(event: UserEvent): Board[Chase] = event.eventType match {
    case UserEventType.LeftClick =>
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      auto.updateOne(event.pos){ _.copy(color = color, brushes = List(color)) }
    case UserEventType.RightClick =>
      auto.update { _.copy(center = Some(event.pos)) }
  }

  override protected def toColor(c: Chase): Color =
    if (c.center.contains(c.pos)) Color.BLACK
    else {
      val rgb = c.color.toRGB
      Color.rgb(rgb.r, rgb.g, rgb.b)
    }
}

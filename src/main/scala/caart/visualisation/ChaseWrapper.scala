package caart.visualisation

import caart.Arguments
import caart.chase.Chase
import caart.engine.{Automaton, Board}
import caart.fields.RGB
import javafx.scene.paint.Color

import scala.util.Random

final class ChaseWrapper(override val args: Arguments) extends AutoWrapper[Chase] {
  override val auto: Automaton[Chase] = Chase.automaton(args.dim)

  override protected def updateFromEvent(event: UserEvent): Board[Chase] = event.eventType match {
    case UserEventType.LeftClick | UserEventType.MouseDrag =>
      auto.update { _.copy(center = Some(event.pos)) }
    case UserEventType.RightClick =>
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      auto.updateOne(event.pos){ _.copy(color = color, brushes = List(color)) }
    case _ =>
      auto.current
  }

  override protected def toColor(c: Chase): Color =
    if (c.center.contains(c.pos)) Color.BLACK
    else {
      val rgb = c.color.toRGB
      Color.rgb(rgb.r, rgb.g, rgb.b)
    }
}

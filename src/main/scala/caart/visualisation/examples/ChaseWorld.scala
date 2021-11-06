package caart.visualisation.examples

import caart.Arguments
import caart.engine.Automaton
import caart.engine.fields.RGB
import caart.examples.Chase
import caart.visualisation.{UserEvent, UserEventType, World}
import com.typesafe.scalalogging.LazyLogging
import javafx.scene.paint.Color

import scala.util.Random

final class ChaseWorld(override val args: Arguments) extends World[Chase] with LazyLogging {
  override val auto: Automaton[Chase] = Chase.automaton(args.dim)

  override protected def processUserEvent(event: UserEvent): Unit = event.eventType match {
    case UserEventType.LeftClick =>
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      auto.addEvent(event.pos, Chase.CreateChaser(color))
    case UserEventType.RightClick =>
      auto.addEvent(Chase.SetPlayer(event.pos))
      auto.addEvent(event.pos, Chase.SetPlayerHere)
  }

  override protected def toColor(cell: Chase): Color = {
     val rgb = auto.updatedByEvents(cell).getOrElse(cell).color.toRGB
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }
}

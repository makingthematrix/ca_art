package caart.visualisation.examples

import caart.Arguments
import caart.engine.Automaton
import caart.engine.fields.RGB
import caart.examples.{Chase, ChaseGlobal}
import caart.visualisation.{UserEvent, UserEventType, World}
import com.typesafe.scalalogging.LazyLogging
import javafx.scene.paint.Color

import scala.util.Random

final class ChaseWorld(override protected val args: Arguments) extends World[Chase, ChaseGlobal] with LazyLogging {
  override protected val auto: Automaton[Chase, ChaseGlobal] = Chase.automaton(args.dim)

  override protected def processUserEvent(event: UserEvent): Unit = event match {
    case UserEvent(Some(pos), UserEventType.LeftClick) =>
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      auto.eventHub ! (pos, Chase.CreateChaser(color))
    case UserEvent(Some(pos), UserEventType.RightClick) =>
      auto.eventHub ! Chase.SetPlayer(pos)
      auto.eventHub ! (pos, Chase.SetPlayerHere)
    case _ =>
  }

  override protected def toColor(cell: Chase): Color = {
     val rgb = auto.updatedByEvents(cell).getOrElse(cell).color.toRGB
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }
}

package caart.visualisation.examples

import caart.Arguments
import caart.engine.Automaton
import caart.engine.GlobalCell.Empty
import caart.examples.LangtonsAnt
import caart.visualisation.{UserEvent, World}
import javafx.scene.paint.Color

final class LangtonsAntWorld(override val args: Arguments) extends World[LangtonsAnt, Empty[LangtonsAnt]] {
  override val auto: Automaton[LangtonsAnt, Empty[LangtonsAnt]] = LangtonsAnt.automaton(args.dim)

  override protected def toColor(cell: LangtonsAnt): Color = {
    val c = auto.updatedByEvents(cell).getOrElse(cell)
    (c.color, c.dir) match {
      case (_, Some(_)) => Color.RED
      case (false, _)   => Color.WHITE
      case (true, _)    => Color.BLACK
    }
  }

  override protected def processUserEvent(event: UserEvent): Unit =
    auto.addEvent(event.pos, LangtonsAnt.CreateAnt)
}
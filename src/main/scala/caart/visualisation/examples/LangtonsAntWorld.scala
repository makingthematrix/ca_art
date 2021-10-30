package caart.visualisation.examples

import caart.Arguments
import caart.engine.fields.Up
import caart.engine.{Automaton, Board}
import caart.examples.LangtonsAnt
import caart.visualisation.{World, UserEvent}
import javafx.scene.paint.Color

final class LangtonsAntWorld(override val args: Arguments) extends World[LangtonsAnt] {
  override val auto: Automaton[LangtonsAnt] = LangtonsAnt.automaton(args.dim)

  override protected def toColor(c: LangtonsAnt): Color = (c.color, c.dir) match {
    case (_, Some(_)) => Color.RED
    case (false, _)   => Color.WHITE
    case (true, _)    => Color.BLACK
  }

  override protected def updateFromEvent(event: UserEvent): Board[LangtonsAnt] =
    auto.updateOne(event.pos) { cell => cell.copy(color = !cell.color, dir = Some(Up)) }
}
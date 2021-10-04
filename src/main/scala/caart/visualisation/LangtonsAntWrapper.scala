package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, Board}
import caart.fields.Up
import caart.langtonsant.LangtonsAnt
import javafx.scene.paint.Color

final class LangtonsAntWrapper (override val args: Arguments) extends AutoWrapper[LangtonsAnt] {
  override val auto: Automaton[LangtonsAnt] = LangtonsAnt.automaton(args.dim)

  override protected def toColor(c: LangtonsAnt): Color = (c.color, c.dir) match {
    case (_, Some(_)) => Color.RED
    case (false, _)   => Color.WHITE
    case (true, _)    => Color.BLACK
  }

  override protected def updateFromEvent(event: UserEvent): Board[LangtonsAnt] =
    auto.updateOne(event.pos) { cell => cell.copy(color = !cell.color, dir = Some(Up)) }
}

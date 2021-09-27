package caart.visualisation

import caart.Arguments
import caart.engine.Automaton
import caart.fields.{Pos2D, RGB, Up}
import caart.langtonsant.LangtonsAnt
import javafx.scene.paint.Color

class LangtonsAntWrapper (override val args: Arguments) extends AutoWrapper[LangtonsAnt] {
  override val auto: Automaton[LangtonsAnt] = LangtonsAnt.automaton(args.dim)

  override protected def toColor(c: LangtonsAnt): Color = (c.color, c.dir) match {
    case (_, Some(_)) => Color.RED
    case (false, _)   => Color.WHITE
    case (true, _)    => Color.BLACK
  }

  override def updateOne(pos: Pos2D): Unit = updateBoard {
    auto.updateOne(pos) { cell => cell.copy(color = !cell.color, dir = Some(Up)) }
  }
}

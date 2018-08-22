package langtonscell

import engine.AutomatonCell
import engine.Near.near4
import fields._

case class LangtonsCell(color: Boolean,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        override val findCell: (Pos2D) => LangtonsCell
                       )
  extends AutomatonCell[LangtonsCell] {

  override  def update: Option[LangtonsCell] = {
    val near = near4(this, findCell)
    if (dir.isEmpty && near.forall(_._2.dir.isEmpty)) None
    else (newColor, newDir(near)) match {
      case (c, d) if c == color && d == dir => None
      case (c, d)                           => Some(copy(color = c, dir = d))
    }
  }

  private def newColor = if (dir.isEmpty) color else !color

  private def newDir(near: Map[Dir2D, LangtonsCell]) = dir match {
    case None =>
      near.find {
        case (d, c) => c.dir.contains(d.turnAround)
      }.map {
        case (d, _) => if (color) d.turnLeft else d.turnRight
      }
    case _ => None
  }
}

object LangtonsCell {
  def apply(pos: Pos2D, neighbor: (Pos2D) => LangtonsCell): LangtonsCell = LangtonsCell(false, None, pos, neighbor)
}

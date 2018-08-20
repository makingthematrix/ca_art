package langtonscell

import engine.AutomatonCell
import engine.Near.near4
import fields._

case class LangtonsCell(color: Boolean,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        override val neighbor: (Pos2D) => LangtonsCell
                       )
  extends AutomatonCell[LangtonsCell] {

  override  def update: LangtonsCell = (newColor, newDir(near4(this, neighbor))) match {
    case (c, d) if c == color && d == dir => this
    case (c, d)                           => copy(color = c, dir = d)
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

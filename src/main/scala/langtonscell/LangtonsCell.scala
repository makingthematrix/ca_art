package langtonscell

import engine.{Automaton, AutomatonCell}
import fields._

case class LangtonsCell(color: WhiteBlack,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        private val auto: Automaton[LangtonsCell])
  extends AutomatonCell[LangtonsCell] {

  def newColor: WhiteBlack = dir.fold(color)(_ => color.toggle)

  def newDir(near: Map[Dir2D, LangtonsCell]): Option[Dir2D] = dir match {
    case None =>
      near.find {
        case (d, c) => c.dir.contains(d.turnAround)
      }.map {
        case (d, _) => color match {
          case White => d.turnRight
          case Black => d.turnLeft
        }
      }
    case _ => None
  }

  override  def update: LangtonsCell = auto.near(this) match {
    case near if dir.isEmpty && near.values.forall(_.dir.isEmpty) => this
    case near => copy(color = newColor, dir = newDir(near))
  }
}

object LangtonsCell {
  def apply(pos: Pos2D, world: Automaton[LangtonsCell]): LangtonsCell = LangtonsCell(White, None, pos, world)
}

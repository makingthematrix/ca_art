package langtonscolors

import engine.{Automaton, AutomatonCell, Board}
import fields.{Color, Dir2D, Pos2D}
import engine.Near.near4

case class LangtonsColors(colors: Set[Color],
                     dirs: List[(Dir2D, Color)],
                     override val pos: Pos2D,
                     override val findCell: Pos2D => LangtonsColors)
  extends AutomatonCell[LangtonsColors] {

  override  def update: Option[LangtonsColors] = {
    val near = near4(this, findCell)
    if (dirs.isEmpty && near.forall(_._2.dirs.isEmpty)) None
    else (newColors, newDirs(near)) match {
      case (cs, ds) if cs == colors && ds == dirs => None
      case (cs, ds)                               => Some(copy(colors = cs, dirs = ds))
    }
  }

  private def newColors = {
    val newColors = dirs.map(_._2).toSet
    colors.union(newColors).diff(colors.intersect(newColors)) // no generic xor?
  }

  private def newDirs(near: Map[Dir2D, LangtonsColors]) =
    near.toList.flatMap {
      case (thisDir, cell) => cell.dirs.filter(_._1 == thisDir.turnAround)
    }.map {
      case (thatDir, color) =>
        (if (colors.contains(color)) thatDir.turnLeft else thatDir.turnRight, color)
    }
}


object LangtonsColors {
  def apply(pos: Pos2D, findCell: Pos2D => LangtonsColors): LangtonsColors = LangtonsColors(Set.empty, List.empty, pos, findCell)

  def automaton(dim: Int)(init: Board[LangtonsColors] => Board[LangtonsColors]): Automaton[LangtonsColors] = {
    new Automaton[LangtonsColors](dim, init, Board.apply[LangtonsColors], apply)
  }
}

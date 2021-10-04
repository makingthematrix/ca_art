package caart.examples

import caart.engine.fields.{CMYK, Dir2D, Pos2D}
import caart.engine.{AutomatonCell, AutomatonCreator, Neighborhood}

final case class LangtonsColors(override val pos: Pos2D,
                                override val findCell: Pos2D => LangtonsColors,
                                colors: Set[CMYK] = Set.empty,
                                dirs: List[(Dir2D, CMYK)] = Nil) extends AutomatonCell[LangtonsColors] {
  override def needsUpdate: Boolean =
    dirs.nonEmpty || Neighborhood.neumann(this).exists(p => p._2.dirs.nonEmpty)

  override  def update: Option[LangtonsColors] = (newColors, newDirs) match {
    case (cs, ds) if cs == colors && ds == dirs => None
    case (cs, ds)                               => Some(copy(colors = cs, dirs = ds))
  }

  private def newColors = {
    val newColors = dirs.map(_._2).toSet
    (colors | newColors) &~ (colors & newColors) // no generic xor?
  }

  private def newDirs = Neighborhood.neumann(this).toList.flatMap {
    case (thisDir, cell) => cell.dirs.filter(_._1 == thisDir.turnAround)
  }.map {
    case (thatDir, color) if colors.contains(color) => (thatDir.turnRight, color)
    case (thatDir, color) =>                           (thatDir.turnLeft, color)
  }
}

object LangtonsColors extends AutomatonCreator[LangtonsColors] {
  def cell(pos: Pos2D, findCell: Pos2D => LangtonsColors): LangtonsColors = LangtonsColors(pos, findCell)
}

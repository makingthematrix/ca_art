package caart.examples

import caart.engine.GlobalCell.EmptyGlobalCell
import caart.engine.fields.{CMYK, Dir2D, Pos2D}
import caart.engine.{Automaton, AutomatonCell, AutomatonCreator, GlobalCell}

final case class LangtonsColors(override val pos: Pos2D,
                                override val auto: Automaton[LangtonsColors],
                                colors: Set[CMYK] = Set.empty,
                                dirs: Map[Dir2D, CMYK] = Map.empty)
  extends AutomatonCell[LangtonsColors] {
  override type GC = EmptyGlobalCell

  override def needsUpdate: Boolean =
    dirs.nonEmpty || auto.neumann(pos).exists(_._2.dirs.nonEmpty)

  override  def update: Option[LangtonsColors] = (newColors, newDirs) match {
    case (cs, ds) if cs == colors && ds == dirs => None
    case (cs, ds)                               => Some(copy(colors = cs, dirs = ds))
  }

  private def newColors = {
    val newColors = dirs.values.toSet
    (colors | newColors) &~ (colors & newColors) // no generic xor?
  }

  private def newDirs =
    auto.neumann(pos).flatMap {
      case (thisDir, cell) => cell.dirs.filter(_._1 == thisDir.turnAround)
    }.map {
      case (thatDir, color) if colors.contains(color) => (thatDir.turnRight, color)
      case (thatDir, color) =>                           (thatDir.turnLeft, color)
    }
}

object LangtonsColors extends AutomatonCreator[LangtonsColors] {
  override def cell(pos: Pos2D, auto: Automaton[LangtonsColors]): LangtonsColors = LangtonsColors(pos, auto)
  override def globalCell( auto: Automaton[LangtonsColors]): EmptyGlobalCell = GlobalCell.Empty
}

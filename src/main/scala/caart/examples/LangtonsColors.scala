package caart.examples

import caart.engine.fields.{CMYK, Dir2D, Pos2D}
import caart.engine.{Automaton, Cell}

final case class LangtonsColors(override val pos: Pos2D,
                                override val auto: Cell.AutoContractNoGlobal[LangtonsColors],
                                colors: Set[CMYK] = Set.empty,
                                dirs: Map[Dir2D, CMYK] = Map.empty) extends Cell.NoGlobal[LangtonsColors] {
  override type CE = LangtonsColors.CreateAnt

  override def needsSelfUpdate: Boolean =
    dirs.nonEmpty || auto.neumann(pos).exists(_._2.dirs.nonEmpty)

  override  def selfUpdate: Option[LangtonsColors] = (newColors, newDirs) match {
    case (`colors`, `dirs`) => None
    case (cs, ds)           => Some(copy(colors = cs, dirs = ds))
  }

  override def updateFromEvents(events: Iterable[LangtonsColors.CreateAnt]): Option[LangtonsColors] =
    events.headOption.map {
      case LangtonsColors.CreateAnt(color, dir) => copy(colors = Set(color), dirs = Map(dir -> color))
    }

  private def newColors = {
    val newColors = dirs.values.toSet
    (colors | newColors) &~ (colors & newColors) // no generic xor?
  }

  private def newDirs = auto.neumann(pos).flatMap {
    case (thisDir, cell) => cell.dirs.filter(_._1 == thisDir.turnAround)
  }.map {
    case (thatDir, color) if colors.contains(color) => (thatDir.turnRight, color)
    case (thatDir, color) =>                           (thatDir.turnLeft, color)
  }
}

object LangtonsColors extends Automaton.CreatorNoGlobal[LangtonsColors] {
  override def cell(pos: Pos2D, auto: Cell.AutoContractNoGlobal[LangtonsColors]): LangtonsColors = LangtonsColors(pos, auto)
  final case class CreateAnt(color: CMYK, dir: Dir2D) extends Cell.Event
}

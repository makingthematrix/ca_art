package caart.examples

import caart.engine.fields.{Dir2D, Pos2D}
import caart.engine.{Automaton, Cell}

final case class LangtonsAnt(override val pos: Pos2D,
                             override val auto: Cell.AutoContractNoGlobal[LangtonsAnt],
                             color: Boolean = false,
                             dir: Option[Dir2D] = None) extends Cell.NoGlobal[LangtonsAnt] {
  override type CE = LangtonsAnt.CreateAnt.type

  override def needsSelfUpdate: Boolean = dir.isDefined || auto.neumann(pos).exists(_._2.dir.isDefined)

  override  def selfUpdate: Option[LangtonsAnt] = (newColor, newDir) match {
    case (`color`, `dir`) => None
    case (c, d)           => Some(copy(color = c, dir = d))
  }

  override def updateFromEvents(events: Iterable[LangtonsAnt.CreateAnt.type]): Option[LangtonsAnt] =
    if (events.nonEmpty) Some(copy(dir = Some(Dir2D.Up))) else None

  private def newColor = if (dir.isEmpty) color else !color

  private def newDir = auto.neumann(pos).find {
    case (thisDir, cell) => cell.dir.contains(thisDir.turnAround)
  }.map {
    case (thisDir, _) if color => thisDir.turnLeft
    case (thisDir, _)          => thisDir.turnRight
  }
}

object LangtonsAnt extends Automaton.CreatorNoGlobal[LangtonsAnt] {
  override def cell(pos: Pos2D, auto: Cell.AutoContractNoGlobal[LangtonsAnt]): LangtonsAnt = LangtonsAnt(pos, auto)
  case object CreateAnt extends Cell.Event
}

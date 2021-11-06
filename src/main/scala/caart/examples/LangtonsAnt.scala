package caart.examples

import caart.engine.GlobalCell.Empty
import caart.engine.{Automaton, Cell, GlobalCell}
import caart.engine.fields.{Dir2D, Pos2D, Up}

final case class LangtonsAnt(override val pos: Pos2D,
                             override val auto: Cell.AutoContract[LangtonsAnt, Empty[LangtonsAnt]],
                             color: Boolean = false,
                             dir: Option[Dir2D] = None)
  extends Cell[LangtonsAnt] {
  import LangtonsAnt._
  override type GC = Empty[LangtonsAnt]
  override type CE = CreateAnt.type

  /* In case of Langton's Ant in every iteration only a small part of the board is updated
   * ( `2*n / (dim*dim)` where `n` is the number of ants on the board). We can speed it up
   * by overriding `needsSelfUpdate` with a quick check if the selfUpdate is needed at all.
   */
  override def needsSelfUpdate: Boolean =
    dir.isDefined || auto.neumann(pos).exists(_._2.dir.isDefined)

  override  def selfUpdate: Option[LangtonsAnt] = (newColor, newDir) match {
    case (`color`, `dir`) => None
    case (c, d)           => Some(copy(color = c, dir = d))
  }

  override def updateFromEvents(events: Iterable[CreateAnt.type]): Option[LangtonsAnt] =
    if (events.nonEmpty) Some(copy(dir = Some(Up))) else None

  private def newColor = if (dir.isEmpty) color else !color

  private def newDir =
    auto.neumann(pos).find {
      case (thisDir, cell) => cell.dir.contains(thisDir.turnAround)
    }.map {
      case (thisDir, _) if color => thisDir.turnLeft
      case (thisDir, _)          => thisDir.turnRight
    }
}

object LangtonsAnt extends Automaton.Creator[LangtonsAnt, Empty[LangtonsAnt]] {
  override def cell(pos: Pos2D, auto: Cell.AutoContract[LangtonsAnt, Empty[LangtonsAnt]]): LangtonsAnt = LangtonsAnt(pos, auto)
  override def globalCell(auto: GlobalCell.AutoContract[LangtonsAnt, Empty[LangtonsAnt]]): Empty[LangtonsAnt] = GlobalCell.empty

  case object CreateAnt extends Cell.Event
}

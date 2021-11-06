package caart.examples

import caart.engine.GlobalCell.Empty
import caart.engine.{Automaton, Cell, GlobalCell}
import caart.engine.fields.Pos2D

final case class GameOfLife(override val pos: Pos2D,
                            override val auto: Cell.AutoContract[GameOfLife],
                            life: Boolean = false)
  extends Cell[GameOfLife] {
  import caart.examples.GameOfLife._
  override type GC = Empty[GameOfLife]
  override type CE = FlipCell.type

  override def updateFromEvents(events: Iterable[FlipCell.type]): Option[GameOfLife] =
    if (events.size % 2 == 1) Some(copy(life = !life)) else None

  override def selfUpdate: Option[GameOfLife] =
    auto.moore(pos).values.count(_.life) match {
      case 3 if !life => Some(copy(life = true))
      case n if life && (n < 2 || n > 3) => Some(copy(life = false))
      case _ => None
    }
}

object GameOfLife extends Automaton.Creator[GameOfLife] {
  override def cell(pos: Pos2D, auto: Cell.AutoContract[GameOfLife]): GameOfLife = GameOfLife(pos, auto)
  override def globalCell(auto: GlobalCell.AutoContract[GameOfLife]): Empty[GameOfLife] = GlobalCell.empty

  case object FlipCell extends Cell.Event
}

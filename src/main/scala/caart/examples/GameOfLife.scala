package caart.examples

import caart.engine.fields.Pos2D
import caart.engine.{Automaton, Cell}

final case class GameOfLife(override val pos: Pos2D,
                            override val auto: Cell.AutoContractNoGlobal[GameOfLife],
                            life: Boolean = false)
  extends Cell.NoGlobal[GameOfLife] {
  import caart.examples.GameOfLife._
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

object GameOfLife extends Automaton.CreatorNoGlobal[GameOfLife] {
  override def cell(pos: Pos2D, auto: Cell.AutoContractNoGlobal[GameOfLife]): GameOfLife = GameOfLife(pos, auto)

  case object FlipCell extends Cell.Event
}

package caart.examples

import caart.engine.GlobalCell.EmptyGlobalCell
import caart.engine.{Automaton, AutomatonCell, AutomatonCreator, GlobalCell}
import caart.engine.fields.Pos2D

final case class GameOfLife(override val pos: Pos2D,
                            override val auto: Automaton[GameOfLife],
                            life: Boolean = false)
  extends AutomatonCell[GameOfLife] {
  override type GC = EmptyGlobalCell

  override def update: Option[GameOfLife] =
    auto.moore(pos).values.count(_.life) match {
      case 3 if !life                    => Some(copy(life = true))
      case n if life && (n < 2 || n > 3) => Some(copy(life = false))
      case _                             => None
    }
}

object GameOfLife extends AutomatonCreator[GameOfLife] {
  override def cell(pos: Pos2D, auto: Automaton[GameOfLife]): GameOfLife = GameOfLife(pos, auto)
  override def globalCell: EmptyGlobalCell = GlobalCell.Empty
}

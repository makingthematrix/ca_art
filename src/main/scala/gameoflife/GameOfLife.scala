package gameoflife

import engine.{Automaton, Board, AutomatonCell, Neighborhood}
import fields.Pos2D
import Neighborhood.moore

case class GameOfLife(life: Boolean,
                      override val pos: Pos2D,
                      override val findCell: Pos2D => GameOfLife)
  extends AutomatonCell[GameOfLife] {

  override def update: Option[GameOfLife] = moore(this).count { case (_, c) => c.life } match {
    case 3 if !life => Some(copy(life = true))
    case n if life && (n < 2 || n > 3) => Some(copy(life = false))
    case _ => None
  }

}

object GameOfLife {
  def apply(pos: Pos2D, findCell: Pos2D => GameOfLife): GameOfLife = GameOfLife(life = false, pos, findCell)
    
  def automaton(dim: Int): Automaton[GameOfLife] = new Automaton[GameOfLife](dim, apply, Board.apply)
}
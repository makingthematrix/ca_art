package gameoflife

import engine.{Automaton, AutomatonCell, Board, Near}
import fields.Pos2D

case class GameOfLife(life: Boolean,
                      override val pos: Pos2D,
                      override val findCell: Pos2D => GameOfLife)
  extends AutomatonCell[GameOfLife] {

  override def update: Option[GameOfLife] = Near.near8(this).count { case (_, gol) => gol.life } match {
    case 3 if !life => Some(copy(life = true))
    case n if life && (n < 2 || n > 3) => Some(copy(life = false))
    case _ => None
  }

}

object GameOfLife {
  def apply(pos: Pos2D, findCell: Pos2D => GameOfLife): GameOfLife = GameOfLife(life = false, pos, findCell)

  def automaton(dim: Int)(init: Board[GameOfLife] => Board[GameOfLife] = board => board): Automaton[GameOfLife] =
    new Automaton[GameOfLife](dim, init, Board.apply[GameOfLife], apply)
}
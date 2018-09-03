package engine

import fields.Pos2D

class Automaton[CA <: AutomatonCell[CA]](dim: Int,
                                         buildBoard: (Int, Pos2D => CA) => Board[CA],
                                         buildCell: (Pos2D, Pos2D => CA) => CA)
  extends Iterator[Board[CA]] {

  private var board: Board[CA] = buildBoard( dim, buildCell(_, board.findCell(_)) )

  override def next(): Board[CA] = {
    board = board.next
    board
  }

  override def hasNext: Boolean = true

  def update(updater: Board[CA] => Board[CA]): Board[CA] = {
    board = updater(board)
    board
  }

}

object Automaton {
  def apply[CA <: AutomatonCell[CA]](dim: Int, buildCell: (Pos2D, Pos2D => CA) => CA): Automaton[CA] =
    new Automaton[CA](dim, Board.apply[CA], buildCell)

}

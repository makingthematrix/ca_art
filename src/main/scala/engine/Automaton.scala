package engine

import fields.Pos2D

class Automaton[CA <: AutomatonCell[CA]](dim: Int,
                                         buildCell: (Pos2D, Pos2D => CA) => CA,
                                         buildBoard: (Int, Pos2D => CA) => Board[CA] = Board.apply[CA](_, _)
                                        )
  extends Iterator[Board[CA]] {

  private var board: Board[CA] = buildBoard(dim, buildCell(_, board.findCell(_)))

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


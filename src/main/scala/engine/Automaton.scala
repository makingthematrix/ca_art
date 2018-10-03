package engine

import fields.Pos2D

class Automaton[C <: AutomatonCell[C]](dim: Int,
                                       applyCell:  (Pos2D, Pos2D => C) => C,
                                       applyBoard: (Int, Pos2D => C)   => Board[C] = Board.apply[C] _
                                      ) extends Iterator[Board[C]] {
  private var board: Board[C] = applyBoard(dim, applyCell(_, board.findCell(_)))

  override def next(): Board[C] = {
    board = board.next
    board
  }

  override def hasNext: Boolean = true

  def update(updater: C => C): Board[C] = {
    board = board.copy(updater)
    board
  }
}


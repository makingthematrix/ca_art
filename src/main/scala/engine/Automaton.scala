package engine

import fields.Pos2D

class Automaton[CA <: AutomatonCell[CA]](dim: Int,
                                         init: Board[CA] => Board[CA],
                                         buildBoard: (Int, (Pos2D) => CA) => Board[CA],
                                         buildCell: (Pos2D, (Pos2D) => CA) => CA) {

  private var board: Board[CA] = init(buildBoard(dim, buildCell(_, board.findCell(_)) ))

  val iterator: Iterator[Board[CA]] = new Iterator[Board[CA]] {
    override def hasNext: Boolean = true

    override def next(): Board[CA] = {
      board = board.next
      board
    }
  }
}

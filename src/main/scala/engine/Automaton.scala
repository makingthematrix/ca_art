package engine

import fields.Pos2D

class Automaton[CA <: AutomatonCell[CA]](dim: Int,
                                         init: Board[CA] => Board[CA],
                                         build: (Pos2D, (Pos2D) => CA) => CA) {

  private var board: Board[CA] = init(Board(dim){ build(_, board.get(_)) })

  val iterator: Iterator[Board[CA]] = new Iterator[Board[CA]] {
    override def hasNext: Boolean = true

    override def next(): Board[CA] = {
      board = board.update
      board
    }
  }
}

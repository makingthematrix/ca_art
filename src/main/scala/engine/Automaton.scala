package engine

import fields.{Dir2D, Pos2D}

class Automaton[CA <: AutomatonCell[CA]](dim: Int,
                                         init: Board[CA] => Board[CA],
                                         build: (Pos2D, Automaton[CA]) => CA) {

  private var board: Board[CA] = init(Board(dim){ build(_, this) })

  val iterator: Iterator[Board[CA]] = new Iterator[Board[CA]] {
    override def hasNext: Boolean = true

    override def next(): Board[CA] = {
      board = board.update
      board
    }
  }

  def near(ca: CA): Map[Dir2D, CA] = board.near(ca)
}

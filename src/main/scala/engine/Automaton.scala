package engine

import fields.{Dir2D, Pos2D}

import scala.collection.immutable.StreamIterator

class Automaton[CA <: AutomatonCell[CA]](gridDimension: Int,
                                         init: Board[CA] => Board[CA],
                                         build: (Pos2D, Automaton[CA]) => CA) {
  def near(ca: CA): Map[Dir2D, CA] = boards(ca.generation).near(ca)

  lazy val boards: Stream[Board[CA]] = {
    init(Board(gridDimension) {
      build(_, this)
    }) #:: boards.map(_.update)
  }

  val iterator = new StreamIterator[Board[CA]](boards)
/*
  private var board: Board[CA] = init(Board(gridDimension){ build(_, this) })

  val iterator = new Iterator[Board[CA]] {
    override def hasNext: Boolean = true

    override def next(): Board[CA] = {
      board = board.update
      board
    }
  }

  def near(ca: CA): Map[Dir2D, CA] = board.near(ca)*/
}

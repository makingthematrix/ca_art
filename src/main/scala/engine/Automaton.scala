package engine

import fields.Pos2D

import scala.collection.immutable.StreamIterator

class Automaton[CA <: AutomatonCell[CA]](gridDimension: Int,
                                         init: Grid[CA] => Grid[CA],
                                         build: (Pos2D, Automaton[CA]) => CA) {
  def grid(generation: Int): Grid[CA] = grids(generation)

  private lazy val grids: Stream[Grid[CA]] =
    init(Grid(gridDimension){ build(_, this) }) #:: grids.map(_.update)

  lazy val iterator = new StreamIterator[Grid[CA]](grids)
}

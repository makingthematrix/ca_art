package engine

import fields.Pos2D
import scala.collection.parallel.immutable.ParMap

class Board[CA <: AutomatonCell[CA]](dim: Int,
                                     private val map: ParMap[Int, CA]
                                    ) {
  def findCell(pos: Pos2D): CA = map(Board.id(pos, dim))
  protected def newBoard(map: ParMap[Int, CA]): Board[CA] = new Board(dim, map)

  def next: Board[CA] = newBoard(map.map { case (k, c) => k -> c.update.getOrElse(c) })

  def copy(pos: Pos2D)(updater: CA => CA): Board[CA] = {
    val id = Board.id(pos, dim)
    newBoard(map = map + (id -> updater(map(id))))
  }

  def values: IndexedSeq[CA] = map.values.toIndexedSeq

  def -(board: Board[CA]): IndexedSeq[CA] = Pos2D(dim).flatMap { p =>
    val c = findCell(p)
    if (c != board.findCell(p)) Some(c) else None
  }
}

object Board {
  def id(pos: Pos2D, dim: Int): Int = {
    def wrap(i: Int) = i % dim match {
      case x if x >= 0 => x
      case x           => x + dim
    }

    wrap(pos.x) * dim + wrap(pos.y)
  }

  def buildMap[CA <: AutomatonCell[CA]](dim: Int, build: Pos2D => CA): ParMap[Int, CA] =
    Pos2D(dim).foldLeft(ParMap.empty[Int, CA])((map, pos) => map + (id(pos, dim) -> build(pos)))

  def apply[CA <: AutomatonCell[CA]](dim: Int, build: Pos2D => CA): Board[CA] = new Board(dim, buildMap(dim, build))
}

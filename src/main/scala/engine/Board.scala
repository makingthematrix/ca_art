package engine

import fields.Pos2D
import scala.collection.parallel.immutable.ParMap

class Board[CA <: AutomatonCell[CA]](dim: Int, protected val map: ParMap[Int, CA]) {
  def findCell(pos: Pos2D): CA = map(Board.id(pos, dim))

  def next: Board[CA] = new Board(dim, map.map { case (k, c) => k -> c.update.getOrElse(c) })

  def copy(pos: Pos2D)(updater: CA => CA): Board[CA] = {
    val id = Board.id(pos, dim)
    new Board(dim, map.updated(id, updater(map(id))))
  }

  def copy(updater: CA => CA): Board[CA] = new Board(dim, map.map { case (id, cell) => (id, updater(cell)) })

  def values: List[CA] = map.values.toList

  def -(board: Board[CA]): List[CA] = Pos2D(dim).flatMap { p =>
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

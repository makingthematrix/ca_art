package engine

import fields.Pos2D
import scala.collection.parallel.immutable.ParMap

class Board[C <: AutomatonCell[C]](dim: Int, protected val map: ParMap[Int, C]) {
  def findCell(pos: Pos2D): C = map(Board.id(pos, dim))

  def next: Board[C] = new Board(dim, map.map { case (k, c) => k -> c.update.getOrElse(c) })

  def copy(pos: Pos2D)(updater: C => C): Board[C] = {
    val id = Board.id(pos, dim)
    new Board(dim, map.updated(id, updater(map(id))))
  }

  def copy(updater: C => C): Board[C] = new Board(dim, map.map { case (id, cell) => (id, updater(cell)) })

  def values: List[C] = map.values.toList

  def -(board: Board[C]): List[C] = Pos2D(dim).flatMap { p =>
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

  def buildMap[CA <: AutomatonCell[CA]](dim: Int, applyCell: Pos2D => CA): ParMap[Int, CA] =
    Pos2D(dim).foldLeft(ParMap.empty[Int, CA])((map, pos) => map + (id(pos, dim) -> applyCell(pos)))

  def apply[CA <: AutomatonCell[CA]](dim: Int, applyCell: Pos2D => CA): Board[CA] = new Board(dim, buildMap(dim, applyCell))
}

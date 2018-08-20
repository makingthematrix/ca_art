package engine

import fields.Pos2D
import scala.collection.parallel.immutable.ParMap

case class Board[CA <: AutomatonCell[CA]](dim: Int, private val map: ParMap[Int, CA]) {
  def get(pos: Pos2D): CA = map(Board.id(pos, dim))

  def update: Board[CA] = copy(map = map.map { case (k, c) => k -> c.update })

  def update(pos: Pos2D)(updater: CA => CA): Board[CA] = {
    val id = Board.id(pos, dim)
    copy(map = map + (id -> updater(map(id))))
  }

  def values: IndexedSeq[CA] = map.values.toIndexedSeq

  def -(board: Board[CA]): IndexedSeq[CA] = Pos2D(dim).flatMap { p =>
    val cell = get(p)
    if (cell != board.get(p)) Some(cell) else None
  }
}

object Board {
  private[Board] def id(pos: Pos2D, dim: Int) = {
    def wrap(i: Int) = i % dim match {
      case x if x >= 0 => x
      case x           => x + dim
    }

    wrap(pos.x) * dim + wrap(pos.y)
  }

  def apply[CA <: AutomatonCell[CA]](dim: Int)(build: Pos2D => CA): Board[CA] = {
    val map = Pos2D(dim).foldLeft(ParMap.empty[Int, CA])((map, pos) => map + (id(pos, dim) -> build(pos)))
    Board(dim, map)
  }
}

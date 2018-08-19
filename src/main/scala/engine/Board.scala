package engine

import fields.{Dir2D, Pos2D}

import scala.collection.parallel.immutable.ParMap

case class Board[CA <: AutomatonCell[CA]](dim: Int, private val map: ParMap[Int, CA]) {
  private def id(pos: Pos2D) = Board.id(pos, dim)

  def get(pos: Pos2D): CA = map(id(pos))

  def update: Board[CA] = copy(map = map.map { case (k, c) => k -> c.update })

  def update(pos: Pos2D)(updater: CA => CA): Board[CA] = id(pos) match {
    case id => copy(map = map + (id -> updater(map(id))))
  }

  def near(cell: CA): Map[Dir2D, CA] = Dir2D.dirs.map(dir => dir -> get(cell.pos.move(dir))).toMap

  def values: Iterable[CA] = map.values.toIndexedSeq

  def -(board: Board[CA]): Iterable[CA] =
    (0 until dim)
      .flatMap(x => (0 until dim).map(y => Pos2D(x, y)))
      .flatMap { p =>
        val cell = get(p)
        if (cell == board.get(p)) None
        else Some(cell)
      }
}

object Board {

  private[Board] def id(pos: Pos2D, dim: Int) = {
    def wrap(i: Int) = i % dim match {
      case x if x >= 0 => x
      case x           => dim + x
    }

    wrap(pos.x) * dim + wrap(pos.y)
  }

  def apply[CA <: AutomatonCell[CA]](dim: Int)(build: Pos2D => CA): Board[CA] = {
    val map = (0 until dim)
      .flatMap(x => (0 until dim).map(y => Pos2D(x, y)))
      .foldLeft(ParMap.empty[Int, CA])((map, pos) => map + (id(pos, dim) -> build(pos)))
    Board(dim, map)
  }
}

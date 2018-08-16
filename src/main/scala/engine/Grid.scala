package engine

import fields.{Dir2D, Pos2D}

case class Grid[CA <: AutomatonCell[CA]](dim: Int, map: Map[Int, CA]) {

  def get(pos: Pos2D): CA = map(id(pos))

  def update: Grid[CA] = copy(map = map.mapValues(_.update))

  def update(pos:Pos2D)(updater: CA => CA): Grid[CA] = id(pos) match {
    case id => copy(map = map + (id -> map(id)))
  }

  def near(cell: CA): Map[Dir2D, CA] = Dir2D.dirs.map(dir => dir -> get(cell.pos.move(dir))).toMap

  private def id(pos: Pos2D) = Grid.id(pos, dim)
}

object Grid {

  private[Grid] def id(pos: Pos2D, dim: Int) = {
    def wrap(i: Int) = i % dim match {
      case pos if pos >= 0 => pos
      case neg             => dim + neg
    }

    wrap(pos.x) * dim + wrap(pos.y)
  }

  def apply[CA <: AutomatonCell[CA]](dim: Int)(build: Pos2D => CA): Grid[CA] = {
    val map = (0 until dim)
      .flatMap(x => (0 until dim).map(y => Pos2D(x, y)))
      .foldLeft(Map.empty[Int, CA])((map, pos) => map + (id(pos, dim) -> build(pos)))
    Grid(dim, map)
  }
}

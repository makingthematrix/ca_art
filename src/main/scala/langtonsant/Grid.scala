package langtonsant

case class Grid(dim: Int, map: Map[Int, LangtonsAnt]) {
  def update: Grid = copy(map = map.mapValues(_.update))
  def foreach(f: (LangtonsAnt) => Unit): Unit = map.values.foreach(f)

  private def id(x: Int, y: Int) = Grid.id(x, y, dim)

  def get(x: Int, y: Int): LangtonsAnt = map(id(x, y))

  def update(x: Int, y: Int, updater: (LangtonsAnt) => LangtonsAnt): Grid = id(x, y) match {
    case id => copy(map = map + (id -> map(id)))
  }

  def allNear(cell: LangtonsAnt): Map[Dir2D, LangtonsAnt] =
    Map(
      Up    -> get(cell.x, cell.y - 1),
      Right -> get(cell.x + 1, cell.y),
      Down  -> get(cell.x, cell.y + 1),
      Left  -> get(cell.x - 1, cell.y)
    )
}

object Grid {

  private[Grid] def id(x: Int, y: Int, dim: Int) = {
    def wrap(i: Int) = i % dim match {
      case pos if pos >= 0 => pos
      case neg             => dim + neg
    }

    wrap(x) * dim + wrap(y)
  }

  def apply(dim: Int, world: World): Grid = {
    val map = (0 until dim)
      .flatMap(x => (0 until dim).map(y => (x, y)))
      .foldLeft(Map.empty[Int, LangtonsAnt])((map, t) => map + (id(t._1, t._2, dim) -> LangtonsAnt(t._1, t._2, world)))
    Grid(dim, map)
  }
}

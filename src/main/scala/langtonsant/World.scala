package langtonsant

class World(gridDimension: Int) {
  def grid(generation: Int): Grid = grids(generation)

  lazy val grids: Stream[Grid] = Grid(gridDimension, this) #:: grids.map(_.update)
}
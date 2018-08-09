package langtonsant

case class LangtonsAnt(color: WhiteBlack,
                       dir: Option[Dir2D],
                       private val pos: (Int, Int),
                       private val world: World,
                       private val generation: Int) {

  private lazy val grid = world.grid(generation)

  def updateColor: WhiteBlack = dir match {
    case Some(_) => color.toggle
    case _       => color
  }

  def updateDir: Option[Dir2D] = dir match {
    case Some(antDir) if grid.allNear(this).exists { case (d, c) => c.dir.contains(d.turnAround) } =>
      Some(color match {
        case White => antDir.turnLeft
        case Black => antDir.turnRight
      })
    case _ => None
  }

  def update: LangtonsAnt = copy(color = updateColor, dir = updateDir, generation = generation + 1)

  val x = pos._1
  val y = pos._2
}

object LangtonsAnt {
  def apply(x: Int, y: Int, world: World): LangtonsAnt = LangtonsAnt(White, None, (x, y), world, 0)
  def ant(x: Int, y: Int, world: World): LangtonsAnt = LangtonsAnt(White, Some(Up), (x, y), world, 0)
}


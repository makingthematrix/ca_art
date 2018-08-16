package fields

case class Pos2D(x: Int, y: Int) {
  def move(dir: Dir2D): Pos2D = dir match {
    case Up    => copy(y = y - 1)
    case Right => copy(x = x + 1)
    case Down  => copy(y = y + 1)
    case Left  => copy(x = x - 1)
  }
}

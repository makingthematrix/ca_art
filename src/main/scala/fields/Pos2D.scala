package fields

import scala.util.Random

case class Pos2D(x: Int, y: Int) {
  def move(dir: Dir2D): Pos2D = dir match {
    case Up    => copy(y = y - 1)
    case Right => copy(x = x + 1)
    case Down  => copy(y = y + 1)
    case Left  => copy(x = x - 1)
  }
}

object Pos2D {
  def apply(dim: Int): IndexedSeq[Pos2D] = (0 until dim).flatMap(x => (0 until dim).map(Pos2D(x, _)))

  def random(dim: Int): Pos2D = Pos2D(Random.nextInt(dim), Random.nextInt(dim))
}

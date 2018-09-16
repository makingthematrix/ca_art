package fields

import scala.util.Random

case class Pos2D(x: Int, y: Int) {
  def move(dir: Dir2D): Pos2D = copy(x + dir.x.toInt, y + dir.y.toInt)

  def dir(other: Pos2D) = Dir2D(other.x - x, other.y - y)
  def -(other: Pos2D) = other.dir(this)
}

object Pos2D {
  def apply(dim: Int): List[Pos2D] = (0 until dim).flatMap(x => (0 until dim).map(Pos2D(x, _))).toList

  def range(p1: Pos2D, p2: Pos2D): List[Pos2D] = {
    val xfrom = math.min(p1.x, p2.x)
    val yfrom = math.min(p1.y, p2.y)
    val xto = math.max(p1.x, p2.x)
    val yto = math.max(p1.y, p2.y)

    (xfrom to xto).flatMap(x => (yfrom to yto).map(Pos2D(x, _))).toList
  }

  def random(dim: Int): Pos2D = Pos2D(Random.nextInt(dim), Random.nextInt(dim))
}

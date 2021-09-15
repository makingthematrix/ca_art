package fields

import scala.util.Random

/**
* Yet another representation of a position in 2D :) 
* I decided to implement it myself, because it allowed me to introduce
* a few important utility methods. I think that as long as conversion
* to other representations is trivial, implementing such simple case
* classes is totally ok. They may be reduntant, but they simplify a lot.
*/
final case class Pos2D(x: Int, y: Int) {
  // If Dir2D is one of the Dir2D constants, the method returns  
  // the adjacent position in the given direction. 
  // Used to collect the neighborhood of the cell with the given `pos`.
  def move(dir: Dir2D): Pos2D = copy(x + dir.x.toInt, y + dir.y.toInt)

  // Returns the direction (normalized) vector between the two positions.
  def dir(other: Pos2D): Dir2D = Dir2D(other.x - x, other.y - y)
  def -(other: Pos2D): Dir2D = other.dir(this)
}

object Pos2D {
  // Returns all positions for a square board with the given edge length `dim` (exclusive).
  def apply(dim: Int): List[Pos2D] = (0 until dim).flatMap(x => (0 until dim).map(Pos2D(x, _))).toList

  // Returns all positions for a square board between two positions (inclusive)    
  def range(p1: Pos2D, p2: Pos2D): List[Pos2D] = {
    val xfrom = math.min(p1.x, p2.x)
    val yfrom = math.min(p1.y, p2.y)
    val xto = math.max(p1.x, p2.x)
    val yto = math.max(p1.y, p2.y)

    (xfrom to xto).flatMap(x => (yfrom to yto).map(Pos2D(x, _))).toList
  }

  // Returns a random position on a board of the given size 
  def random(dim: Int): Pos2D = Pos2D(Random.nextInt(dim), Random.nextInt(dim))
}

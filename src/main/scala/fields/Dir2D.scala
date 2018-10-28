package fields

/**
Similarly to `Pos2D` this is a simple representation of a 2D vector.
It can be used in two ways: as a normalized vector between two positions,
and as such it can be held as a field in a cell and used to compute
its new state, or it can denote a spatial relation between two cells.
In the second case we deal usually only with constants: Up, Right, Down, etc.
*/
class Dir2D(val x: Double, val y: Double) {
  def turnRight: Dir2D = new Dir2D(-y, x)
  def turnLeft:  Dir2D = new Dir2D(y, -x)
  def turnAround:Dir2D = new Dir2D(-x, -y)

  // returns the cross product of this vector and another
  def crossZ(other: Dir2D): Double = x * (other.y - y) - y * (other.x - x)
  
    
  // calculating the cross product is an easy way to tell if the other vector
  // is clockwise or counterclockwise from this one
  // `crossZ` is 0.0 if the vectors are parallel
  def rightOf(other: Dir2D): Boolean = crossZ(other) < 0
  def leftOf(other: Dir2D): Boolean = crossZ(other) > 0

  def approx4: Dir2D = Dir2D.approx4(x, y)
  def approx8: Dir2D = Dir2D.approx8(x, y)

  override def equals(obj: scala.Any): Boolean = obj match {
    case dir: Dir2D => dir.x == x && dir.y == y
    case _ => false
  }

  override def hashCode: Int = x.hashCode + y.hashCode

  override def toString: String = s"Dir2D(${x.round(3)}, ${y.round(3)})"
}

case object Up    extends Dir2D(0.0, -1.0) {
  override lazy val turnRight: Dir2D   = Right
  override lazy val turnLeft : Dir2D   = Left
  override lazy val turnAround : Dir2D = Down

  override def toString: String = "Up"
}

case object Right extends Dir2D(1.0, 0.0) {
  override lazy val turnRight: Dir2D   = Down
  override lazy val turnLeft : Dir2D   = Up
  override lazy val turnAround : Dir2D = Left

  override def toString: String = "Right"
}

case object Down  extends Dir2D(0.0, 1.0) {
  override lazy val turnRight: Dir2D   = Left
  override lazy val turnLeft : Dir2D   = Right
  override lazy val turnAround : Dir2D = Up

  override def toString: String = "Down"
}

case object Left  extends Dir2D(-1.0, 0.0) {
  override lazy val turnRight: Dir2D   = Up
  override lazy val turnLeft : Dir2D   = Down
  override lazy val turnAround : Dir2D = Right

  override def toString: String = "Left"
}

case object UpLeft extends Dir2D(-1.0, -1.0) {
  override lazy val turnRight: Dir2D   = UpRight
  override lazy val turnLeft : Dir2D   = DownLeft
  override lazy val turnAround : Dir2D = DownRight

  override def toString: String = "UpLeft"
}

case object UpRight extends Dir2D(1.0, -1.0) {
  override lazy val turnRight: Dir2D   = DownRight
  override lazy val turnLeft : Dir2D   = UpLeft
  override lazy val turnAround : Dir2D = DownLeft

  override def toString: String = "UpRight"
}

case object DownRight extends Dir2D(1.0, 1.0) {
  override lazy val turnRight: Dir2D = DownLeft
  override lazy val turnLeft : Dir2D = UpRight
  override lazy val turnAround : Dir2D = UpLeft

  override def toString: String = "DownRight"
}

case object DownLeft extends Dir2D(-1.0, 1.0) {
  override lazy val turnRight: Dir2D = UpLeft
  override lazy val turnLeft : Dir2D = DownRight
  override lazy val turnAround : Dir2D = UpRight

  override def toString: String = "DownLeft"
}

object Dir2D {
  val dirs4 = Array(Up, Right, Down, Left)
  val dirs8 = Array(Up, UpRight, Right, DownRight, Down, DownLeft, Left, UpLeft)

  def apply(x: Double, y: Double): Dir2D = {
    require(x != 0.0 || y != 0.0, "Unable to create a Dir2D from (x = 0.0, y = 0.0)")
    (x, y) match {
      case (0.0, _) if y < 0.0 => Up
      case (_, 0.0) if x > 0.0 => Right
      case (0.0, _) if y > 0.0 => Down
      case (_, 0.0) if x < 0.0 => Left
      case _ if x == y && x < 0.0   => UpLeft
      case _ if x == -y && x > 0.0  => UpRight
      case _ if x == y && x > 0.0   => DownRight
      case _ if x == -y && x < 0.0  => DownLeft
      case _  =>
        val den = math.sqrt(x * x + y * y)
        new Dir2D(x / den, y / den)
    }
  }

  def approx4(x: Double, y: Double) =
    if (x > 0.0) {
      if (y > x) Down
      else if (y < -x) Up
      else Right
    } else {
      if (y < x) Up
      else if (y > -x) Down
      else Left
    }

  def approx8(x: Double, y: Double) = {
    val absX = math.abs(x)
    if (y < 0.0) {
      if (2.0 * absX < -y) Up
      else if (absX < -2.0 * y) {
        if (x < 0.0) UpLeft else UpRight
      } else {
        if (x < 0.0) Left else Right
      }
    } else {
      if (2.0 * absX < y) Down
      else if (absX < 2.0 * y) {
        if (x < 0.0) DownLeft else DownRight
      } else {
        if (x < 0.0) Left else Right
      }
    }
  }

  def unapply(dir: Dir2D): Option[(Double, Double)] = Some((dir.x, dir.y))
}
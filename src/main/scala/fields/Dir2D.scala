package fields

class Dir2D(val x: Double, val y: Double) {
  def turnRight: Dir2D = new Dir2D(-y, x)
  def turnLeft:  Dir2D = new Dir2D(y, -x)
  def turnAround:Dir2D = new Dir2D(-x, -y)

  def crossZ(other: Dir2D): Double = x * (other.y - y) - y * (other.x - x)
  def rightOf(other: Dir2D): Boolean = crossZ(other) < 0
  def leftOf(other: Dir2D): Boolean = crossZ(other) > 0

  def approx4: Dir2D = Dir2D.approx4(x, y)
  def approx8: Dir2D = Dir2D.approx8(x, y)

  override def equals(obj: scala.Any): Boolean = obj match {
    case dir: Dir2D => dir.x == x && dir.y == y
    case _ => false
  }

  override def hashCode: Int = x.hashCode + y.hashCode
}

case object Up    extends Dir2D(0.0, -1.0) {
  override lazy val turnRight: Dir2D   = Right
  override lazy val turnLeft : Dir2D   = Left
  override lazy val turnAround : Dir2D = Down
}

case object Right extends Dir2D(1.0, 0.0) {
  override lazy val turnRight: Dir2D   = Down
  override lazy val turnLeft : Dir2D   = Up
  override lazy val turnAround : Dir2D = Left
}

case object Down  extends Dir2D(0.0, 1.0) {
  override lazy val turnRight: Dir2D   = Left
  override lazy val turnLeft : Dir2D   = Right
  override lazy val turnAround : Dir2D = Up
}

case object Left  extends Dir2D(-1.0, 0.0) {
  override lazy val turnRight: Dir2D   = Up
  override lazy val turnLeft : Dir2D   = Down
  override lazy val turnAround : Dir2D = Right
}

case object UpLeft extends Dir2D(-1.0, -1.0) {
  override lazy val turnRight: Dir2D   = UpRight
  override lazy val turnLeft : Dir2D   = DownLeft
  override lazy val turnAround : Dir2D = DownRight
}

case object UpRight extends Dir2D(1.0, -1.0) {
  override lazy val turnRight: Dir2D   = DownRight
  override lazy val turnLeft : Dir2D   = UpLeft
  override lazy val turnAround : Dir2D = DownLeft
}

case object DownRight extends Dir2D(1.0, 1.0) {
  override lazy val turnRight: Dir2D = DownLeft
  override lazy val turnLeft : Dir2D = UpRight
  override lazy val turnAround : Dir2D = UpLeft
}

case object DownLeft extends Dir2D(-1.0, 1.0) {
  override lazy val turnRight: Dir2D = UpLeft
  override lazy val turnLeft : Dir2D = DownRight
  override lazy val turnAround : Dir2D = UpRight
}

object Dir2D {
  val dirs4 = Array(Up, Right, Down, Left)
  val dirs8 = Array(Up, UpRight, Right, DownRight, Down, DownLeft, Left, UpLeft)

  def apply(x: Double, y: Double): Dir2D = {
    require(x != 0.0 || y != 0.0, "Unable to create a Dir2D from (x = 0.0, y = 0.0)")
    (x, y) match {
      case (0.0, yi) if yi < 0.0 => Up
      case (xi, 0.0) if xi > 0.0 => Right
      case (0.0, yi) if yi > 0.0 => Down
      case (xi, 0.0) if xi < 0.0 => Left
      case (xi, yi) if xi == yi && xi < 0.0  => UpLeft
      case (xi, yi) if xi == -yi && xi > 0.0 => UpRight
      case (xi, yi) if xi == yi && xi > 0.0  => DownRight
      case (xi, yi) if xi == -yi && xi <0.0  => DownLeft
      case (xi, yi)  =>
        val den = math.sqrt(xi * xi + yi * yi)
        new Dir2D(xi / den, yi / den)
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

  def unapply(dir: Dir2D): (Double, Double) = (dir.x, dir.y)
}
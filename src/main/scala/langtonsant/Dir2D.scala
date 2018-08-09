package langtonsant

sealed trait Dir2D {
  def index: Int
  def turnRight: Dir2D
  def turnLeft: Dir2D
  def turnAround:Dir2D
}

case object Up    extends Dir2D {
  override val index: Int = 0
  override lazy val turnRight: Dir2D = Right
  override lazy val turnLeft : Dir2D = Left
  override lazy val turnAround : Dir2D = Down
}

case object Right extends Dir2D {
  val index: Int = 1
  override lazy val turnRight: Dir2D = Down
  override lazy val turnLeft : Dir2D = Up
  override lazy val turnAround : Dir2D = Left
}

case object Down  extends Dir2D {
  val index: Int = 2
  override lazy val turnRight: Dir2D = Left
  override lazy val turnLeft : Dir2D = Right
  override lazy val turnAround : Dir2D = Up
}

case object Left  extends Dir2D {
  val index: Int = 3
  override lazy val turnRight: Dir2D = Up
  override lazy val turnLeft : Dir2D = Down
  override lazy val turnAround : Dir2D = Right
}

object Dir2D {
  val dirs = List(Up, Right, Down, Left)

  implicit def fromInt(index: Int): Option[Dir2D] = index match {
    case 0 => Some(Up)
    case 1 => Some(Right)
    case 2 => Some(Down)
    case 3 => Some(Left)
    case _ => None
  }
}
package fields

sealed trait Dir2D {
  def turnRight: Dir2D
  def turnLeft:  Dir2D
  def turnAround:Dir2D
}

case object Up    extends Dir2D {
  override lazy val turnRight: Dir2D = Right
  override lazy val turnLeft : Dir2D = Left
  override lazy val turnAround : Dir2D = Down
}

case object Right extends Dir2D {
  override lazy val turnRight: Dir2D = Down
  override lazy val turnLeft : Dir2D = Up
  override lazy val turnAround : Dir2D = Left
}

case object Down  extends Dir2D {
  override lazy val turnRight: Dir2D = Left
  override lazy val turnLeft : Dir2D = Right
  override lazy val turnAround : Dir2D = Up
}

case object Left  extends Dir2D {
  override lazy val turnRight: Dir2D = Up
  override lazy val turnLeft : Dir2D = Down
  override lazy val turnAround : Dir2D = Right
}

case object UpLeft extends Dir2D {
  override lazy val turnRight: Dir2D = UpRight
  override lazy val turnLeft : Dir2D = DownLeft
  override lazy val turnAround : Dir2D = DownRight
}

case object UpRight extends Dir2D {
  override lazy val turnRight: Dir2D = DownRight
  override lazy val turnLeft : Dir2D = UpLeft
  override lazy val turnAround : Dir2D = DownLeft
}

case object DownRight extends Dir2D {
  override lazy val turnRight: Dir2D = DownLeft
  override lazy val turnLeft : Dir2D = UpRight
  override lazy val turnAround : Dir2D = UpLeft
}

case object DownLeft extends Dir2D {
  override lazy val turnRight: Dir2D = UpLeft
  override lazy val turnLeft : Dir2D = DownRight
  override lazy val turnAround : Dir2D = UpRight
}

object Dir2D {
  val dirs4 = Array(Up, Right, Down, Left)
  val dirs8 = Array(Up, UpRight, Right, DownRight, Down, DownLeft, Left, UpLeft)
}
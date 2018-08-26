package fields

import math.min

case class Color(r: Int, g: Int, b: Int) {
  def +(c: Color) : Color = Color(min(r + c.r, 255),min(b + c.b, 255), min(b + c.b, 255))
}

object Color {
  val White = Color(255, 255, 255)
  val Black = Color(0, 0, 0)

  val Red = Color(255, 0, 0)
  val Orange = Color(255, 165, 0)
  val Yellow = Color(255, 255, 0)
  val Green = Color(0, 255, 0)
  val Blue = Color(0, 0, 255)
  val Indigo = Color(0, 28, 200)
  val Violet = Color(128, 0, 255)

  val monotone = List(White, Black)
  val rainbow = List(Red, Orange, Yellow, Green, Blue, Indigo, Violet)

  val all = monotone ::: rainbow
}

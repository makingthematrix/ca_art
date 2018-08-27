package fields

import math.min

case class RGB(r: Int, g: Int, b: Int) {
  def +(c: RGB) : RGB = RGB(min(r + c.r, 255),min(b + c.b, 255), min(b + c.b, 255))

  def toCMYK: CMYK = this match {
    case RGB.White => CMYK.White
    case RGB.Black => CMYK.Black
    case _ =>
      val c = 1.0 - (r / 255.0)
      val m = 1.0 - (g / 255.0)
      val y = 1.0 - (b / 255.0)
      val k = Array(c, m, y).min
      if (k >= 1.0) CMYK.Black
      else CMYK((c - k)/(1.0 - k), (m- k)/(1.0 - k), (y - k)/(1.0 - k), k)
  }
}

object RGB {
  val White = RGB(255, 255, 255)
  val Black = RGB(0, 0, 0)

  val Red = RGB(255, 0, 0)
  val Orange = RGB(255, 165, 0)
  val Yellow = RGB(255, 255, 0)
  val Green = RGB(0, 255, 0)
  val Blue = RGB(0, 0, 255)
  val Indigo = RGB(0, 28, 200)
  val Violet = RGB(128, 0, 255)

  val monotone = List(White, Black)
  val rainbow = List(Red, Orange, Yellow, Green, Blue, Indigo, Violet)

  val all = monotone ::: rainbow
}

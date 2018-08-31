package fields

import math.round

case class CMYK(c: Double, m: Double, y: Double, k: Double) {
  def +(other: CMYK): CMYK = CMYK.sum(Seq(this, other))

  def toRGB: RGB = this match {
    case CMYK.White => RGB.White
    case CMYK.Black => RGB.Black
    case _ =>
      RGB(
        round(255.0 * (1.0 - c) * (1.0 -k)).toInt,
        round(255.0 * (1.0 - m) * (1.0 -k)).toInt,
        round(255.0 * (1.0 - y) * (1.0 -k)).toInt
      )
  }
}

object CMYK {
  def apply(c: Double, m: Double, y: Double): CMYK = CMYK(c, m, y, 0.0)

  val Cyan = CMYK(1.0, 0.0, 0.0)
  val Magenta = CMYK(0.0, 1.0, 0.0)
  val Yellow = CMYK(0.0, 0.0, 1.0)

  val colors = Array(Cyan, Magenta, Yellow)

  val Black = CMYK(0.0, 0.0, 0.0, 1.0)
  val White = CMYK(0.0, 0.0, 0.0)

  def sum(colors: Traversable[CMYK]): CMYK =
    sum(Vector(colors.map(c => c.c + c.k).sum, colors.map(c => c.m + c.k).sum, colors.map(c => c.y + c.k).sum))

  private[CMYK] def sum(vec: Vector[Double]): CMYK = {
    val max = vec.max
    val narr = vec.map(_ / max)
    val nk = narr.min
    if (nk >= 1.0) CMYK.Black else {
      val nnarr = narr.map(n => (n - nk) / (1.0 - nk))
      CMYK(nnarr(0), nnarr(1), nnarr(2), nk)
    }
  }
}

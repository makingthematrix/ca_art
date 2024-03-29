package caart.engine.fields

import math.round

/**
* While RGB is what we use to describe colors made from mixing light of different wavelength,
* we can think of CMYK as a description of colors made from mixing paint. The main difference
* is that in RGB the "zero" color (the identity) is black, and adding colors to it we move 
* towards white, while in CMYK the identity is the white color of the canvas on which we paint.
* Mixing colors on the canvas moves us towards black. However, CMYK is not a simple inversion
* of RGB. The methods used to convert between the two can be very complex - what I use here is 
* probably the simplest one.
* 
* In CA Art, CMYK is used to visualize the state of a cell. This way we can treat the white color
* in CMYK (0.0, 0.0, 0.0) as the default state, and then use basic colors (cyan, magenta, yellow)
* for data in the cell. Mixing the colors gives us the visualization of the whole state. At the
* end, just before it's displayed on the screen, the result color is converted to RGB.
* 
* https://en.wikipedia.org/wiki/CMYK_color_model
*/
final case class CMYK(c: Double, m: Double, y: Double, k: Double) {
  def +(other: CMYK): CMYK = CMYK.sum(Seq(this, other))

  // this makes sense only for dimming the color, ie. coeff in <0.0, 1.0>
  def *(coeff: Double): CMYK = {
    require(coeff >= 0.0 && coeff <= 1.0)
    val nc = CMYK(c * coeff, m * coeff, y * coeff, k * coeff)
    if (nc.c > 1.0 || nc.m > 1.0 || nc.y >1.0 || nc.k > 1.0) CMYK.sum(Seq(nc)) else nc
  }

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

  lazy val abs: Double = math.sqrt(c *c + m * m + y * y + k * k)

  override def toString: String = s"CMYK(${c.round(3)}, ${m.round(3)}, ${y.round(3)}, ${k.round(3)})"
}

object CMYK {
  def apply(c: Double, m: Double, y: Double): CMYK = CMYK(c, m, y, 0.0)

  def unapply(color: CMYK): Option[(Double, Double, Double, Double)] = Some((color.c, color.m, color.y, color.k))

  val Cyan: CMYK = CMYK(1.0, 0.0, 0.0)
  val Magenta: CMYK = CMYK(0.0, 1.0, 0.0)
  val Yellow: CMYK = CMYK(0.0, 0.0, 1.0)

  val colors: Array[CMYK] = Array(Cyan, Magenta, Yellow)

  val Black: CMYK = CMYK(0.0, 0.0, 0.0, 1.0)
  val White: CMYK = CMYK(0.0, 0.0, 0.0)

  def sum(colors: Iterable[CMYK]): CMYK =
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

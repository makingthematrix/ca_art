package caart.engine

package object fields {

  implicit final class RichDouble(val d: Double) extends AnyVal {
    def round(digits: Int = 0): Double =
      if (digits == 0) math.round(d).toDouble else {
      val t = math.pow(10.0, digits)
      math.round(d * t).toDouble / t
    }
  }

}

package object engine {
  def round(d: Double, digits: Int): Double = {
    val t = math.pow(10.0, digits)
    (d * t).round.toDouble / t
  }
}

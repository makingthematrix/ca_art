package fields

import org.scalatest.{FlatSpec, Matchers}

class CMYKSpec extends FlatSpec with Matchers {
  "CMYK color" should "multiply by a coefficient" in {
    val c = CMYK(1.0, 0.0, 0.0, 0.0)
    c * 0.5 shouldEqual CMYK(0.5, 0.0, 0.0, 0.0)
  }
}

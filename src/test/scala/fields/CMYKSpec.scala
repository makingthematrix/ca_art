package fields

class CMYKSpec extends munit.FunSuite {
  test("CMYK color should multiply by a coefficient") {
    val c = CMYK(1.0, 0.0, 0.0, 0.0)
    assertEquals(c * 0.5, CMYK(0.5, 0.0, 0.0, 0.0))
  }
}

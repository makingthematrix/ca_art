package caart.engine.fields

class Dir2DSpec extends munit.FunSuite {
  test("Dir2D should approximate to the nearest von Neumann's neighbor") {
    assertEquals(new Dir2D(1.0, -2.0).approx4, Up)
      assertEquals(new Dir2D(-1.0, -2.0).approx4, Up)

      assertEquals(new Dir2D(2.0, -1.0).approx4, Right)
      assertEquals(new Dir2D(2.0, 1.0).approx4, Right)

      assertEquals(new Dir2D(1.0, 2.0).approx4, Down)
      assertEquals(new Dir2D(-1.0, 2.0).approx4, Down)

      assertEquals(new Dir2D(-2.0, -1.0).approx4, Left)
      assertEquals(new Dir2D(-2.0, 1.0).approx4, Left)

      assertEquals(new Dir2D(-1.0, 0.0).approx4, Left)
      assertEquals(new Dir2D(1.0, 0.0).approx4, Right)
      assertEquals(new Dir2D(0.0, -1.0).approx4, Up)
      assertEquals(new Dir2D(0.0, 1.0).approx4, Down)
  }
    
  test("Dir2D should approximate to the nearest Moore's neighbor") {
    assertEquals(new Dir2D(1.0, -3.0).approx8, Up)
      assertEquals(new Dir2D(-1.0, -3.0).approx8, Up)

      assertEquals(new Dir2D(1.5, -2.0).approx8, UpRight)
      assertEquals(new Dir2D(2.0, -1.5).approx8, UpRight)

      assertEquals(new Dir2D(3.0, -1.0).approx8, Right)
      assertEquals(new Dir2D(3.0, 1.0).approx8, Right)

      assertEquals(new Dir2D(1.5, 2.0).approx8, DownRight)
      assertEquals(new Dir2D(2.0, 1.5).approx8, DownRight)

      assertEquals(new Dir2D(1.0, 3.0).approx8, Down)
      assertEquals(new Dir2D(-1.0, 3.0).approx8, Down)

      assertEquals(new Dir2D(-1.5, 2.0).approx8, DownLeft)
      assertEquals(new Dir2D(-2.0, 1.5).approx8, DownLeft)

      assertEquals(new Dir2D(-3.0, -1.0).approx8, Left)
      assertEquals(new Dir2D(-3.0, 1.0).approx8, Left)

      assertEquals(new Dir2D(-1.5, -2.0).approx8, UpLeft)
      assertEquals(new Dir2D(-2.0, -1.5).approx8, UpLeft)
  }
}

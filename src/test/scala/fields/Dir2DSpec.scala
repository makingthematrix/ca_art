package fields

import org.scalatest._

class Dir2DSpec extends FlatSpec with Matchers {
  "'Raw' Dir2D" should "approximate to the nearest von Neumann's neighbor" in {
    new Dir2D(1.0, -2.0).approx4 shouldEqual Up
    new Dir2D(-1.0, -2.0).approx4 shouldEqual Up

    new Dir2D(2.0, -1.0).approx4 shouldEqual Right
    new Dir2D(2.0, 1.0).approx4 shouldEqual Right

    new Dir2D(1.0, 2.0).approx4 shouldEqual Down
    new Dir2D(-1.0, 2.0).approx4 shouldEqual Down

    new Dir2D(-2.0, -1.0).approx4 shouldEqual Left
    new Dir2D(-2.0, 1.0).approx4 shouldEqual Left

    new Dir2D(-1.0, 0.0).approx4 shouldEqual Left
    new Dir2D(1.0, 0.0).approx4 shouldEqual Right
    new Dir2D(0.0, -1.0).approx4 shouldEqual Up
    new Dir2D(0.0, 1.0).approx4 shouldEqual Down
  }

  it should "approximate to the neares Moore's neighbor" in {
    new Dir2D(1.0, -3.0).approx8 shouldEqual Up
    new Dir2D(-1.0, -3.0).approx8 shouldEqual Up

    new Dir2D(1.5, -2.0).approx8 shouldEqual UpRight
    new Dir2D(2.0, -1.5).approx8 shouldEqual UpRight

    new Dir2D(3.0, -1.0).approx8 shouldEqual Right
    new Dir2D(3.0, 1.0).approx8 shouldEqual Right

    new Dir2D(1.5, 2.0).approx8 shouldEqual DownRight
    new Dir2D(2.0, 1.5).approx8 shouldEqual DownRight

    new Dir2D(1.0, 3.0).approx8 shouldEqual Down
    new Dir2D(-1.0, 3.0).approx8 shouldEqual Down

    new Dir2D(-1.5, 2.0).approx8 shouldEqual DownLeft
    new Dir2D(-2.0, 1.5).approx8 shouldEqual DownLeft

    new Dir2D(-3.0, -1.0).approx8 shouldEqual Left
    new Dir2D(-3.0, 1.0).approx8 shouldEqual Left

    new Dir2D(-1.5, -2.0).approx8 shouldEqual UpLeft
    new Dir2D(-2.0, -1.5).approx8 shouldEqual UpLeft
  }
}

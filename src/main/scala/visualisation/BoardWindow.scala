package visualisation

import de.h2b.scala.lib.simgraf.{Color, Point, World}
import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import engine.Board
import fields.{Black, White}
import langtonscell.LangtonsCell

class BoardWindow(window: World, scale: Int, withBorder: Boolean) {

  def draw(x: Int, y: Int, c: Color): Unit = {
    window.activeColor = c
    if (scale == 1)
      window.plot(Point(x,y))
    else
      Rectangle(
        Point(x * scale, y * scale),
        Point(x * scale + scale - (if (withBorder) 1 else 0), y * scale + scale - (if (withBorder) 1 else 0))
      ).fill(window)
  }

  def draw(lc: LangtonsCell): Unit =
    draw(lc.pos.x, lc.pos. y, lc.color match {
      case White => Color(255, 255, 255)
      case Black => Color(0, 0, 0)
    })

  def draw(board: Board[LangtonsCell]): Unit = board.iterator.foreach(draw)
}

object BoardWindow {
  def apply(title: String, dim: Int = 800, scale: Int = 1, withBorder: Boolean = false): BoardWindow = {
    val iterator = GridLayout.onScreen(1, 1).iterator
    val window = World(Rectangle(Point(0, 0), Point(dim * scale, dim * scale)))(iterator.next().fit(dim * scale, dim * scale),  title)
    window.clear(Color.White)
    new BoardWindow(window, scale, withBorder)
  }

}
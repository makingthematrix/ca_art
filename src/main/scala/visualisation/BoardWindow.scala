package visualisation

import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import de.h2b.scala.lib.simgraf.{Color, Point, World}
import engine.Board
import fields.Pos2D
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
    draw(lc.pos.x, lc.pos. y, (lc.color, lc.dir) match {
      case (_, Some(_)) => Color(255, 0, 0)
      case (false, _)   => Color(255, 255, 255)
      case (true, _)    => Color(0, 0, 0)
    })

  private var colorMap = Map.empty[Pos2D, Color]

  private def toColorMap(board: Board[LangtonsCell]): Map[Pos2D, Color] =
    board.values.map(c => c.pos -> (c.color, c.dir)).toMap.mapValues {
      case (_, Some(_)) => Color.Red
      case (false, _)   => Color.White
      case (true, _)    => Color.Black
    }

  private var oldBoard = Option.empty[Board[LangtonsCell]]

  def draw(board: Board[LangtonsCell]): Unit = {
    oldBoard.fold(board.values)(board - _).foreach(draw)
    oldBoard = Some(board)
  }
}

object BoardWindow {
  def apply(title: String, dim: Int = 800, scale: Int = 1, withBorder: Boolean = false): BoardWindow = {
    val window =
      World(
        Rectangle(Point(0, 0), Point(dim * scale, dim * scale))
      )(
        GridLayout.onScreen(1, 1).iterator.next().fit(dim * scale, dim * scale),
        title
      )
    window.clear(Color.White)
    new BoardWindow(window, scale, withBorder)
  }

}
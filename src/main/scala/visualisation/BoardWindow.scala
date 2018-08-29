package visualisation

import de.h2b.scala.lib.simgraf.event._
import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import de.h2b.scala.lib.simgraf.{Color, Point, World}
import engine.{AutomatonCell, Board}
import fields.Pos2D

class BoardWindow[CA <: AutomatonCell[CA]](window: World,
                                           toColor: CA => Color,
                                           scale: Int,
                                           withBorder: Boolean) {

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

  private var oldBoard = Option.empty[Board[CA]]

  def draw(board: Board[CA]): Unit = {
    oldBoard.fold(board.values)(board - _).foreach { c => draw(c.pos.x, c.pos.y, toColor(c))}
    oldBoard = Some(board)
  }
}

object BoardWindow {
  def apply[CA <: AutomatonCell[CA]](title: String,
                                     toColor: CA => Color,
                                     dim: Int = 100,
                                     scale: Int = 1,
                                     withBorder: Boolean = false,
                                     left: Pos2D => Unit = _ => (),
                                     right: Pos2D => Unit = _ => ()): BoardWindow[CA] = {
    val window = World.withEvents(
      Rectangle(Point(0, 0), Point(dim * scale, dim * scale))
    )(
      GridLayout.onScreen(1, 1).iterator.next().fit(dim * scale, dim * scale),
      title
    )
    window.clear(Color.White)

    Subscriber.to(window) {
      case MouseEvent(LeftButton, _, _, pixel)  => left(Pos2D(dim - pixel.x, pixel.y))
      case MouseEvent(RightButton, _, _, pixel) => right(Pos2D(dim - pixel.x, pixel.y))
      case e: Event ⇒ println(e)
    }

    new BoardWindow[CA](window, toColor, scale, withBorder)
  }

}
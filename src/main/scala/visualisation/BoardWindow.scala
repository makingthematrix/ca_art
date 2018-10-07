package visualisation

import java.util
import java.util.concurrent.LinkedBlockingQueue

import de.h2b.scala.lib.simgraf.event._
import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import de.h2b.scala.lib.simgraf.{Color, Point, World}
import engine.{Automaton, AutomatonCell, Board}
import fields.Pos2D

import scala.collection.JavaConverters._

class BoardWindow[C <: AutomatonCell[C]](window: World,
                                         toColor: C => Color,
                                         dim: Int,
                                         scale: Int) {
  import BoardWindow._

  def draw(x: Int, y: Int, c: Color): Unit = {
    window.activeColor = c
    if (scale == 1)
      window.plot(Point(x,y))
    else
      Rectangle(
        Point(x * scale, y * scale),
        Point(x * scale + scale, y * scale + scale)
      ).fill(window)
  }

  def draw(board: Board[C]): Unit = {
    oldBoard.fold(board.values)(board - _).foreach { c => draw(c.pos.x, c.pos.y, toColor(c))}
    oldBoard = Some(board)
  }

  private var oldBoard = Option.empty[Board[C]]

  private val clicksQueue = new LinkedBlockingQueue[ClickType]()

  private def clicks = if (!clicksQueue.isEmpty) {
    val col = new util.ArrayList[ClickType]()
    clicksQueue.drainTo(col)
    col.asScala.toList
  } else List.empty


  def mainLoop(auto: Automaton[C], leftClickUpdate: C => C, sleep: Long = 100L): Unit = {
    var pause = true
    var stop = false

    Subscriber.to(window) {
      case MouseEvent(LeftButton, _, _, pixel)  =>
        val p = Pos2D(pixel.x / scale, pixel.y / scale)
        draw(auto.update(cell => if (cell.pos == p) leftClickUpdate(cell) else cell))
      case DragEvent(LeftButton, _, start, end) =>
        val range = Pos2D.range(Pos2D(start.x / scale, start.y / scale), Pos2D(end.x / scale, end.y / scale)).toSet
        val updated = auto.update(cell => if (range.contains(cell.pos)) leftClickUpdate(cell) else cell)
        draw(updated)
      case MouseEvent(RightButton, _, _, _) =>
        pause = !pause
      case KeyEvent('p') =>
        pause = !pause
      case KeyEvent('q') =>
        pause = true
        stop = true
        system.terminate()
      case e: Event =>
        println(e)
    }

    while(!stop){
      if (!pause) draw(auto.next()) else Thread.sleep(sleep)
    }

  }
}

object BoardWindow {

  def apply[CA <: AutomatonCell[CA]](title: String,
                                     toColor: CA => Color,
                                     dim: Int,
                                     scale: Int): BoardWindow[CA] = {
    val window = World.withEvents(
      Rectangle(Point(0, 0), Point(dim * scale, dim * scale))
    )(
      GridLayout.onScreen(1, 1).iterator.next().fit(dim * scale, dim * scale),
      title
    )

    window.clear(Color.White)

    new BoardWindow[CA](window, toColor, dim, scale)
  }

  sealed trait ClickType
  case class LeftClick(pos: Pos2D) extends ClickType
  case class RightClick(pos: Pos2D) extends ClickType
  case object ExitClick extends ClickType
}
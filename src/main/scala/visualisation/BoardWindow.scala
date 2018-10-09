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

  private val identityClick: C => C = c => c
  private val identityClick2: (C, Pos2D) => C = (c, _) => c

  def mainLoop(auto: Automaton[C],
               leftClick: C => C = identityClick,
               rightClick: C => C = identityClick,
               leftClick2: (C, Pos2D) => C = identityClick2,
               rightClick2: (C, Pos2D) => C = identityClick2,
               sleep: Long = 0L): Unit = {
    var pause = true
    var stop = false

    Subscriber.to(window) {
      case MouseEvent(LeftButton, _, _, pixel) if leftClick != identityClick =>
        val p = Pos2D(pixel.x / scale, pixel.y / scale)
        draw(auto.update(cell => if (cell.pos == p) leftClick(cell) else cell))
      case MouseEvent(LeftButton, _, _, pixel) if leftClick2 != identityClick2 =>
        val p = Pos2D(pixel.x / scale, pixel.y / scale)
        draw(auto.update(leftClick2(_, p)))
      case DragEvent(LeftButton, _, start, end) =>
        val range = Pos2D.range(Pos2D(start.x / scale, start.y / scale), Pos2D(end.x / scale, end.y / scale)).toSet
        val updated = auto.update(cell => if (range.contains(cell.pos)) leftClick(cell) else cell)
        draw(updated)
      case MouseEvent(RightButton, _, _, pixel) if rightClick != identityClick =>
        val p = Pos2D(pixel.x / scale, pixel.y / scale)
        draw(auto.update(cell => if (cell.pos == p) rightClick(cell) else cell))
      case MouseEvent(RightButton, _, _, pixel) if rightClick2 != identityClick2 =>
        val p = Pos2D(pixel.x / scale, pixel.y / scale)
        draw(auto.update(rightClick2(_, p)))
      case KeyEvent(' ') =>
        pause = !pause
      case KeyEvent('q') =>
        pause = true
        stop = true
        system.terminate()
      case e: Event =>
        println(e)
    }

    while(!stop){
      if (!pause) {
        draw(auto.next())
        if (sleep > 0L) Thread.sleep(sleep)
      } else Thread.sleep(100L)
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
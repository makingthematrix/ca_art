package visualisation

import java.util

import de.h2b.scala.lib.simgraf.event._
import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import de.h2b.scala.lib.simgraf.{Color, Point, World}
import engine.{AutomatonCell, Board}
import fields.Pos2D
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import scala.collection.JavaConverters._

class BoardWindow[CA <: AutomatonCell[CA]](window: World,
                                           toColor: CA => Color,
                                           scale: Int) {
  lazy val leftClicks: BoardWindow.Clicks = getClicks(left)
  lazy val rightClicks: BoardWindow.Clicks = getClicks(right)

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

  def draw(board: Board[CA]): Unit = {
    oldBoard.fold(board.values)(board - _).foreach { c => draw(c.pos.x, c.pos.y, toColor(c))}
    oldBoard = Some(board)
  }

  private var oldBoard = Option.empty[Board[CA]]

  private val left = new LinkedBlockingQueue[Pos2D]()
  private val right = new LinkedBlockingQueue[Pos2D]()

  Subscriber.to(window) {
    case MouseEvent(LeftButton, _, _, pixel)  => left.add(Pos2D(pixel.x / scale, pixel.y / scale))
    case MouseEvent(RightButton, _, _, pixel) => right.add(Pos2D(pixel.x / scale, pixel.y / scale))
    case DragEvent(LeftButton, _, start, end) =>
      left.addAll(Pos2D.range(Pos2D(start.x / scale, start.y / scale), Pos2D(end.x / scale, end.y / scale)).asJavaCollection)
    case e: Event ⇒ println(e)
  }

  private def getClicks(queue: BlockingQueue[Pos2D]): BoardWindow.Clicks = new BoardWindow.Clicks {
    override def isEmpty: Boolean = queue.isEmpty
    override def size: Int = queue.size()
    override def peek: Pos2D = queue.peek()
    override def take: Pos2D = queue.take()
    override def takeAll: List[Pos2D] = if (!isEmpty) {
      val col = new util.ArrayList[Pos2D]()
      queue.drainTo(col)
      col.asScala.toList
    } else List.empty
  }

}

object BoardWindow {

  trait Clicks {
    def isEmpty: Boolean
    def size: Int
    def peek: Pos2D
    def take: Pos2D
    def takeAll: List[Pos2D]
  }

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

    new BoardWindow[CA](window, toColor, scale)
  }
}
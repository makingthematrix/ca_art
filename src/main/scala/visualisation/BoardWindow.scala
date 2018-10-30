package visualisation

import de.h2b.scala.lib.simgraf.event._
import de.h2b.scala.lib.simgraf.layout.GridLayout
import de.h2b.scala.lib.simgraf.shapes.Rectangle
import de.h2b.scala.lib.simgraf.{Color, Point, World}
import engine.{Automaton, AutomatonCell, Board}
import fields.Pos2D

/** A wrapper around Simgraf.World
  *
  * Displays the board as a 2D rectangle. `dim` is the size of the board, `scale` is the size in pixels
  * of a side of a square representing one cell. Since right now we support only 2D c.a., this is enough
  * to display any cellular automaton with `toColor` used to interpret the state of the cell as as color.
  *
  * `BoardWindow` can be used by the automaton with calls to `draw`, or - for interactive examples -
  * the automaton may be provided to the `mainLoop` method.
  *
  * @constructor takes the `Simgraf.World` instance, the function to compute a color from the cell state, the size of the board and its scale.
  * @tparam C the exact case class implementing the cell
  */
class BoardWindow[C <: AutomatonCell[C]] private (window: World,
                                                  toColor: C => Color,
                                                  dim: Int,
                                                  scale: Int) {
  /** Draws the given board in the window.
    *
    * @param board the current board
    */
  def draw(board: Board[C]): Unit = {
    oldBoard.fold(board.values)(board - _).foreach { c => draw(c.pos.x, c.pos.y, toColor(c))}
    oldBoard = Some(board)
  }
    
  private def draw(x: Int, y: Int, c: Color): Unit = {
    window.activeColor = c
    if (scale == 1)
      window.plot(Point(x,y))
    else
      Rectangle(
        Point(x * scale, y * scale),
        Point(x * scale + scale, y * scale + scale)
      ).fill(window)
  }

  private var oldBoard = Option.empty[Board[C]]

  private val identityClick: C => C = c => c
  private val identityClick2: (C, Pos2D) => C = (c, _) => c

  /** Runs the provided automaton in a loop and allows for the user's interference.
    *
    * @param auto the automaton in its initial state
    * @param leftClick a function updating a cell as a result of left-clicking on its visual representation
    * @param rightClick a function updating a cell as a result of right-clicking on its visual representation
    * @param leftClick2 a function updating every cell on the board as a result of left-clicking on the given position
    * @param rightClick2 a function updating every cell on the board as a result of right-clicking on the given position
    * @param sleep the time interval between two consecutive iterations
    *
    * The loop starts in the paused state. The user is able to alter the board with clicks and drags (a drag
    * is interpreted as a series of clicks on every cell in the covered rectangle). Hitting the SPACE button
    * launches the iterations. Hitting SPACE again pauses the loop. Hitting 'q' stops the loop.
    *
    * @todo Right now updates can be safely performed only in the paused state. Clicking and dragging while
    * the loop is running also works, but sometimes the outcome is weird (wrong cells being updated).
    */
  def mainLoop(auto: Automaton[C],
               leftClick:    C => C         = identityClick,
               rightClick:   C => C         = identityClick,
               leftClick2:  (C, Pos2D) => C = identityClick2,
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
  def apply[C <: AutomatonCell[C]](title: String,
                                   toColor: C => Color,
                                   dim: Int,
                                   scale: Int): BoardWindow[C] = {
    val window = World.withEvents(
      Rectangle(Point(0, 0), Point(dim * scale, dim * scale))
    )(
      GridLayout.onScreen(1, 1).iterator.next().fit(dim * scale, dim * scale),
      title
    )

    window.clear(Color.White)

    new BoardWindow[C](window, toColor, dim, scale)
  }
}
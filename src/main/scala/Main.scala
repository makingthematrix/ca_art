import engine.{Automaton, Board}
import fields.{Pos2D, Up}
import langtonscell.LangtonsCell
import visualisation.BoardWindow

object Main {
  val dim = 200

  private def init(board: Board[LangtonsCell]) = {
    board.update(Pos2D(dim / 2, dim / 2)) {
      _.copy(dir = Some(Up))
    }
  }

  def main(args: Array[String]): Unit = {
    val auto = new Automaton(dim, init, LangtonsCell.apply)
    val world = BoardWindow("Langtons Ant", dim = dim, scale = 8)

    val timestamp = System.currentTimeMillis()
    for (i <- 1 to 100) {
        world.draw(auto.iterator.next())
    }

    println(s"scale 1: ${System.currentTimeMillis() - timestamp}")
    // 200, streams, diff, IJ: 13925
    // 200, iterator, diff, IJ:10711
    // 200, iterator, no diff, IJ: 12825
    // 200, iterator, no diff, no IJ: 14762
    // 200, iterator, diff, no IJ: 10564
    // 200, streams, diff, no IJ: 12075

  }
}

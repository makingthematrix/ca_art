import engine.{Automaton, Board}
import fields.{Pos2D, Up}
import langtonscell.LangtonsCell
import visualisation.BoardWindow

object Main {
  val dim = 200

  private def init(board: Board[LangtonsCell]) =
    board.update(Pos2D(dim / 2, dim / 2)) {
      _.copy(dir = Some(Up))
    }

  def main(args: Array[String]): Unit = {
    val auto = new Automaton(dim, init, LangtonsCell.apply)
    val world = BoardWindow("Langtons Ant", dim = dim, scale = 4)

    val timestamp = System.currentTimeMillis()
    for (_ <- 1 to 100) world.draw(auto.iterator.next())
    println(s"${System.currentTimeMillis() - timestamp}")
  }
}

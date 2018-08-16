import engine.{Automaton, Grid}
import fields.{Pos2D, Up}
import langtonscell.LangtonsCell

object Main {
  private def init(grid: Grid[LangtonsCell]) = grid.update(Pos2D(50, 50)){ _.copy(dir = Some(Up)) }

  def main(args: Array[String]): Unit = {
    val ca = new Automaton(100, init, LangtonsCell.apply)
    val it = ca.iterator

    val grid1 = it.next()
    val grid2 = it.next()

    println("Hello, world!")
  }
}

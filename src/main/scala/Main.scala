import engine.{Automaton, Board}
import fields.{Pos2D, Up}
import langtonscell.LangtonsCell
import visualisation.BoardWindow

object Main {

  def main(args: Array[String]): Unit = {
    var dim = 200
    var it = 100
    args.sliding(2, 2).foreach {
      case Array("dim", d) => println(s"dim = $d"); dim = Integer.parseInt(d)
      case Array("it", i)  => println(s"it  = $i"); it = Integer.parseInt(i)
      case x => println(s"x = ${x.toList}");
    }

    val init = (board: Board[LangtonsCell]) => board.update(Pos2D(dim / 2, dim / 2)) { _.copy(dir = Some(Up)) }

    val auto = new Automaton(dim, init, LangtonsCell.apply)
    val world = BoardWindow("Langtons Ant", dim = dim, scale = 4)

    val timestamp = System.currentTimeMillis()
    for (_ <- 1 to it) world.draw(auto.iterator.next())
    println(s"${System.currentTimeMillis() - timestamp}")
  }

}

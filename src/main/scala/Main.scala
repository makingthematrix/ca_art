import engine.{Automaton, Board}
import fields.{Pos2D, Up}
import langtonscell.{LangtonsBoard, LangtonsCell}
import visualisation.BoardWindow

object Main {

  def main(args: Array[String]): Unit = {
    var dim = 200
    var it = 100
    var step = 1
    args.sliding(2, 2).foreach {
      case Array("dim", d)  => println(s"dim  = $d"); dim  = Integer.parseInt(d)
      case Array("it", i)   => println(s"it   = $i"); it   = Integer.parseInt(i)
      case Array("step", s) => println(s"step = $s"); step = Integer.parseInt(s)
      case x => println(s"unrecognized parameters: ${x.toList}");
    }

    val init = (board: Board[LangtonsCell]) => board.copy(Pos2D(dim / 2, dim / 2)) { _.copy(dir = Some(Up)) }

    val auto = new Automaton(dim, init, LangtonsBoard.apply, LangtonsCell.apply)
    val world = BoardWindow("Langtons Ant", dim = dim, scale = 4)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.iterator.next()
      if (i % step == 0) world.draw(board)
    }
    world.draw(auto.iterator.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

}

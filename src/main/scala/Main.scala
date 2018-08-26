import de.h2b.scala.lib.simgraf.Color
import fields.{Pos2D, Up}
import langtonscell.LangtonsCell
import langtonscolors.LangtonsColors
import visualisation.BoardWindow

object Main {

  def main(args: Array[String]): Unit = {
    var dim   = 200
    var it    = 100
    var step  = 1
    var scale = 4

    args.sliding(2, 2).foreach {
      case Array("dim", d)   => println(s"dim  = $d");  dim   = Integer.parseInt(d)
      case Array("it", i)    => println(s"it   = $i");  it    = Integer.parseInt(i)
      case Array("step", s)  => println(s"step = $s");  step  = Integer.parseInt(s)
      case Array("scale", s) => println(s"scale = $s"); scale = Integer.parseInt(s)
      case x => println(s"unrecognized parameters: ${x.toList}");
    }

    langtonsColors(dim, it, step, scale)
  }

  private def langtonsCell(dim:Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsCell.automaton(dim) { board =>
      board.copy(Pos2D(dim / 2, dim / 2))(_.copy(dir = Some(Up)))
    }

    val world = BoardWindow[LangtonsCell]("Langtons Cell", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) world.draw(board)
    }

    world.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def toColor(c: LangtonsCell) = (c.color, c.dir) match {
    case (_, Some(_)) => Color.Red
    case (false, _)   => Color.White
    case (true, _)    => Color.Black
  }


  private def langtonsColors(dim:Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsColors.automaton(dim) { board =>
      board.copy(Pos2D(dim / 2, dim / 2))(_.copy(dirs = List((Up, fields.Color.Blue))))
    }

    val world = BoardWindow[LangtonsColors]("Langtons Cell", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) world.draw(board)
    }

    world.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def toColor(c: LangtonsColors) = {
    val color = if (c.colors.isEmpty) fields.Color.White else c.colors.fold(fields.Color.Black)(_ + _)
    Color(color.r, color.g, color.b)
  }
}

import de.h2b.scala.lib.simgraf.Color
import fields.{CMYK, Dir2D, Pos2D, Up}
import langtonscell.LangtonsCell
import langtonscolors.LangtonsColors
import visualisation.BoardWindow

import scala.util.Random

object Main {

  def main(args: Array[String]): Unit = {
    var dim   = 200
    var it    = 100
    var step  = 1
    var scale = 4
    var example = "langtonsColorsInteractive"

    args.sliding(2, 2).foreach {
      case Array("dim", d)     => println(s"dim  = $d");  dim   = Integer.parseInt(d)
      case Array("it", i)      => println(s"it   = $i");  it    = Integer.parseInt(i)
      case Array("step", s)    => println(s"step = $s");  step  = Integer.parseInt(s)
      case Array("scale", s)   => println(s"scale = $s"); scale = Integer.parseInt(s)
      case Array("example", e) => println(s"example = $e"); example = e;
      case x => println(s"unrecognized parameters: ${x.toList}");
    }

    example match {
      case "langtonsCell"   => langtonsCell(dim, it, step, scale)
      case "langtonsCellInteractive"   => langtonsCellInteractive(dim, scale)
      case "langtonsColors" => langtonsColors(dim, it, step, scale)
      case "langtonsColorsInteractive"   => langtonsColorsInteractive(dim, scale)
      case x => println(s"unrecognized example: $x")
    }
  }

  private def langtonsCell(dim: Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsCell.automaton(dim) { board =>
      board.copy(Pos2D(dim / 2, dim / 2))(_.copy(dir = Some(Up)))
    }

    val world = BoardWindow[LangtonsCell]("Langtons Cell", lcToColor(_), dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) world.draw(board)
    }

    world.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsCellInteractive(dim: Int, scale: Int) = {
    val auto = LangtonsCell.automaton(dim)()

    var start = false
    var exit = false

    val world = BoardWindow[LangtonsCell]("Langtons Cell", lcToColor(_), dim = dim, scale = scale,
      left = (p: Pos2D) => {
        start = true
        auto.update(_.copy(p)(c => c.copy(color = true, dir = Some(Up))))
      },
      right = (_: Pos2D) => exit = true
    )

    while(!start) Thread.sleep(500)
    while(!exit) world.draw(auto.next())
  }

  private def lcToColor(c: LangtonsCell) = (c.color, c.dir) match {
    case (_, Some(_)) => Color.Red
    case (false, _)   => Color.White
    case (true, _)    => Color.Black
  }


  private def langtonsColors(dim:Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsColors.automaton(dim) { board =>
      board
        .copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Cyan))))
        .copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Magenta))))
        .copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Yellow))))
    }

    val world = BoardWindow[LangtonsColors]("Langtons Colors", lcsToColor(_), dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) world.draw(board)
    }

    world.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsColorsInteractive(dim: Int, scale: Int) = {
    def randomDirs = {
      val d: Dir2D = Dir2D.dirs(Random.nextInt(Dir2D.dirs.size))
      val c: CMYK = CMYK.colors(Random.nextInt(CMYK.colors.size))
      List((d, c))
    }

    val auto = LangtonsColors.automaton(dim)()

    var start = false
    var exit = false

    val world = BoardWindow[LangtonsColors]("Langtons Colors", lcsToColor(_), dim = dim, scale = scale,
      left = (p: Pos2D) => {
        start = true
        auto.update(_.copy(p)(c => c.copy(dirs = randomDirs)))
      },
      right = (_: Pos2D) => exit = true
    )

    while(!start) Thread.sleep(500)
    while(!exit) world.draw(auto.next())
  }

  private def lcsToColor(c: LangtonsColors) =
    if (c.colors.isEmpty) Color.White else {
      val color = CMYK.sum(c.colors).toRGB
      Color(color.r, color.g, color.b)
    }
}

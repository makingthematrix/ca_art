import brush.Brush
import de.h2b.scala.lib.simgraf.Color
import engine.Automaton
import fields._
import gameoflife.GameOfLife
import langtonscell.LangtonsAnt
import langtonscolors.LangtonsColors
import visualisation.BoardWindow

import scala.util.Random

object Main {

  def main(args: Array[String]): Unit = {
    var dim   = 100
    var it    = 100
    var step  = 1
    var scale = 8
    var example = "4i"

    args.sliding(2, 2).foreach {
      case Array("dim", d)     => println(s"dim  = $d");  dim   = Integer.parseInt(d)
      case Array("it", i)      => println(s"it   = $i");  it    = Integer.parseInt(i)
      case Array("step", s)    => println(s"step = $s");  step  = Integer.parseInt(s)
      case Array("scale", s)   => println(s"scale = $s"); scale = Integer.parseInt(s)
      case Array("example", e) => println(s"example = $e"); example = e;
      case x => println(s"unrecognized parameters: ${x.toList}");
    }

    example match {
      case "1i" => gameOfLifeInteractive(dim, scale)
      case "2"  => langtonsAnt(dim, it, step, scale)
      case "2i" => langtonsAntInteractive(dim, scale)
      case "3"  => langtonsColors(dim, it, step, scale)
      case "3i" => langtonsColorsInteractive(dim, scale)
      case "4i" => brushInteractive(dim, scale)
      case x    => println(s"unrecognized example: $x")
    }

  }

  private def langtonsAnt(dim: Int, it: Int, step: Int, scale: Int): Unit = {
    val auto =
      LangtonsAnt.automaton(dim)
      .update(cell => if (cell.pos == Pos2D(dim / 2, dim / 2)) cell.copy(dir = Some(Up)) else cell)

    val boardWindow = BoardWindow[LangtonsAnt]("Langtons Ant", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next
      if (i % step == 0) boardWindow.draw(board)
    }

    boardWindow.draw(auto.next)

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsAntInteractive(dim: Int, scale: Int): Unit = {
    val auto = LangtonsAnt.automaton(dim)
    val boardWindow = BoardWindow[LangtonsAnt]("Langtons Ant", toColor, dim, scale)
    boardWindow.mainLoop(auto, _.copy(color = true, dir = Some(Up)))

    System.exit(0)
  }

  private def toColor(c: LangtonsAnt) = (c.color, c.dir) match {
    case (_, Some(_)) => Color.Red
    case (false, _)   => Color.White
    case (true, _)    => Color.Black
  }

  private def langtonsColors(dim:Int, it: Int, step: Int, scale: Int): Unit = {
    val auto = LangtonsColors.automaton(dim)
    val cyanPos    = Pos2D.random(dim)
    val magentaPos = Pos2D.random(dim)
    val yellowPos  = Pos2D.random(dim)
    auto.update {
      case cell if cell.pos == cyanPos    => cell.copy(dirs = List((Up, CMYK.Cyan)))
      case cell if cell.pos == magentaPos => cell.copy(dirs = List((Up, CMYK.Magenta)))
      case cell if cell.pos == yellowPos  => cell.copy(dirs = List((Up, CMYK.Yellow)))
      case cell => cell
    }

    val boardWindow = BoardWindow[LangtonsColors]("Langtons Colors", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) boardWindow.draw(board)
    }

    boardWindow.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsColorsInteractive(dim: Int, scale: Int): Unit = {
    def randomDirs = {
      val d = Dir2D.dirs4(Random.nextInt(Dir2D.dirs4.length))
      val c = CMYK.colors(Random.nextInt(CMYK.colors.length))
      List((d, c))
    }

    val auto = LangtonsColors.automaton(dim)
    val boardWindow = BoardWindow[LangtonsColors]("Langtons Colors", toColor, dim, scale)
    boardWindow.mainLoop(auto, _.copy(dirs = randomDirs))

    System.exit(0)
  }

  private def toColor(c: LangtonsColors) =
    if (c.colors.isEmpty) Color.White else {
      val color = CMYK.sum(c.colors).toRGB
      Color(color.r, color.g, color.b)
    }

  private def gameOfLifeInteractive(dim: Int, scale: Int): Unit = {
    val auto = new Automaton[GameOfLife](dim, GameOfLife.apply)
    val boardWindow = BoardWindow[GameOfLife]("Game of Life", toColor, dim, scale)
    boardWindow.mainLoop(auto, c => c.copy(life = !c.life))

    System.exit(0)
  }

  private def toColor(c: GameOfLife) = if (c.life) Color.Black else Color.White

  private def brushInteractive(dim: Int, scale: Int): Unit = {
    val auto = Brush.automaton(dim)
    val boardWindow = BoardWindow[Brush]("Brush", toColor, dim, scale)
    boardWindow.mainLoop(auto,
      leftClick2 = (c, pos) => c.copy(center = Some(pos)),
      rightClick = randomBrush
    )

    System.exit(0)
  }

  private def randomBrush(c: Brush): Brush = {
    val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
    c.copy(color = color, brushes = List(color))
  }

  private def toColor(c: Brush) = if (c.center.contains(c.pos)) Color.Black else {
    val rgb = c.color.toRGB
    Color(rgb.r, rgb.g, rgb.b)
  }

}

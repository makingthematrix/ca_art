import brush.Brush
import de.h2b.scala.lib.simgraf.Color
import engine.Automaton
import fields.{CMYK, Dir2D, Pos2D, Up}
import gameoflife.GameOfLife
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
    var example = "3i"

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
      case "2"  => langtonsCell(dim, it, step, scale)
      case "2i" => langtonsCellInteractive(dim, scale)
      case "3"  => langtonsColors(dim, it, step, scale)
      case "3i" => langtonsColorsInteractive(dim, scale)
      case "4i" => brushInteractive(dim, scale)
      case x    => println(s"unrecognized example: $x")
    }

    System.exit(0)
  }

  private def langtonsCell(dim: Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsCell.automaton(dim)
    auto.update { board =>
      board.copy(Pos2D(dim / 2, dim / 2))(_.copy(dir = Some(Up)))
    }

    val boardWindow = BoardWindow[LangtonsCell]("Langtons Cell", toColor(_), dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
      if (i % step == 0) boardWindow.draw(board)
    }

    boardWindow.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsCellInteractive(dim: Int, scale: Int) = {
    val auto = LangtonsCell.automaton(dim)
    val boardWindow = BoardWindow[LangtonsCell]("Langtons Cell", toColor, dim, scale)
    boardWindow.mainLoop(auto, _.copy(color = true, dir = Some(Up)))
  }

  private def toColor(c: LangtonsCell) = (c.color, c.dir) match {
    case (_, Some(_)) => Color.Red
    case (false, _)   => Color.White
    case (true, _)    => Color.Black
  }

  private def langtonsColors(dim:Int, it: Int, step: Int, scale: Int) = {
    val auto = LangtonsColors.automaton(dim)
    auto.update {
      _.copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Cyan))))
       .copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Magenta))))
       .copy(Pos2D.random(dim))(_.copy(dirs = List((Up, CMYK.Yellow))))
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

  private def langtonsColorsInteractive(dim: Int, scale: Int) = {
    def randomDirs = {
      val d: Dir2D = Dir2D.dirs4(Random.nextInt(Dir2D.dirs4.size))
      val c: CMYK = CMYK.colors(Random.nextInt(CMYK.colors.size))
      List((d, c))
    }

    val auto = LangtonsColors.automaton(dim)
    val boardWindow = BoardWindow[LangtonsColors]("Langtons Colors", toColor, dim, scale)
    boardWindow.mainLoop(auto, _.copy(dirs = randomDirs))
  }

  private def toColor(c: LangtonsColors) =
    if (c.colors.isEmpty) Color.White else {
      val color = CMYK.sum(c.colors).toRGB
      Color(color.r, color.g, color.b)
    }

  private def gameOfLifeInteractive(dim: Int, scale: Int) = {
    val auto = new Automaton[GameOfLife](dim, GameOfLife.apply)
    val boardWindow = BoardWindow[GameOfLife]("Game of Life", toColor, dim, scale)
    boardWindow.mainLoop(auto, c => c.copy(life = !c.life))
  }

  private def toColor(c: GameOfLife) = if (c.life) Color.Black else Color.White

  private def brushInteractive(dim: Int, scale: Int) = {
    val auto = Brush.automaton(dim)
    // TODO: select random colored points
    val boardWindow = BoardWindow[Brush]("Brush", toColor, dim, scale)
    boardWindow.mainLoop(auto, c => {
      auto.update(_.copy(_.copy(center = Some(c.pos))))
      c
    })
  }

  private def toColor(c: Brush) = {
    val rgb = c.color.toRGB
    Color(rgb.r, rgb.g, rgb.b)
  }

}

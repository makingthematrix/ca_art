package caart

import caart.chase.Chase
import com.almasb.fxgl.app.GameApplication
import caart.fields._
import caart.gameoflife.GameOfLife
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import caart.langtonsant.LangtonsAnt
import caart.langtonscolors.LangtonsColors
import caart.visualisation.FXGLApp

import scala.jdk.CollectionConverters._
import scala.util.Random

final class Main extends Application {
  override def start(stage: Stage): Unit = {
    val args = Arguments.parseArguments(getParameters.getRaw.asScala.toSeq)
    stage.setScene(new Scene(GameApplication.embeddedLaunch(new FXGLApp(args)), args.windowSize, args.windowSize))
    stage.show()
  }
}

object Main {
  def main(args: Array[String]): Unit = Application.launch(classOf[Main], args: _*)

  private def gameOfLifeInteractive(dim: Int, scale: Int): Unit = {
    //def toColor(c: GameOfLife) = if (c.life) Color.Black else Color.White

    val auto = GameOfLife.automaton(dim)
    Application.launch(classOf[Main])
    (0 to 30).foreach(_ => Thread.sleep(1000))

    System.exit(0)
  }

  private def langtonsAnt(dim: Int, it: Int, step: Int, scale: Int): Unit = {
    /*def toColor(c: LangtonsAnt) = (c.color, c.dir) match {
      case (_, Some(_)) => Color.Red
      case (false, _)   => Color.White
      case (true, _)    => Color.Black
    }*/

    val auto =
      LangtonsAnt.automaton(dim)
      .update(cell => if (cell.pos == Pos2D(dim / 2, dim / 2)) cell.copy(dir = Some(Up)) else cell)

    //val boardWindow = BoardWindow[LangtonsAnt]("Langtons Ant", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next
      //if (i % step == 0) boardWindow.draw(board)
    }

    //boardWindow.draw(auto.next)

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsAntInteractive(dim: Int, scale: Int): Unit = {
    /*def toColor(c: LangtonsAnt) = (c.color, c.dir) match {
      case (_, Some(_)) => Color.Red
      case (false, _)   => Color.White
      case (true, _)    => Color.Black
    }*/

    val auto = LangtonsAnt.automaton(dim)
    //val boardWindow = BoardWindow[LangtonsAnt]("Langtons Ant", toColor, dim, scale)
    //boardWindow.mainLoop(auto, _.copy(color = true, dir = Some(Up)))

    System.exit(0)
  }

  private def langtonsColors(dim:Int, it: Int, step: Int, scale: Int): Unit = {
    /*def toColor(c: LangtonsColors) =
      if (c.colors.isEmpty) Color.White else {
        val color = CMYK.sum(c.colors).toRGB
        Color(color.r, color.g, color.b)
      }
*/
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

  //  val boardWindow = BoardWindow[LangtonsColors]("Langtons Colors", toColor, dim = dim, scale = scale)

    val timestamp = System.currentTimeMillis()
    for (i <- 0 until it){
      val board = auto.next()
    //  if (i % step == 0) boardWindow.draw(board)
    }

    //boardWindow.draw(auto.next())

    println(s"${System.currentTimeMillis() - timestamp}")
  }

  private def langtonsColorsInteractive(dim: Int, scale: Int): Unit = {
    /*def toColor(c: LangtonsColors) =
      if (c.colors.isEmpty) Color.White else {
        val color = CMYK.sum(c.colors).toRGB
        Color(color.r, color.g, color.b)
      }
*/
    def randomDirs = {
      val d = Dir2D.dirs4(Random.nextInt(Dir2D.dirs4.length))
      val c = CMYK.colors(Random.nextInt(CMYK.colors.length))
      List((d, c))
    }

    val auto = LangtonsColors.automaton(dim)
  //  val boardWindow = BoardWindow[LangtonsColors]("Langtons Colors", toColor, dim, scale)
  //  boardWindow.mainLoop(auto, _.copy(dirs = randomDirs))

    System.exit(0)
  }

  private def chaseInteractive(dim: Int, scale: Int): Unit = {
    def randomColor(c: Chase): Chase = {
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      c.copy(color = color, brushes = List(color))
    }
/*
    def toColor(c: Chase) = if (c.center.contains(c.pos)) Color.Black else {
      val rgb = c.color.toRGB
      Color(rgb.r, rgb.g, rgb.b)
    }*/

    val auto = Chase.automaton(dim)
    //val boardWindow = BoardWindow[Chase]("Chase", toColor, dim, scale)
    /*boardWindow.mainLoop(auto,
      leftClick2 = (c, pos) => c.copy(center = Some(pos)),
      rightClick = randomColor
    )
*/
    System.exit(0)
  }

}

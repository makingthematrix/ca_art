package caart

import caart.chase.Chase
import caart.fields._
import caart.visualisation.FXGLApp
import com.almasb.fxgl.app.GameApplication
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

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

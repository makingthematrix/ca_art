package caart

import caart.visualisation.FXGLApp
import com.almasb.fxgl.app.GameApplication
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

import scala.jdk.CollectionConverters._

final class Main extends Application {
  override def start(stage: Stage): Unit = {
    val args = Arguments.parseArguments(getParameters.getRaw.asScala.toSeq)
    stage.setScene(new Scene(GameApplication.embeddedLaunch(new FXGLApp(args)), args.windowSize, args.windowSize))
    stage.show()
  }
}

object Main {
  def main(args: Array[String]): Unit = Application.launch(classOf[Main], args: _*)
}

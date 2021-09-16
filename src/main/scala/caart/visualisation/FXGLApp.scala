package caart.visualisation

import caart.Arguments
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.almasb.fxgl.dsl.FXGL
import javafx.scene.paint.Color
import javafx.scene.text.{Font, Text}

import scala.util.chaining.scalaUtilChainingOps

final class FXGLApp(args: Arguments) extends GameApplication {
  override def initSettings(gameSettings: GameSettings): Unit = {
    gameSettings.setWidth(args.windowSize)
    gameSettings.setHeight(args.windowSize)
    gameSettings.set3D(false)
    gameSettings.setApplicationMode(ApplicationMode.DEBUG)
    gameSettings.setGameMenuEnabled(true)
    gameSettings.setPixelsPerMeter(args.scale)
    gameSettings.setScaleAffectedOnResize(true)
  }

  override protected def initUI(): Unit = {
    println(s"args: $args")

    val uiText = new Text("Hello from FXGL").tap { text =>
      text.setFill(Color.WHITE)
      text.setFont(Font.font("Verdana", 20))
    }
    FXGL.addUINode(uiText, 100, 100)
  }
}


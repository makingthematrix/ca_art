package caart.visualisation

import caart.Arguments
import caart.fields.{Pos2D, RGB}
import caart.gameoflife.GameOfLife
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import javafx.scene.paint.Color

final class FXGLApp(args: Arguments) extends GameApplication {
  private val tileFactory = new TileFactory[GameOfLife](args.scale, toColor)
  private val auto = GameOfLife.automaton(args.dim)

  private lazy val tiles = auto.board.values.map(tileFactory.newTile)

  override def initSettings(gameSettings: GameSettings): Unit = {
    gameSettings.setWidth(args.windowSize)
    gameSettings.setHeight(args.windowSize)
    gameSettings.set3D(false)
    gameSettings.setApplicationMode(ApplicationMode.DEVELOPER)
    gameSettings.setGameMenuEnabled(true)
    gameSettings.setPixelsPerMeter(args.scale)
    gameSettings.setScaleAffectedOnResize(true)
  }

  override protected def initUI(): Unit = {
    println(s"args: $args")
    tiles
  }

  override protected def initGame(): Unit = {
    auto.update {
      case cell if cell.pos == Pos2D(10, 10) => cell.copy(life = true)
      case cell => cell
    }
    tiles.foreach(_.refresh())
  }

  private def next():Unit = {
    auto.next()
    tiles.foreach(_.refresh())
  }

  private def toColor(c: GameOfLife): Color = {
    val rgb = if (c.life) RGB.Black else RGB.White
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }
}


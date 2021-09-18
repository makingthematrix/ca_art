package caart.visualisation

import caart.Arguments
import caart.fields.{Pos2D, RGB}
import caart.gameoflife.GameOfLife
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.wire.signals.EventStream
import javafx.scene.paint.Color

final class FXGLApp(args: Arguments) extends GameApplication {
  private val onClick = EventStream[Pos2D]()
  private val tileFactory = new TileFactory[GameOfLife](args.scale, toColor, onClick)
  private val auto = GameOfLife.automaton(args.dim)

  onClick.foreach { pos =>
    println(s"click: $pos")
    auto.updateOne(pos) { cell => cell.copy(life = !cell.life) }
    tileMap(pos).refresh()
  }

  private lazy val tiles = auto.positions.map(pos => tileFactory.newTile(() => auto.findCell(pos)))
  private lazy val tileMap = tiles.map(t => t.pos -> t).toMap

  private def refreshTiles(): Unit = tiles.foreach(_.refresh())

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
    refreshTiles()
  }

  private def next():Unit = {
    auto.next()
    refreshTiles()
  }

  private def toColor(c: GameOfLife): Color = {
    val rgb = if (c.life) RGB.Black else RGB.White
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }
}


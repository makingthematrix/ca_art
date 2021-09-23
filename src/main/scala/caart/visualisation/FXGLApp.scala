package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell}
import caart.fields.{Pos2D, RGB}
import caart.gameoflife.GameOfLife
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.ui.UiDispatchQueue
import com.wire.signals.{CancellableFuture, EventStream, Serialized, Signal}
import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.chaining.scalaUtilChainingOps

final class FXGLApp(args: Arguments) extends GameApplication {
  import com.wire.signals.Threading.defaultContext
  import com.wire.signals.ui.UiDispatchQueue.Ui

  private val delay        = FiniteDuration(args.delay, TimeUnit.MILLISECONDS)
  private lazy val auto    = GameOfLife.automaton(args.dim)
  private lazy val tiles   = auto.positions.map(pos => newTile(() => auto.findCell(pos), toColor))
  private lazy val tileMap = tiles.map(t => t.pos -> t).toMap

  private val onClick = EventStream[Pos2D]()
  onClick.foreach { pos =>
    println(s"click: $pos")
    auto.updateOne(pos) { cell => cell.copy(life = !cell.life) }
    tileMap(pos).refresh()
  }

  private val gameState = Signal[GameState](GameState.Pause)
  gameState.foreach {
    case GameState.Play => run()
    case _ =>
  }

  private def run(): Future[Unit] = Serialized.future("auto") {
    for {
      _     <- Future.successful(auto.next())
      _     <- refreshTiles()
      state <- gameState.head
      _     <- if (state == GameState.Play) CancellableFuture.delayed(delay)(run()).future
               else Future.successful(())
    } yield ()
  }

  private def newTile[C <: AutomatonCell[C]](cell: () => C, toColor: C => Color): Tile[C] =
    Tile(cell, args.scale, toColor, onClick).tap { _.addToUi() }

  private def toColor(c: GameOfLife): Color = {
    val rgb = if (c.life) RGB.Black else RGB.White
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }

  private def refreshTiles() = Future { tiles.foreach(_.refresh()) }(Ui)

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
    UiDispatchQueue.setUi(Platform.runLater)
    tiles
    FXGL.onKeyUp(KeyCode.SPACE, () => gameState.mutate {
      case GameState.Pause => GameState.Play
      case GameState.Play  => GameState.Pause
    })
  }

  override protected def initGame(): Unit = refreshTiles()

  override
}


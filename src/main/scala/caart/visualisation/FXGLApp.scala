package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell}
import caart.fields.{Pos2D, RGB}
import caart.gameoflife.GameOfLife
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.{CancellableFuture, EventStream, Signal}
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.chaining.scalaUtilChainingOps

final class FXGLApp(args: Arguments) extends GameApplication {
  import com.wire.signals.Threading.defaultContext

  private val onClick = EventStream[Pos2D]()
  private val heartbeat = EventStream[Unit]()
  private val gameState = Signal[GameState](GameState.Pause)
  private val delayDuration = FiniteDuration(args.delay, TimeUnit.MILLISECONDS)

  private lazy val auto: Automaton[GameOfLife] = GameOfLife.automaton(args.dim)

  onClick.foreach { pos =>
    println(s"click: $pos")
    auto.updateOne(pos) { cell => cell.copy(life = !cell.life) }
    tileMap(pos).refresh()
  }

  heartbeat.foreach { _ =>
    gameState.head.foreach {
      case GameState.Play =>
        FXGL.getEventBus.fireEvent(new NextEvent(NextEvent.NextEventType))
        CancellableFuture.delayed(delayDuration) { heartbeat ! () }
      case GameState.Pause =>
    }
  }

  gameState.foreach {
    case GameState.Play => heartbeat ! ()
    case _ =>
  }

  private def newTile[C <: AutomatonCell[C]](cell: () => C, toColor: C => Color): Tile[C] =
    Tile(cell, args.scale, toColor, onClick).tap { _.addToUi() }

  private lazy val tiles = auto.positions.map(pos => newTile(() => auto.findCell(pos), toColor))
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
    gameSettings.addEngineService(classOf[AutomatonService])
  }

  override protected def initUI(): Unit = {
    println(s"args: $args")
    tiles
    FXGL.onKey(KeyCode.SPACE, () => gameState.mutate {
      case GameState.Pause => GameState.Play
      case GameState.Play  => GameState.Pause
    })
  }

  override protected def initGame(): Unit = {
    refreshTiles()
  }

  def next(): Unit = {
    auto.next()
    refreshTiles()
  }

  private def toColor(c: GameOfLife): Color = {
    val rgb = if (c.life) RGB.Black else RGB.White
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }
}


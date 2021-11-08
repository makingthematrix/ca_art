package caart.visualisation

import caart.Arguments
import caart.visualisation.Game.buildWorld
import caart.visualisation.examples.{ChaseWorld, GameOfLifeWorld, LangtonsAntWorld, LangtonsColorsWorld, SnakeWorld}
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.almasb.fxgl.dsl.FXGL
import com.typesafe.scalalogging.LazyLogging
import com.wire.signals.Signal
import com.wire.signals.ui.UiDispatchQueue
import javafx.application.Platform
import javafx.scene.input.KeyCode

final class Game(args: Arguments) extends GameApplication with LazyLogging {
  import com.wire.signals.Threading.defaultContext

  private lazy val world = buildWorld(args)

  private val gameState = Signal[GameState](GameState.Pause)
  gameState.foreach {
    case GameState.Play => run()
    case _ =>
  }

  private def run(): Unit =
    while(gameState.currentValue.contains(GameState.Play)) {
      val t = System.currentTimeMillis
      world.next()
      logger.debug(s"the turn took ${System.currentTimeMillis - t}ms")
      if (args.delay > 0L) Thread.sleep(args.delay)
    }

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
    logger.debug(s"args: $args")
    UiDispatchQueue.setUi(Platform.runLater)
    world.init()
  }

  override def initInput(): Unit = {
    FXGL.onKeyUp(KeyCode.SPACE, () => gameState.mutate {
      case GameState.Pause => GameState.Play
      case GameState.Play  => GameState.Pause
    })
  }
}

object Game {
  def buildWorld(args: Arguments): GameContract = args.example match {
    case Arguments.GameOfLifeExample     => new GameOfLifeWorld(args)
    case Arguments.LangtonsAntExample    => new LangtonsAntWorld(args)
    case Arguments.LangtonsColorsExample => new LangtonsColorsWorld(args)
    case Arguments.ChaseExample          => new ChaseWorld(args)
    case Arguments.SnakeExample          => new SnakeWorld(args)
  }
}
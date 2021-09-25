package caart.visualisation

import caart.Arguments
import caart.engine.AutomatonCell
import caart.fields.Pos2D
import com.almasb.fxgl.app.{ApplicationMode, GameApplication, GameSettings}
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.ui.UiDispatchQueue
import com.wire.signals.{CancellableFuture, EventStream, Serialized, Signal}
import javafx.application.Platform
import javafx.scene.input.KeyCode

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

final class FXGLApp(args: Arguments) extends GameApplication {
  import com.wire.signals.Threading.defaultContext
  import com.wire.signals.ui.UiDispatchQueue.Ui

  private val delay        = FiniteDuration(args.delay, TimeUnit.MILLISECONDS)
  private lazy val auto = AutoWrapper(args)

  private val gameState = Signal[GameState](GameState.Pause)
  gameState.foreach {
    case GameState.Play => run()
    case _ =>
  }

  private def run(): Future[Unit] = {
    /*while(gameState.currentValue.contains(GameState.Play)) {
      auto.next()
      Thread.sleep(args.delay)
    }*/

    val t = System.currentTimeMillis()
    for {
      _     <- auto.next()
      _ = println(s"refresh took ${System.currentTimeMillis() - t}ms")
      state <- gameState.head
      _     <- if (state == GameState.Play) CancellableFuture.delayed(delay)(run()).future
               else Future.successful(())
    } yield ()
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
    println(s"args: $args")
    UiDispatchQueue.setUi(Platform.runLater)
    auto.init()
    FXGL.onKeyUp(KeyCode.SPACE, () => gameState.mutate {
      case GameState.Pause => GameState.Play
      case GameState.Play  => GameState.Pause
    })
  }

  override protected def initGame(): Unit = {}
}


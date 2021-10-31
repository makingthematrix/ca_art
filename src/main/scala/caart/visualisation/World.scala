package caart.visualisation

import caart.Arguments
import caart.engine.fields.Pos2D
import caart.engine.{Automaton, AutomatonCell, Board}
import caart.visualisation.examples.{ChaseWorld, GameOfLifeWorld, LangtonsAntWorld, LangtonsColorsWorld}
import com.almasb.fxgl.dsl.FXGL
import com.typesafe.scalalogging.LazyLogging
import com.wire.signals.ui.UiDispatchQueue.Ui
import com.wire.signals.{EventStream, Signal, SourceStream}
import javafx.scene.canvas.Canvas
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.paint.Color

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

abstract class World[C <: AutomatonCell[C]] extends LazyLogging {
  def args: Arguments
  def auto: Automaton[C]
  protected def toColor(c: C): Color
  protected def updateFromEvent(event: UserEvent): Board[C]

  private val onUserEvent: SourceStream[UserEvent] = EventStream[UserEvent]()
  onUserEvent.foreach(event => updateBoard { updateFromEvent(event) })

  private val drag = Signal(Option.empty[Pos2D])
  drag.onUpdated.collect { case (Some(Some(prev)), _) => UserEvent(prev, UserEventType.LeftClick) }.pipeTo(onUserEvent)

  private val canvas = new Canvas().tap { canvas =>
    canvas.setWidth(args.windowSize.toDouble)
    canvas.setHeight(args.windowSize.toDouble)
  }

  private var currentBoard = Option.empty[Board[C]]
  private var currentTurn = 0L

  def updateBoard(newBoard: Board[C]): Unit = {
    val toUpdate = currentBoard.fold(newBoard.cells)(newBoard - _).groupBy(toColor)
    if (toUpdate.nonEmpty) {
      currentBoard = Some(newBoard)
      Future {
        val graphics = canvas.getGraphicsContext2D
        toUpdate.foreach { case (color, cells) =>
          graphics.setFill(color)
          cells.foreach { c =>
            graphics.fillRect(c.pos.x * args.scale, c.pos.y * args.scale, args.scale, args.scale)
          }
        }
      }(Ui)
    }
  }

  def next(): Unit = {
    var t = System.currentTimeMillis
    val newBoard = auto.next()
    logger.debug(s"--- auto next: ${System.currentTimeMillis - t}ms")
    if (currentTurn % args.step == 0) {
      updateBoard(newBoard)
      if (args.enforceGC) {
        t = System.currentTimeMillis
        System.gc()
        logger.debug(s"--- garbage collection: ${System.currentTimeMillis - t}ms")
      }
    }
    currentTurn += 1L
  }

  def init(): Unit = {
    FXGL.addUINode(canvas)

    canvas.setOnMouseDragged { (ev: MouseEvent) =>
      val p = Pos2D(ev.getSceneX.toInt / args.scale, ev.getSceneY.toInt / args.scale)
      drag ! Some(p)
    }

    canvas.setOnMousePressed { (ev: MouseEvent) =>
      ev.setDragDetect(true)
      val p = Pos2D(ev.getSceneX.toInt / args.scale, ev.getSceneY.toInt / args.scale)
      if (ev.getButton == MouseButton.PRIMARY) onUserEvent ! UserEvent(p, UserEventType.LeftClick)
      else if (ev.getButton == MouseButton.SECONDARY) onUserEvent ! UserEvent(p, UserEventType.RightClick)
    }

    canvas.setOnMouseReleased { (_: MouseEvent) => drag ! None }

    Future {
      canvas.getGraphicsContext2D.setFill(Color.WHITE)
      canvas.getGraphicsContext2D.fillRect(0.0, 0.0, args.windowSize.toDouble, args.windowSize.toDouble)
    }(Ui)
  }
}

object World {
  def apply(args: Arguments): World[_] = args.example match {
    case Arguments.GameOfLifeExample     => new GameOfLifeWorld(args)
    case Arguments.LangtonsAntExample    => new LangtonsAntWorld(args)
    case Arguments.LangtonsColorsExample => new LangtonsColorsWorld(args)
    case Arguments.ChaseExample          => new ChaseWorld(args)
  }
}
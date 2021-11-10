package caart.visualisation

import caart.Arguments
import caart.engine.GlobalCell.Empty
import caart.engine.fields.Pos2D
import caart.engine.{Automaton, AutomatonNoGlobal, Board, Cell, GlobalCell}
import com.almasb.fxgl.dsl.FXGL
import com.typesafe.scalalogging.LazyLogging
import com.wire.signals.ui.UiDispatchQueue.Ui
import com.wire.signals.{EventStream, Signal, SourceStream}
import javafx.scene.canvas.Canvas
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.paint.Color

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

abstract class World[C <: Cell[C], GC <: GlobalCell[C, GC]] extends GameContract with LazyLogging {
  protected val args: Arguments
  protected val auto: Automaton[C, GC]
  protected def toColor(c: C): Color
  protected def processUserEvent(event: UserEvent): Unit

  protected val onUserEvent: SourceStream[UserEvent] = EventStream[UserEvent]()
  onUserEvent.foreach { event =>
    processUserEvent(event)
    event.pos.map(auto.findCell).foreach { cell =>
      val color = toColor(cell)
      Future {
        val graphics = canvas.getGraphicsContext2D
        graphics.setFill(color)
        graphics.fillRect(cell.pos.x * args.scale, cell.pos.y * args.scale, args.scale, args.scale)
      }(Ui)
    }
  }

  private val drag = Signal(Option.empty[Pos2D])
  drag.onUpdated.collect { case (Some(prev), _) => UserEvent(prev, UserEventType.LeftClick) }.pipeTo(onUserEvent)

  protected val canvas: Canvas = new Canvas().tap { canvas =>
    canvas.setWidth(args.windowSize.toDouble)
    canvas.setHeight(args.windowSize.toDouble)
  }

  private var currentBoard = Option.empty[Board[C]]
  private var currentTurn = 0L

  private def updateBoard(newBoard: Board[C]): Unit = {
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

  override def next(): Boolean = {
    var t = System.currentTimeMillis
    val newBoard = auto.next()
    //logger.debug(s"--- auto next: ${System.currentTimeMillis - t}ms")
    if (currentTurn % args.step == 0) {
      updateBoard(newBoard)
      if (args.enforceGC) {
        t = System.currentTimeMillis
        System.gc()
      }
    }
    currentTurn += 1L
    true
  }

  override def init(): Unit = {
    FXGL.addUINode(canvas)

    canvas.setOnMouseDragged { (ev: MouseEvent) =>
      val p = Pos2D(ev.getSceneX.toInt / args.scale, ev.getSceneY.toInt / args.scale)
      drag ! Some(p)
    }

    canvas.setOnMousePressed { (ev: MouseEvent) =>
      ev.setDragDetect(true)
      val p = Pos2D(ev.getSceneX.toInt / args.scale, ev.getSceneY.toInt / args.scale)
      if (ev.getButton == MouseButton.PRIMARY) onUserEvent ! UserEvent(Some(p), UserEventType.LeftClick)
      else if (ev.getButton == MouseButton.SECONDARY) onUserEvent ! UserEvent(Some(p), UserEventType.RightClick)
    }

    canvas.setOnMouseReleased { (_: MouseEvent) => drag ! None }

    Future {
      canvas.getGraphicsContext2D.setFill(Color.WHITE)
      canvas.getGraphicsContext2D.fillRect(0.0, 0.0, args.windowSize.toDouble, args.windowSize.toDouble)
    }(Ui)
  }
}

abstract class WorldNoGlobal[C <: Cell[C]] extends World[C, Empty[C]] {
  override protected val auto: AutomatonNoGlobal[C]
}

trait GameContract {
  def next(): Boolean
  def init(): Unit
}
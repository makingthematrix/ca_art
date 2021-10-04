package caart.visualisation

import caart.engine.AutomatonCell
import caart.fields.Pos2D
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.{Signal, SourceStream}
import javafx.geometry.Point2D
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

final case class Tile[C <: AutomatonCell[C]](cell:         () => C,
                                             size:         Int,
                                             toColor:      C => Color,
                                             onUserEvent:  SourceStream[UserEvent]) {
  private val drag = Signal(Option.empty[Pos2D])
  drag.onUpdated.collect { case (Some(Some(prev)), _) => UserEvent(prev, UserEventType.LeftClick) }.pipeTo(onUserEvent)

  private val tile = new Rectangle(size, size, toColor(cell()))
  val pos: Pos2D = cell().pos

  def refresh(): Unit = tile.setFill(toColor(cell()))

  def initialize(): Unit = {
    val position = new Point2D(size * pos.x, size * pos.y)
    FXGL.addUINode(tile, position.getX, position.getY)

    tile.setOnMouseDragged { (ev: MouseEvent) =>
      val p = Pos2D(ev.getSceneX.toInt / size, ev.getSceneY.toInt / size)
      if (p != pos) drag ! Some(p)
    }

    tile.setOnMousePressed { (ev: MouseEvent) =>
      ev.setDragDetect(true)
      if (ev.getButton == MouseButton.PRIMARY) onUserEvent ! UserEvent(pos, UserEventType.LeftClick)
      else if (ev.getButton == MouseButton.SECONDARY) onUserEvent ! UserEvent(pos, UserEventType.RightClick)
    }

    tile.setOnMouseReleased { (_: MouseEvent) => drag ! None }
  }
}

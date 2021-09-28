package caart.visualisation

import caart.engine.AutomatonCell
import caart.fields.Pos2D
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.SourceStream
import javafx.geometry.Point2D
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

final case class Tile[C <: AutomatonCell[C]](cell:         () => C,
                                             size:         Int,
                                             toColor:      C => Color,
                                             onLeftClick:  SourceStream[Pos2D],
                                             onRightClick: SourceStream[Pos2D]) {
  private val tile = new Rectangle(size, size, toColor(cell()))
  val pos: Pos2D = cell().pos

  def refresh(): Unit = tile.setFill(toColor(cell()))

  def addToUi(): Unit = {
    val position = new Point2D(size * pos.x, size * pos.y)
    FXGL.addUINode(tile, position.getX, position.getY)
    tile.setOnMouseClicked { (ev: MouseEvent) =>
      if (ev.getButton == MouseButton.PRIMARY) onLeftClick ! pos
      else if (ev.getButton == MouseButton.SECONDARY) onRightClick ! pos
    }
  }
}

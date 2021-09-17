package caart.visualisation

import caart.engine.AutomatonCell
import com.almasb.fxgl.dsl.FXGL
import com.sun.javafx.scene.{DirtyBits, NodeHelper}
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

final case class Tile[C <: AutomatonCell[C]](cell: C, position: Point2D, size: Int, colorFunction: C => Color) {
  private val tile = new Rectangle(size, size, colorFunction(cell))

  def refresh(): Unit = {
    tile.setFill(colorFunction(cell))
    NodeHelper.markDirty(tile, DirtyBits.SHAPE_FILL)
  }

  def addToUi(): Unit = FXGL.addUINode(tile, position.getX, position.getY)
}

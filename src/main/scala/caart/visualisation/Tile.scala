package caart.visualisation

import caart.engine.AutomatonCell
import caart.fields.Pos2D
import com.almasb.fxgl.dsl.FXGL
import com.wire.signals.SourceStream
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

final case class Tile[C <: AutomatonCell[C]](cell: () => C, size: Int, colorFunction: C => Color, onClick: SourceStream[Pos2D]) {
  private val tile = new Rectangle(size, size, colorFunction(cell()))
  val pos: Pos2D = cell().pos

  def refresh(): Unit = tile.setFill(colorFunction(cell()))

  def addToUi(): Unit = {
    val position = new Point2D(size * pos.x, size * pos.y)
    FXGL.addUINode(tile, position.getX, position.getY)
    tile.setOnMouseClicked((_: MouseEvent) => onClick ! pos)
  }
}
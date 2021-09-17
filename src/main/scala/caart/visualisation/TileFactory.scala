package caart.visualisation

import caart.engine.AutomatonCell
import javafx.geometry.Point2D
import javafx.scene.paint.Color

import scala.util.chaining.scalaUtilChainingOps

class TileFactory[C <:AutomatonCell[C]](scale: Int, colorFunction: C => Color) {
  def newTile(cell: C): Tile[C] =
    Tile(cell, new Point2D(scale * cell.pos.x, scale * cell.pos.y), scale, colorFunction).tap { _.addToUi() }
}

package caart.visualisation

import caart.engine.AutomatonCell
import caart.fields.Pos2D
import com.wire.signals.SourceStream
import javafx.scene.paint.Color

import scala.util.chaining.scalaUtilChainingOps

class TileFactory[C <:AutomatonCell[C]](scale: Int, colorFunction: C => Color, onClick: SourceStream[Pos2D]) {
  def newTile(cell: () => C): Tile[C] =
    Tile(cell, scale, colorFunction, onClick).tap { _.addToUi() }
}

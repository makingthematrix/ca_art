package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell, Board}
import caart.fields.Pos2D
import com.wire.signals.SourceStream
import javafx.scene.paint.Color

import scala.util.chaining.scalaUtilChainingOps

trait AutoWrapper[C <: AutomatonCell[C]] {
  def args: Arguments
  def auto: Automaton[C]
  def updateOne(pos: Pos2D): Board[C]
  protected def toColor(c: C): Color

  def next(): Board[C] = auto.next()

  def createTiles(onClick: SourceStream[Pos2D]): Set[Tile[C]] =
    auto.positions.map(pos => newTile(() => auto.findCell(pos), onClick))

  protected def newTile(cell: () => C, onClick: SourceStream[Pos2D]): Tile[C] =
    Tile(cell, args.scale, toColor, onClick).tap { _.addToUi() }
}

object AutoWrapper {
  def apply(args: Arguments): AutoWrapper[_] = args.example match {
    case Arguments.GameOfLifeInteractiveExample => new GameOfLifeWrapper(args)
    case _ => new GameOfLifeWrapper(args)
  }
}
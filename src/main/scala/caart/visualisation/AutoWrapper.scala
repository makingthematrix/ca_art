package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell, Board}
import caart.fields.Pos2D
import com.wire.signals.{EventStream, SourceStream}
import com.wire.signals.ui.UiDispatchQueue.Ui
import javafx.scene.paint.Color

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

abstract class AutoWrapper[C <: AutomatonCell[C]] {
  def args: Arguments
  def auto: Automaton[C]
  def updateOne(pos: Pos2D): Unit
  protected def toColor(c: C): Color

  val onClick: SourceStream[Pos2D] = EventStream[Pos2D]()
  onClick.foreach { pos =>
    println(s"click: $pos")
    updateOne(pos)
  }

  private var currentBoard = Option.empty[Board[C]]

  def next(): Future[Unit] = {
    val t = System.currentTimeMillis()
    val newBoard: Board[C] = auto.next()
    println(s"--- auto next: ${System.currentTimeMillis() - t}ms")
    val toUpdate: List[C] = currentBoard.fold(newBoard.cells)(newBoard - _)
    currentBoard = Some(newBoard)
    Future {
      val t1 = System.currentTimeMillis()
      toUpdate.foreach(c => tileMap(c.pos).refresh())
      println(s"--- tile refresh: ${System.currentTimeMillis() - t1}ms")
    }(Ui)
  }

  def init(): Unit = {
    tiles
    tileMap
    tiles.foreach(_.refresh())
  }

  protected def newTile(cell: () => C, onClick: SourceStream[Pos2D]): Tile[C] =
    Tile(cell, args.scale, toColor, onClick).tap { _.addToUi() }

  protected lazy val tiles: Set[Tile[C]] = auto.positions.map(pos => newTile(() => auto.findCell(pos), onClick))
  protected lazy val tileMap: Map[Pos2D, Tile[C]] = tiles.map(t => t.pos -> t).toMap
}

object AutoWrapper {
  def apply(args: Arguments): AutoWrapper[_] = args.example match {
    case Arguments.GameOfLifeInteractiveExample => new GameOfLifeWrapper(args)
    case _ => new GameOfLifeWrapper(args)
  }
}
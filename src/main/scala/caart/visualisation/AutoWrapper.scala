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
  onClick.foreach(updateOne)

  private var currentBoard = Option.empty[Board[C]]
  private var currentTurn = 0L

  def updateBoard(newBoard: Board[C]): Future[Unit] = {
    val t = System.currentTimeMillis()
    val toUpdate: List[C] = currentBoard.fold(newBoard.cells)(newBoard - _)
    println(s"--- gathering what to update: ${System.currentTimeMillis() - t}ms (${toUpdate.length})")
    currentBoard = Some(newBoard)
    Future {
      val t = System.currentTimeMillis()
      toUpdate.foreach(c => tileMap(c.pos).refresh())
      println(s"--- tile refresh: ${System.currentTimeMillis() - t}ms")
    }(Ui)
  }

  def next(): Unit = {
    var t = System.currentTimeMillis()
    val newBoard: Board[C] = auto.next()
    println(s"--- auto next: ${System.currentTimeMillis() - t}ms")
    if (currentTurn % args.step == 0) {
      updateBoard(newBoard)
      if (args.enforceGC) {
        t = System.currentTimeMillis()
        System.gc()
        println(s"--- garbage collection: ${System.currentTimeMillis() - t}ms")
      }
    }
    currentTurn += 1L
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
    case Arguments.GameOfLifeExample     => new GameOfLifeWrapper(args)
    case Arguments.LangtonsAntExample    => new LangtonsAntWrapper(args)
    case Arguments.LangtonsColorsExample => new LangtonsColorsWrapper(args)
    case _                               => new GameOfLifeWrapper(args)
  }

}
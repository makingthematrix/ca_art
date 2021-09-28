package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell, Board}
import caart.fields.Pos2D
import com.wire.signals.ui.UiDispatchQueue.Ui
import com.wire.signals.{EventStream, SourceStream}
import javafx.scene.paint.Color

import scala.concurrent.Future

abstract class AutoWrapper[C <: AutomatonCell[C]] {
  def args: Arguments
  def auto: Automaton[C]
  protected def toColor(c: C): Color

  val onLeftClick: SourceStream[Pos2D] = EventStream[Pos2D]()
  val onRightClick: SourceStream[Pos2D] = EventStream[Pos2D]()

  private lazy val tiles: Map[Pos2D, Tile[C]] =
    auto.positions.map { pos =>
      val tile = Tile(() => auto.findCell(pos), args.scale, toColor, onLeftClick, onRightClick)
      pos -> tile
    }.toMap

  private var currentBoard = Option.empty[Board[C]]
  private var currentTurn = 0L

  def updateBoard(newBoard: Board[C]): Future[Unit] = {
    val t = System.currentTimeMillis()
    val toUpdate: List[C] = currentBoard.fold(newBoard.cells)(newBoard - _)
    println(s"--- gathering what to update: ${System.currentTimeMillis() - t}ms (${toUpdate.length})")
    currentBoard = Some(newBoard)
    Future {
      val t = System.currentTimeMillis()
      toUpdate.foreach(c => tiles(c.pos).refresh())
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

  def init(): Unit =
    tiles.values.foreach { _.addToUi() }
}

object AutoWrapper {
  def apply(args: Arguments): AutoWrapper[_] = args.example match {
    case Arguments.GameOfLifeExample     => new GameOfLifeWrapper(args)
    case Arguments.LangtonsAntExample    => new LangtonsAntWrapper(args)
    case Arguments.LangtonsColorsExample => new LangtonsColorsWrapper(args)
    case Arguments.ChaseExample          => new ChaseWrapper(args)
  }
}
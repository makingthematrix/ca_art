package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, AutomatonCell, Board}
import caart.engine.fields.Pos2D
import caart.visualisation.examples.{ChaseWorld, GameOfLifeWorld, LangtonsAntWorld, LangtonsColorsWorld}
import com.typesafe.scalalogging.LazyLogging
import com.wire.signals.ui.UiDispatchQueue.Ui
import com.wire.signals.{EventStream, SourceStream}
import javafx.scene.paint.Color

import scala.concurrent.Future

abstract class World[C <: AutomatonCell[C]] extends LazyLogging {
  def args: Arguments
  def auto: Automaton[C]
  protected def toColor(c: C): Color
  protected def updateFromEvent(event: UserEvent): Board[C]

  private val onUserEvent: SourceStream[UserEvent] = EventStream[UserEvent]()
  onUserEvent.foreach(event => updateBoard { updateFromEvent(event) })

  private lazy val tiles: Map[Pos2D, Tile[C]] =
    auto.positions.map { pos =>
      pos -> Tile(() => auto.findCell(pos), args.scale, toColor, onUserEvent)
    }.toMap

  private var currentBoard = Option.empty[Board[C]]
  private var currentTurn = 0L

  def updateBoard(newBoard: Board[C]): Future[Unit] = {
    val t = System.currentTimeMillis
    val toUpdate = currentBoard.fold(newBoard.cells)(newBoard - _)
    logger.debug(s"--- gathering what to update: ${System.currentTimeMillis - t}ms (${toUpdate.length})")
    currentBoard = Some(newBoard)
    Future { toUpdate.foreach(c => tiles(c.pos).refresh()) }(Ui)
  }

  def next(): Unit = {
    var t = System.currentTimeMillis
    val newBoard = auto.next()
    logger.debug(s"--- auto next: ${System.currentTimeMillis - t}ms")
    if (currentTurn % args.step == 0) {
      updateBoard(newBoard)
      if (args.enforceGC) {
        t = System.currentTimeMillis
        System.gc()
        logger.debug(s"--- garbage collection: ${System.currentTimeMillis - t}ms")
      }
    }
    currentTurn += 1L
  }

  def init(): Unit = tiles.values.foreach { _.initialize() }
}

object World {
  def apply(args: Arguments): World[_] = args.example match {
    case Arguments.GameOfLifeExample     => new GameOfLifeWorld(args)
    case Arguments.LangtonsAntExample    => new LangtonsAntWorld(args)
    case Arguments.LangtonsColorsExample => new LangtonsColorsWorld(args)
    case Arguments.ChaseExample          => new ChaseWorld(args)
  }
}
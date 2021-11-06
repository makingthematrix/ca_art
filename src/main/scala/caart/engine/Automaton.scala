package caart.engine

import caart.engine.fields.{Dir2D, Pos2D}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

/** The main class of a cellular automaton.
  *
  * To create a cellular automaton, the user has to specify the size of the board, `dim`, (for simpilicity reasons 
  * before the ScalaIO talk I assumed the board has the same length in all dimensions), and provide 
  * a function which will be used to create automaton's cells. Optionally, the user can provide a specialized
  * function for creating the board, eg. in case of cell.a. which can be optimized by skipping computations
  * on a part of the board. 
  *
  * TODO: Move away from the default board being a torus with a square surface. `dim` should be specified inside 
  * `applyBoard`, and `applyBoard` should not be optional. Instead, allow for choosing from  a list of utility methods, 
  * similar to how neighborhoods work.
  *
  * @constructor Takes the board edge size, a function for creating cells, and a function to create the board.
  */
class Automaton[C <: Cell[C]](dim: Int,
                              private val createCell:  (Pos2D, Cell.AutoContract[C]) => C,
                              private val createBoard: (Int, Pos2D => C) => Board[C],
                              private val createGlobalCell: GlobalCell.AutoContract[C] => C#GC)
  extends Iterator[Board[C]] with Cell.AutoContract[C] with GlobalCell.AutoContract[C] with LazyLogging { self: Automaton[C] =>
  private var _cellEvents = List.empty[(Pos2D, C#CE)]
  private var _globalEvents = List.empty[C#GC#GCE]
  private var _board: Board[C] = createBoard(dim, createCell(_, this))
  private var _globalCell: C#GC = createGlobalCell(this)

  override def addEvent(pos: Pos2D, event: C#CE): Unit =
    _cellEvents ::= (pos -> event)

  override def addEvent(event: C#GC#GCE): Unit =
    _globalEvents ::= event

  override final def globalCell: C#GC = _globalCell
  final def cellEvents: Map[Pos2D, Iterable[C#CE]] =
    _cellEvents.groupBy(_._1).map { case (pos, events) => (pos, events.map(_._2)) }
  final def globalEvents: List[C#GC#GCE] = _globalEvents
  final def oneCellEvents(pos: Pos2D): List[C#CE] = _cellEvents.collect {
    case (p, event) if pos == p => event
  }

  final def updatedByEvents(cell: C): Option[C] = {
    val events: List[C#CE] = oneCellEvents(cell.pos)
    if (events.nonEmpty) cell.updateFromEvents(events) else None
  }

  final def globalUpdatedByEvents: Option[C#GC] = globalCell.updateFromEvents(globalEvents)

  override def next(): Board[C] = {
    val ges = globalEvents
    val ces = cellEvents
    _globalEvents = Nil
    _cellEvents = Nil
    _globalCell = _globalCell.next(ges)
    _board = _board.next(ces)
    _board
  }

  override def hasNext: Boolean = true

  override def board: Board[C] = _board

  /** Updates the current state of the iterator.
    *
    * Used to provide the initial state of the automaton or to perform a change in the middle of the main loop.
    * `updater` is a function which for a given cell returns its updated version (or the cell itself, without
    * changes). It's called for every cell on the board in order to create an updated copy of the board, which
    * in turn replaces the old one.
    *
    * @param updater Called for every cell; creates an updated copy or returns the old one.
    * @return the updated board
    */
  def update(updater: C => C): Board[C] = {
    _board = _board.copy(updater)
    _board
  }

  def updateCell(pos: Pos2D)(updater: C => C): Board[C] = {
    _board = _board.copy(pos)(updater)
    _board
  }

  def updateGlobal(updater: C#GC => C#GC): C#GC = {
    _globalCell = updater(_globalCell)
    _globalCell
  }

  def cells: Vector[C] = _board.cells
  val positions: Set[Pos2D] = Pos2D(dim).toSet
  def findCell(pos: Pos2D): C = _board.findCell(pos)

  /**
    * The von Neumann's neighborhood is a collection of four cells which are
    * up, right, down, and left from the given one. The method returns them
    * as a map where keys are the corresponding Dir2D constants.
    */
  override final def neumann(pos: Pos2D): Map[Dir2D, C] =
    neumannFind(pos).map { case (dir, neighbor) => dir -> _board.findCell(neighbor) }

  private final val neumannMap = new mutable.HashMap[Pos2D, Map[Dir2D, Pos2D]]()
  private final def neumannFind(pos: Pos2D): Map[Dir2D, Pos2D] =
    neumannMap.getOrElseUpdate(pos, Dir2D.dirs4.map(dir => dir -> pos.move(dir)).toMap)

  /**
    * The Moore's neighborhood is a collection of eight cells which are
    * up, up-right, right, right-down, down, down-left, left, and left-up from
    * the given one. The method returns them as a map where keys are
    * the corresponding Dir2D constants.
    */
  override final def moore(pos: Pos2D): Map[Dir2D, C] =
    mooreFind(pos).map { case (dir, neighbor) => dir -> _board.findCell(neighbor) }

  private final val mooreMap = new mutable.HashMap[Pos2D, Map[Dir2D, Pos2D]]()
  private final def mooreFind(pos: Pos2D): Map[Dir2D, Pos2D] =
    mooreMap.getOrElseUpdate(pos, Dir2D.dirs8.map(dir => dir -> pos.move(dir)).toMap)
}

object Automaton {
  trait Creator[C <: Cell[C]] {
    def cell(pos: Pos2D, auto: Cell.AutoContract[C]): C
    def globalCell(auto: GlobalCell.AutoContract[C]): C#GC

    def automaton(dim: Int): Automaton[C] = new Automaton[C](dim, cell, Board.apply, globalCell)
  }
}

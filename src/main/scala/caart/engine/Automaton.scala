package caart.engine

import caart.engine.GlobalCell.Empty
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
class Automaton[C <: Cell[C], GC <: GlobalCell[C, GC]](
  val dim: Int,
  private val createCell:  (Pos2D, Cell.AutoContract[C, GC]) => C,
  private val createBoard: (Int, Pos2D => C) => Board[C],
  private val createGlobalCell: GlobalCell.AutoContract[C, GC] => GC,
  override val updateStrategy: UpdateStrategy.Type[C],
  override val globalUpdateStrategy: GlobalUpdateStrategy.Type[C, GC]
) extends Iterator[Board[C]]
  with Cell.AutoContract[C, GC]
  with GlobalCell.AutoContract[C, GC]
  with LazyLogging { self: Automaton[C, GC] =>
  override val eventHub: EventHub[C, GC] = new EventHub

  private var _board: Board[C] = createBoard(dim, createCell(_, this))
  private var _globalCell: GC = createGlobalCell(this)

  override final def globalCell: GC = _globalCell

  final def updatedByEvents(cell: C): Option[C] = {
    val events: List[C#CE] = eventHub.oneCellEvents(cell.pos)
    if (events.nonEmpty) cell.updateFromEvents(events) else None
  }

  final def globalUpdatedByEvents: Option[GC] = globalCell.updateFromEvents(eventHub.globalEvents)

  override def next(): Board[C] = {
    _globalCell = _globalCell.next(eventHub.drainGlobalEvents())
    _board = _board.next(eventHub.drainCellEvents())
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

  def updateGlobal(updater: GC => GC): GC = {
    _globalCell = updater(_globalCell)
    _globalCell
  }

  def cells: Vector[C] = _board.cells
  val positions: Set[Pos2D] = Pos2D(dim).toSet
  override def findCell(pos: Pos2D): C = _board.findCell(pos)

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

class AutomatonNoGlobal[C <: Cell[C]](override val dim: Int,
                                      private val createCell:  (Pos2D, Cell.AutoContractNoGlobal[C]) => C,
                                      private val createBoard: (Int, Pos2D => C) => Board[C],
                                      override val updateStrategy: UpdateStrategy.Type[C])
  extends Automaton[C, Empty[C]](
    dim,
    createCell,
    createBoard,
    (_: GlobalCell.AutoContract[C, Empty[C]]) => GlobalCell.empty[C],
    updateStrategy,
    GlobalUpdateStrategy.noUpdate[C, Empty[C]]
  )

object Automaton {
  trait Creator[C <: Cell[C], GC <: GlobalCell[C, GC]] {
    def cell(pos: Pos2D, auto: Cell.AutoContract[C, GC]): C
    def globalCell(auto: GlobalCell.AutoContract[C, GC]): GC

    def automaton(dim: Int,
                  updateStrategy: UpdateStrategy.Type[C] = UpdateStrategy.eventsOverrideSelf[C],
                  globalUpdateStrategy: GlobalUpdateStrategy.Type[C, GC] = GlobalUpdateStrategy.eventsOverrideSelf[C, GC]): Automaton[C, GC] =
      new Automaton[C, GC](dim, cell, Board.apply, globalCell, updateStrategy, globalUpdateStrategy)
  }

  trait CreatorNoGlobal[C <: Cell[C]] extends Creator[C, Empty[C]] {
    override def globalCell(auto: GlobalCell.AutoContract[C, Empty[C]]): Empty[C] = GlobalCell.empty[C]

    def automatonNoGlobal(dim: Int, updateStrategy: UpdateStrategy.Type[C] = UpdateStrategy.eventsOverrideSelf[C]): AutomatonNoGlobal[C] =
      new AutomatonNoGlobal[C](dim, cell, Board.apply, updateStrategy)
  }
}

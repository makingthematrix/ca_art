package caart.engine

import caart.engine.fields.{Dir2D, Pos2D}

/** The trait which must be implemented by every Cellular Automaton's cell class.
  * Every cell must know its position on the board (`pos`), it must have access
  * to other cells of its type (`findCell`), and it must implement the rules which
  * control the creation of its successor (`selfUpdate`).
  *
  * @tparam C the lower bound for the cell is this trait itself
  */
trait Cell[C <: Cell[C]] { self: C =>
  type GC <: GlobalCell[C, GC]
  type CE <: Cell.Event

  val pos: Pos2D
  val auto: Cell.AutoContract[C, GC]

  def selfUpdate: Option[C]
  def updateFromEvents(events: Iterable[C#CE]): Option[C]

  def needsSelfUpdate: Boolean = true

  @inline final def next(events: Iterable[C#CE]): C = auto.updateStrategy(self, events)
}

object Cell {
  trait Event

  trait AutoContract[C <: Cell[C], GC <: GlobalCell[C, GC]] {
    val updateStrategy: UpdateStrategy.Type[C]

    def globalCell: GC
    def addEvent(pos: Pos2D, event: C#CE): Unit
    def addEvent(event: GC#GCE): Unit

    def neumann(pos: Pos2D): Map[Dir2D, C]
    def moore(pos: Pos2D): Map[Dir2D, C]
  }
}
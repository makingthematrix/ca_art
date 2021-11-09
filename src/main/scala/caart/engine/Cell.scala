package caart.engine

import caart.engine.GlobalCell.Empty
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
  final case class NoEvent() extends Event

  trait AutoContract[C <: Cell[C], GC <: GlobalCell[C, GC]] {
    val updateStrategy: UpdateStrategy.Type[C]
    val eventHub: EventHub.AddEvents[C, GC]

    def globalCell: GC

    def findCell(pos: Pos2D): C
    def neumann(pos: Pos2D): Map[Dir2D, C]
    def moore(pos: Pos2D): Map[Dir2D, C]
  }

  type AutoContractNoGlobal[C <: Cell[C]] = AutoContract[C, Empty[C]]

  trait NoGlobal[C <: Cell[C]] extends Cell[C] { self: C =>
    override type GC = Empty[C]
    override val auto: Cell.AutoContractNoGlobal[C] // no change really, but maybe it will be better readable in IDE hints?...
  }

  trait NoEvents[C <: Cell[C]] extends Cell[C] { self: C =>
    override type CE = Cell.NoEvent
    override final def updateFromEvents(events: Iterable[C#CE]): Option[C] = None
  }

  trait CellOnlySelfUpdate[C <: Cell[C]] extends NoGlobal[C] with NoEvents[C] { self: C => }
}
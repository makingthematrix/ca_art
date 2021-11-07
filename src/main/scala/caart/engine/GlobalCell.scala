package caart.engine

import caart.engine.GlobalUpdateStrategy.Type
import caart.engine.fields.Pos2D

trait GlobalCell[C <: Cell[C], GC <: GlobalCell[C, GC]] { self: GC =>
  type GCE <: GlobalCell.Event
  protected val auto: GlobalCell.AutoContract[C, GC]
  def selfUpdate: Option[GC]
  def updateFromEvents(events: Iterable[GC#GCE]): Option[GC]

  def needsSelfUpdate: Boolean = true

  @inline final def next(events: Iterable[GC#GCE]): GC = auto.globalUpdateStrategy(self, events)
}

object GlobalCell {
  trait Event

  trait AutoContract[C <: Cell[C], GC <: GlobalCell[C, GC]] {
    val globalUpdateStrategy: GlobalUpdateStrategy.Type[C, GC]

    def board: Board[C]
    def addEvent(pos: Pos2D, event: C#CE): Unit
  }

  def noAutoContract[C <: Cell[C], GC <: GlobalCell[C, GC]]: AutoContract[C, GC] = new AutoContract[C, GC] {
    override def board: Board[C] = Board.empty[C]
    override def addEvent(pos: Pos2D, event: C#CE): Unit = ()

    override val globalUpdateStrategy: Type[C, GC] = GlobalUpdateStrategy.onlyEvents[C, GC]
  }

  final case class Empty[C <: Cell[C]] private() extends GlobalCell[C, Empty[C]] {
    override type GCE = GlobalCell.Event
    override def selfUpdate: Option[Empty[C]] = None
    override def needsSelfUpdate: Boolean = false
    override val auto: AutoContract[C, Empty[C]] = noAutoContract[C, Empty[C]]
    override def updateFromEvents(events: Iterable[Empty[C]#GCE]): Option[Empty[C]] = None
  }

  def empty[C <: Cell[C]]: Empty[C] = Empty[C]()

  trait NoSelfUpdate[C <: Cell[C], GC <: GlobalCell[C, GC]] extends GlobalCell[C, GC] { self: GC =>
    override def needsSelfUpdate: Boolean = false
    def selfUpdate: Option[GC] = None
    override val auto: AutoContract[C, GC] = noAutoContract[C, GC]
  }
}

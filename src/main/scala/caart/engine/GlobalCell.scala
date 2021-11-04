package caart.engine

import caart.engine.fields.Pos2D

trait GlobalCell[C <: Cell[C], GC <: GlobalCell[C, GC]] { self: GC =>
  type GCE <: GlobalCell.Event
  protected val auto: GlobalCell.AutoContract[C]
  def selfUpdate: Option[GC]
  def updateFromEvents(events: Iterable[C#GC#GCE]): Option[GC]

  def needsSelfUpdate: Boolean = true

  final def next(events: Iterable[C#GC#GCE]): GC =
    (events.nonEmpty, needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => selfUpdate.getOrElse(self)
      case (true,  false) => updateFromEvents(events).getOrElse(self)
      case (true,  true)  => updateFromEvents(events).orElse(selfUpdate).getOrElse(self)
    }
}

object GlobalCell {
  trait Event

  trait AutoContract[C <: Cell[C]] {
    def board: Board[C]
    def addEvent(pos: Pos2D, event: C#CE): Unit
  }

  private def noAutoContract[C <: Cell[C]] = new AutoContract[C] {
    override def board: Board[C] = Board.empty[C]
    override def addEvent(pos: Pos2D, event: C#CE): Unit = ()
  }

  final case class Empty[C <: Cell[C]] private() extends GlobalCell[C, Empty[C]] {
    override type GCE = GlobalCell.Event
    override def selfUpdate: Option[Empty[C]] = None
    override def needsSelfUpdate: Boolean = false
    override val auto: AutoContract[C] = noAutoContract[C]
    override def updateFromEvents(events: Iterable[C#GC#GCE]): Option[Empty[C]] = None
  }

  private val _empty: Empty[_] = Empty()
  def empty[C <: Cell[C]]: Empty[C] = _empty.asInstanceOf[Empty[C]]

  trait NoSelfUpdate[C <: Cell[C], GC <: GlobalCell[C, GC]] extends GlobalCell[C, GC] { self: GC =>
    override def needsSelfUpdate: Boolean = false
    def selfUpdate: Option[GC] = None
    override val auto: AutoContract[C] = noAutoContract[C]
  }
}

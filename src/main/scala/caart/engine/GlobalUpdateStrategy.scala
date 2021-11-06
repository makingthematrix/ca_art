package caart.engine

object GlobalUpdateStrategy {
  type Type[C <: Cell[C], GC <: GlobalCell[C, GC]] = (GC, Iterable[GC#GCE]) => GC

  def eventsOverrideSelf[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.updateFromEvents(events).orElse(self.selfUpdate).getOrElse(self)
    }

  def firstEventsThenSelf[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.updateFromEvents(events).map(c => c.selfUpdate.getOrElse(c)).getOrElse(self.selfUpdate.getOrElse(self))
    }

  def firstSelfThenEvents[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.selfUpdate.map(c => c.updateFromEvents(events).getOrElse(c)).getOrElse(self.updateFromEvents(events).getOrElse(self))
    }

  def onlyEvents[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (events.nonEmpty)
      self.updateFromEvents(events).getOrElse(self)
    else
      self


  def onlySelf[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (self.needsSelfUpdate)
      self.selfUpdate.getOrElse(self)
    else
      self

  def noNothing[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC = self
}

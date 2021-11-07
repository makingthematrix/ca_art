package caart.engine

object GlobalUpdateStrategy {
  type Type[C <: Cell[C], GC <: GlobalCell[C, GC]] = (GC, Iterable[GC#GCE]) => GC

  @inline private def otherwise[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (self.needsSelfUpdate) self.selfUpdate.getOrElse(self)
    else if (events.nonEmpty) self.updateFromEvents(events).getOrElse(self)
    else self

  def eventsOverrideSelf[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.updateFromEvents(events).orElse(self.selfUpdate).getOrElse(self)
    else
      otherwise[C, GC](self, events)

  def firstEventsThenSelf[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.updateFromEvents(events).map(c => c.selfUpdate.getOrElse(c)).getOrElse(self.selfUpdate.getOrElse(self))
    else
      otherwise[C, GC](self, events)

  def firstSelfThenEvents[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.selfUpdate.map(c => c.updateFromEvents(events).getOrElse(c)).getOrElse(self.updateFromEvents(events).getOrElse(self))
    else
      otherwise[C, GC](self, events)

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

  def noUpdate[C <: Cell[C], GC <: GlobalCell[C, GC]](self: GC, events: Iterable[GC#GCE]): GC = self
}

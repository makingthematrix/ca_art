package caart.engine

object UpdateStrategy {
  type Type[C <: Cell[C]] = (C, Iterable[C#CE]) => C

  @inline private def otherwise[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (self.needsSelfUpdate) self.selfUpdate.getOrElse(self)
    else if (events.nonEmpty) self.updateFromEvents(events).getOrElse(self)
    else self

  def eventsOverrideSelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.updateFromEvents(events).orElse(self.selfUpdate).getOrElse(self)
    else
      otherwise[C](self, events)

  def firstEventsThenSelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.updateFromEvents(events).map(c => c.selfUpdate.getOrElse(c)).getOrElse(self.selfUpdate.getOrElse(self))
    else
      otherwise[C](self, events)

  def firstSelfThenEvents[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (self.needsSelfUpdate && events.nonEmpty)
      self.selfUpdate.map(c => c.updateFromEvents(events).getOrElse(c)).getOrElse(self.updateFromEvents(events).getOrElse(self))
    else
      otherwise[C](self, events)

  def onlyEvents[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (events.nonEmpty)
      self.updateFromEvents(events).getOrElse(self)
    else
      self

  def onlySelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (self.needsSelfUpdate)
      self.selfUpdate.getOrElse(self)
    else
      self

  def noUpdate[C <: Cell[C]](self: C, events: Iterable[C#CE]): C = self
}

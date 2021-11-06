package caart.engine

object UpdateStrategy {
  type Type[C <: Cell[C]] = (C, Iterable[C#CE]) => C

  def eventsOverrideSelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.updateFromEvents(events).orElse(self.selfUpdate).getOrElse(self)
    }

  def firstEventsThenSelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.updateFromEvents(events).map(c => c.selfUpdate.getOrElse(c)).getOrElse(self.selfUpdate.getOrElse(self))
    }

  def firstSelfThenEvents[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    (events.nonEmpty, self.needsSelfUpdate) match {
      case (false, false) => self
      case (false, true)  => self.selfUpdate.getOrElse(self)
      case (true,  false) => self.updateFromEvents(events).getOrElse(self)
      case (true,  true)  => self.selfUpdate.map(c => c.updateFromEvents(events).getOrElse(c)).getOrElse(self.updateFromEvents(events).getOrElse(self))
    }

  def onlyEvents[C <: Cell[C]](self: C, events: Iterable[C#CE]): C =
    if (events.nonEmpty)
      self.updateFromEvents(events).getOrElse(self)
    else
      self

  def onlySelf[C <: Cell[C]](self: C, events: Iterable[C#CE]): C = {
    if (self.needsSelfUpdate)
      self.selfUpdate.getOrElse(self)
    else
      self
  }
}

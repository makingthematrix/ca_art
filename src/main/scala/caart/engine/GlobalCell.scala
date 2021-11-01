package caart.engine

trait GlobalCell[GC <: GlobalCell[GC]] { self: GC =>
  def update: Option[GC]
  def needsUpdate: Boolean = true
  final def next: GC = if (needsUpdate) update.getOrElse(self) else self
}

object GlobalCell {
  final case class EmptyGlobalCell private() extends GlobalCell[EmptyGlobalCell] {
    override def update: Option[EmptyGlobalCell] = None
    override def needsUpdate: Boolean = false
  }

  val Empty: EmptyGlobalCell = EmptyGlobalCell()
}

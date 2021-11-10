package caart.engine

import caart.engine.fields.Pos2D
import scala.util.chaining.scalaUtilChainingOps

final class EventHub[C <: Cell[C], GC <: GlobalCell[C, GC]] extends EventHub.AddEvents[C, GC] {
  private var _cellEvents = List.empty[(Pos2D, C#CE)]
  private var _globalEvents = List.empty[GC#GCE]

  def !(event: (Pos2D, C#CE)): Unit =
    _cellEvents ::= event

  def !(event: GC#GCE): Unit =
    _globalEvents ::= event

  @inline def cellEvents: Map[Pos2D, Iterable[C#CE]] =
    if (_cellEvents.isEmpty)
      Map.empty
    else
      _cellEvents.groupBy(_._1).map { case (pos, events) => (pos, events.map(_._2)) }

  @inline def globalEvents: List[GC#GCE] = _globalEvents

  @inline def oneCellEvents(pos: Pos2D): List[C#CE] = _cellEvents.collect { case (p, event) if pos == p => event }

  @inline def drainCellEvents(): Map[Pos2D, Iterable[C#CE]] = cellEvents.tap { _ => _cellEvents = Nil }

  @inline def drainGlobalEvents(): List[GC#GCE] = _globalEvents.tap { _ => _globalEvents = Nil }
}

object EventHub {
  trait AddEvents[C <: Cell[C], GC <: GlobalCell[C, GC]] {
    def !(event: (Pos2D, C#CE)): Unit
    def !(event: GC#GCE): Unit
  }
}
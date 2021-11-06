package caart.examples

import caart.engine.{Automaton, Cell, GlobalCell}
import caart.engine.fields.{Dir2D, Pos2D, Right}

final case class Snake(override val pos: Pos2D,
                       override val auto: Cell.AutoContract[Snake, SnakeGlobal],
                       cellType: Snake.CellType = Snake.Empty)
  extends Cell[Snake] {
  import Snake._
  override type GC = SnakeGlobal
  override type CE = Cell.Event

  override def selfUpdate: Option[Snake] = cellType match {
    case Empty => None
    case Treat => None
    case Head(dir) => None
    case Body(headDir, tailDir) => None
    case Tail(dir) => None
  }

  override def updateFromEvents(events: Iterable[Cell.Event]): Option[Snake] = None
}

final case class SnakeGlobal(headDir: Dir2D = Right, snakeSize: Int = 3, treatEaten: Boolean = false)
  extends GlobalCell.NoSelfUpdate[Snake, SnakeGlobal] {
  override type GCE = GlobalCell.Event
  override def updateFromEvents(events: Iterable[GlobalCell.Event]): Option[SnakeGlobal] = None
}

object Snake extends Automaton.Creator[Snake, SnakeGlobal] {
  override def cell(pos: Pos2D, auto: Cell.AutoContract[Snake, SnakeGlobal]): Snake = Snake(pos, auto)
  override def globalCell(auto: GlobalCell.AutoContract[Snake, SnakeGlobal]): SnakeGlobal = SnakeGlobal()

  sealed trait CellType

  case object Empty extends CellType
  case object Treat extends CellType
  final case class Head(dir2D: Dir2D) extends CellType
  final case class Body(headDir: Dir2D, tailDir: Dir2D) extends CellType
  final case class Tail(dir2D: Dir2D) extends CellType
}
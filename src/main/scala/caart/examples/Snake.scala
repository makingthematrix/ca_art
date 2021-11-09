package caart.examples

import caart.engine.{Automaton, Cell, GlobalCell}
import caart.engine.fields.{Dir2D, Pos2D}

import scala.util.Random

final case class Snake(override val pos: Pos2D,
                       override val auto: Cell.AutoContract[Snake, SnakeGlobal],
                       cellType: Snake.CellType = Snake.Empty) extends Cell.NoEvents[Snake] {
  import Snake._
  override type GC = SnakeGlobal

  override def selfUpdate: Option[Snake] = cellType match {
    case Empty                  => ifEmpty
    case Treat                  => ifTreat
    case Head(dir)              => ifHead(dir)
    case Body(headDir, tailDir) => ifBody(headDir, tailDir)
    case Tail(_)                => ifTail
  }

  @inline private def headDir = auto.globalCell.headDir
  @inline private def treatFound = auto.globalCell.treatFound
  @inline private def treatProbability = auto.globalCell.treatProbability

  private def ifEmpty =
    auto.findCell(pos.move(headDir.turnAround)) match {
      case Snake(_, _, Head(_))                        => Some(copy(cellType = Head(headDir)))
      case _ if Random.nextDouble() < treatProbability => Some(copy(cellType = Treat))
      case _ => None
    }

  private def ifTreat =
    auto.findCell(pos.move(headDir.turnAround)) match {
      case Snake(_, _, Head(_)) =>
        auto.eventHub ! TreatFound
        Some(copy(cellType = Head(headDir)))
      case _ =>
        None
    }

  private def ifHead(dir: Dir2D) = {
    auto.findCell(pos.move(headDir)) match {
      case Snake(_, _, Body(_, _))            => auto.eventHub ! GameOver
      case Snake(_, _, Tail(_)) if treatFound => auto.eventHub ! GameOver
      case _ =>
    }
    Some(copy(cellType = Body(headDir, dir.turnAround)))
  }

  private def ifBody(dir: Dir2D, tailDir: Dir2D) =
    auto.findCell(pos.move(tailDir)) match {
      case Snake(_, _, Tail(_)) if !treatFound => Some(copy(cellType = Tail(dir)))
      case _ => None
    }

  private def ifTail =
    if (treatFound) {
      auto.eventHub ! TreatEaten
      None
    } else
      Some(copy(cellType = Empty))
}

final case class SnakeGlobal(headDir:          Dir2D   = Dir2D.Right,
                             score:            Int     = 0,
                             treatFound:       Boolean = false,
                             gameOver:         Boolean = false,
                             treatProbability: Double  = 0.00005) extends GlobalCell.NoSelfUpdate[Snake, SnakeGlobal] {
  import Snake._
  override type GCE = GlobalEvent
  override def updateFromEvents(events: Iterable[GlobalEvent]): Option[SnakeGlobal] = Some {
    events.foldLeft(this) {
      case (cell, GameOver)   => cell.copy(gameOver = true)
      case (cell, TreatFound) => cell.copy(treatFound = true)
      case (cell, TreatEaten) => cell.copy(score = score + 1, treatFound = false)
      case (cell, TurnLeft)   => cell.copy(headDir = headDir.turnLeft)
      case (cell, TurnRight)  => cell.copy(headDir = headDir.turnRight)
    }
  }
}

object Snake extends Automaton.Creator[Snake, SnakeGlobal] {
  override def cell(pos: Pos2D, auto: Cell.AutoContract[Snake, SnakeGlobal]): Snake = Snake(pos, auto)
  override def globalCell(auto: GlobalCell.AutoContract[Snake, SnakeGlobal]): SnakeGlobal = SnakeGlobal()

  sealed trait CellType
  case object Empty                                     extends CellType
  case object Treat                                     extends CellType
  final case class Head(dir2D: Dir2D)                   extends CellType
  final case class Body(headDir: Dir2D, tailDir: Dir2D) extends CellType
  final case class Tail(dir2D: Dir2D)                   extends CellType

  sealed trait GlobalEvent extends GlobalCell.Event
  case object TurnLeft     extends GlobalEvent
  case object TurnRight    extends GlobalEvent
  case object TreatEaten   extends GlobalEvent
  case object TreatFound   extends GlobalEvent
  case object GameOver     extends GlobalEvent
}

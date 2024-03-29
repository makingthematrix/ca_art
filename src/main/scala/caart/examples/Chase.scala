package caart.examples

import caart.engine.{Automaton, Cell, GlobalCell}
import caart.engine.fields.{CMYK, Pos2D}
import com.typesafe.scalalogging.LazyLogging

final case class Chase(override val pos:  Pos2D,
                       override val auto: Cell.AutoContract[Chase, ChaseGlobal],
                       color:             CMYK = CMYK.White,
                       brushes:           List[CMYK] = Nil) extends Cell[Chase] {
  import Chase._
  override type GC = ChaseGlobal
  override type CE = ChaseEvent

  override def selfUpdate: Option[Chase] = (color, dirToPlayerPosition) match {
    case (CMYK.White, None) => None
    case (_, None)          => Some(copy(color = newColor))
    case (_, Some(_))       => Some(copy(color = newColor, brushes = newBrushes))
  }

  override def updateFromEvents(events: Iterable[ChaseEvent]): Option[Chase] =
    events.collectFirst {
      case SetPlayerHere => copy(color = CMYK.Black)
    }.orElse {
      val colors = events.collect { case CreateChaser(color) => color }.toList
      Some(copy(color = CMYK.sum(color :: colors), brushes = colors))
    }

  override def needsSelfUpdate: Boolean =
    (color != CMYK.White) || auto.moore(pos).values.exists(_.brushes.nonEmpty)

  private def newColor =
    if (auto.globalCell.playerPosition.contains(pos)) CMYK.Black
    else if (brushes.nonEmpty) CMYK.sum(color :: brushes)
    else if (color.abs < 0.02) CMYK.White
    else color * 0.98

  private def newBrushes = auto.moore(pos).collect {
    case (thisDir, cell) if cell.brushes.nonEmpty && cell.dirToPlayerPosition.contains(thisDir.turnAround) => cell.brushes
  }.flatten.toList

  private def dirToPlayerPosition = auto.globalCell.playerPosition match {
    case None                      => None
    case Some(cPos) if cPos == pos => None
    case Some(cPos)                => Some((cPos - pos).approx8)
  }
}

final case class ChaseGlobal(playerPosition: Option[Pos2D] = None) extends GlobalCell.NoSelfUpdate[Chase, ChaseGlobal] {
  override type GCE = Chase.SetPlayer
  override def updateFromEvents(events: Iterable[Chase.SetPlayer]): Option[ChaseGlobal] =
    events.headOption.map { case Chase.SetPlayer(pos) => copy(playerPosition = Some(pos)) }
}

object Chase extends Automaton.Creator[Chase, ChaseGlobal] with LazyLogging {
  override def cell(pos: Pos2D, auto: Cell.AutoContract[Chase, ChaseGlobal]): Chase = Chase(pos, auto)
  override def globalCell(auto: GlobalCell.AutoContract[Chase, ChaseGlobal]): ChaseGlobal = ChaseGlobal()

  trait ChaseEvent extends Cell.Event
  final case class CreateChaser(color: CMYK) extends ChaseEvent
  case object SetPlayerHere extends ChaseEvent

  final case class SetPlayer(pos: Pos2D) extends GlobalCell.Event
}

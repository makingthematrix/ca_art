package caart.examples

import caart.engine.{Automaton, AutomatonCell, AutomatonCreator, GlobalCell}
import caart.engine.fields.{CMYK, Pos2D}

final case class Chase(override val pos: Pos2D,
                       override val auto: Automaton[Chase],
                       color:   CMYK = CMYK.White,
                       brushes: List[CMYK] = Nil)
  extends AutomatonCell[Chase] {
  override type GC = Chase.Global

  private[Chase] lazy val dirToPlayerPosition = auto.global.playerPosition match {
    case None                      => None
    case Some(cPos) if cPos == pos => None
    case Some(cPos)                => Some((cPos - pos).approx8)
  }

  override def update: Option[Chase] = (color, dirToPlayerPosition) match {
    case (CMYK.White, None) => None
    case (_, None)          => Some(copy(color = newColor))
    case (_, Some(_))       => Some(copy(color = newColor, brushes = newBrushes))
  }

  override def needsUpdate: Boolean =
    (color != CMYK.White) || auto.moore(pos).values.exists(_.brushes.nonEmpty)

  private def newColor =
    if (brushes.nonEmpty) CMYK.sum(color :: brushes)
    else if (color.abs < 0.02) CMYK.White
    else color * 0.98

  private def newBrushes =
    auto.moore(pos).collect {
      case (thisDir, cell) if cell.brushes.nonEmpty && cell.dirToPlayerPosition.contains(thisDir.turnAround) => cell.brushes
    }.flatten.toList

  override def toString: String = s"Chase($pos, color = $color, direction to the player's position = $dirToPlayerPosition, brushes = $brushes)"
}

object Chase extends AutomatonCreator[Chase] {
  final case class Global(playerPosition: Option[Pos2D] = None) extends GlobalCell[Chase.Global]

  def cell(pos: Pos2D, auto: Automaton[Chase]): Chase = Chase(pos, auto)
  override def globalCell(auto: Automaton[Chase]): Chase.Global = Chase.Global()
}


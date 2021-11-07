package caart.visualisation.examples

import caart.Arguments
import caart.engine.AutomatonNoGlobal
import caart.examples.GameOfLife
import caart.visualisation.{UserEvent, WorldNoGlobal}
import javafx.scene.paint.Color

final class GameOfLifeWorld(override val args: Arguments) extends WorldNoGlobal[GameOfLife] {
  override val auto: AutomatonNoGlobal[GameOfLife] = GameOfLife.automatonNoGlobal(args.dim)

  override protected def toColor(cell: GameOfLife): Color =
    auto.updatedByEvents(cell) match {
      case Some(newCell) if newCell.life => Color.RED
      case None if cell.life => Color.BLACK
      case _ => Color.WHITE
    }

  override protected def processUserEvent(event: UserEvent): Unit =
    auto.addEvent(event.pos, GameOfLife.FlipCell)
}

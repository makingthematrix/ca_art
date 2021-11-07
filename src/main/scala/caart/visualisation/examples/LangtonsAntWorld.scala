package caart.visualisation.examples

import caart.Arguments
import caart.engine.AutomatonNoGlobal
import caart.examples.LangtonsAnt
import caart.visualisation.{UserEvent, WorldNoGlobal}
import javafx.scene.paint.Color

final class LangtonsAntWorld(override val args: Arguments) extends WorldNoGlobal[LangtonsAnt] {
  override val auto: AutomatonNoGlobal[LangtonsAnt] = LangtonsAnt.automatonNoGlobal(args.dim)

  override protected def toColor(cell: LangtonsAnt): Color = {
    val c = auto.updatedByEvents(cell).getOrElse(cell)
    (c.color, c.dir) match {
      case (_, Some(_)) => Color.RED
      case (false, _)   => Color.WHITE
      case (true, _)    => Color.BLACK
    }
  }

  override protected def processUserEvent(event: UserEvent): Unit =
    auto.addEvent(event.pos, LangtonsAnt.CreateAnt)
}

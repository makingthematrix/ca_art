package caart.visualisation.examples

import caart.Arguments
import caart.engine.AutomatonNoGlobal
import caart.engine.fields.{CMYK, Dir2D}
import caart.examples.LangtonsColors
import caart.visualisation.{UserEvent, WorldNoGlobal}
import javafx.scene.paint.Color

import scala.util.Random

final class LangtonsColorsWorld(override val args: Arguments) extends WorldNoGlobal[LangtonsColors] {
  override val auto: AutomatonNoGlobal[LangtonsColors] = LangtonsColors.automatonNoGlobal(args.dim)

  override protected def toColor(cell: LangtonsColors): Color = {
    val c = auto.updatedByEvents(cell).getOrElse(cell)
    if (c.colors.isEmpty) Color.WHITE
    else {
      val rgb = CMYK.sum(c.colors).toRGB
      Color.rgb(rgb.r, rgb.g, rgb.b)
    }
  }

  override protected def processUserEvent(event: UserEvent): Unit = event.pos.foreach { pos =>
    val dir = Dir2D.dirs4(Random.nextInt(Dir2D.dirs4.length))
    val color = CMYK.colors(Random.nextInt(CMYK.colors.length))
    auto.addEvent(pos, LangtonsColors.CreateAnt(color, dir))
  }
}

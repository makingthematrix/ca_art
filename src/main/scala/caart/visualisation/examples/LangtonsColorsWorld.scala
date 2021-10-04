package caart.visualisation.examples

import caart.Arguments
import caart.engine.fields.{CMYK, Dir2D}
import caart.engine.{Automaton, Board}
import caart.examples.LangtonsColors
import caart.visualisation.{UserEvent, World}
import javafx.scene.paint.Color

import scala.util.Random

final class LangtonsColorsWorld(override val args: Arguments) extends World[LangtonsColors] {
  override val auto: Automaton[LangtonsColors] = LangtonsColors.automaton(args.dim)

  override protected def toColor(c: LangtonsColors): Color =
    if (c.colors.isEmpty) Color.WHITE
    else {
      val rgb = CMYK.sum(c.colors).toRGB
      Color.rgb(rgb.r, rgb.g, rgb.b)
    }

  override protected def updateFromEvent(event: UserEvent): Board[LangtonsColors] =
    auto.updateOne(event.pos) { cell =>
      val d = Dir2D.dirs4(Random.nextInt(Dir2D.dirs4.length))
      val c = CMYK.colors(Random.nextInt(CMYK.colors.length))
      cell.copy(colors = Set(c), dirs = Vector((d, c)))
    }
}


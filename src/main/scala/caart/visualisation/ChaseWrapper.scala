package caart.visualisation

import caart.Arguments
import caart.chase.Chase
import caart.engine.Automaton
import caart.fields.RGB
import javafx.scene.paint.Color

import scala.util.Random

final class ChaseWrapper(override val args: Arguments) extends AutoWrapper[Chase] {
  override val auto: Automaton[Chase] = Chase.automaton(args.dim)

  onLeftClick.foreach { pos =>
    updateBoard {
      auto.update { _.copy(center = Some(pos)) }
    }
  }

  onRightClick.foreach { pos =>
    updateBoard {
      val color = RGB.rainbow(Random.nextInt(RGB.rainbow.size)).toCMYK
      auto.updateOne(pos){ _.copy(color = color, brushes = List(color)) }
    }
  }

  override protected def toColor(c: Chase): Color =
    if (c.center.contains(c.pos)) Color.BLACK
    else {
      val rgb = c.color.toRGB
      Color.rgb(rgb.r, rgb.g, rgb.b)
    }
}

package caart

import caart.Arguments.{EmptyExample, Example}

final case class Arguments(dim:     Int = 100,  // the number of cells in one dimension of the automaton
                           it:      Int = 100,  // the number of iterations the automaton will run
                           step:    Int = 1,    // how often the visualisation should be refresh
                           scale:   Int = 8,    // how many pixels per cell in the visualisation
                           delay:   Long = 500L, // interval between two updates in milliseconds
                           example: Example = EmptyExample
                          ) {
  lazy val windowSize: Int = dim * scale
}

object Arguments {
  sealed trait Example { val str: String }
  case object EmptyExample                     extends Example { override val str: String = "0" }
  case object GameOfLifeInteractiveExample     extends Example { override val str: String = "1i" }
  case object LangtonsAntExample               extends Example { override val str: String = "2" }
  case object LangtonsAntInteractiveExample    extends Example { override val str: String = "2i" }
  case object LangtonsColorsExample            extends Example { override val str: String = "3" }
  case object LangtonsColorsInteractiveExample extends Example { override val str: String = "3i" }
  case object ChaseInteractiveExample          extends Example { override val str: String = "4i" }

  val examples = Seq(
    GameOfLifeInteractiveExample, LangtonsAntExample, LangtonsAntInteractiveExample,
    LangtonsColorsExample, LangtonsColorsInteractiveExample, ChaseInteractiveExample
  )

  def parseArguments(args: Seq[String]): Arguments = args.flatMap(_.split('=')).sliding(2, 2).foldLeft(Arguments()){
    case (acc, Seq("dim", value))     => acc.copy(dim   = Integer.parseInt(value))
    case (acc, Seq("it", value))      => acc.copy(it    = Integer.parseInt(value))
    case (acc, Seq("step", value))    => acc.copy(step  = Integer.parseInt(value))
    case (acc, Seq("scale", value))   => acc.copy(scale = Integer.parseInt(value))
    case (acc, Seq("delay", value))   => acc.copy(delay = Integer.parseInt(value).toLong)
    case (acc, Seq("example", value)) =>
      examples.find(_.str == value) match {
        case Some(ex) => acc.copy(example = ex)
        case _        => println(s"unrecognized example: $value"); acc
      }
    case (acc, Seq(name, value)) =>
      println(s"unrecognized parameter: $name with value $value"); acc
    case (acc, _) =>
      println(s"something went wrong"); acc
  }

}

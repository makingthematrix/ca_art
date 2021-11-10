package caart

import caart.Arguments.{GameOfLifeExample, Example}

final case class Arguments(dim:     Int = 100,        // the number of cells in one dimension of the automaton
                           it:      Int = 100,        // the number of iterations the automaton will run
                           step:    Int = 1,          // how often the visualisation should be refresh
                           scale:   Int = 8,          // how many pixels per cell in the visualisation
                           delay:   Long = 0L,        // interval between two updates in milliseconds
                           enforceGC: Boolean = false, // enforce garbage collection every turn
                           example: Example = GameOfLifeExample
                          ) {
  lazy val windowSize: Int = dim * scale
}

object Arguments {
  sealed trait Example { val str: String }
  case object GameOfLifeExample     extends Example { override val str: String = "life" }
  case object LangtonsAntExample    extends Example { override val str: String = "ant" }
  case object LangtonsColorsExample extends Example { override val str: String = "antc" }
  case object ChaseExample          extends Example { override val str: String = "chase" }
  case object SnakeExample          extends Example { override val str: String = "snake" }

  val examples = Seq(GameOfLifeExample, LangtonsAntExample, LangtonsColorsExample, ChaseExample, SnakeExample)

  def parseArguments(args: Seq[String]): Arguments =
    if (args.size == 1)
      args.head.toLowerCase.trim match {
        case "life"  => Arguments(example = Arguments.GameOfLifeExample)
        case "ant"   => Arguments(example = Arguments.LangtonsAntExample)
        case "antc"  => Arguments(example = Arguments.LangtonsColorsExample)
        case "chase" => Arguments(example = Arguments.ChaseExample)
        case "snake" => Arguments(example = Arguments.SnakeExample, dim = 40, delay = 60, scale = 16)
      }
    else
      args.flatMap(_.split('=')).sliding(2, 2).foldLeft(Arguments()){
        case (acc, Seq("dim", value))         => acc.copy(dim   = Integer.parseInt(value))
        case (acc, Seq("it", value))          => acc.copy(it    = Integer.parseInt(value))
        case (acc, Seq("step", value))        => acc.copy(step  = Integer.parseInt(value))
        case (acc, Seq("scale", value))       => acc.copy(scale = Integer.parseInt(value))
        case (acc, Seq("delay", value))       => acc.copy(delay = Integer.parseInt(value).toLong)
        case (acc, Seq("enforcegc", "true"))  => acc.copy(enforceGC = true)
        case (acc, Seq("enforcegc", "false")) => acc.copy(enforceGC = false)
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

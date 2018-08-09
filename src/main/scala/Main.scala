import langtonsant.World

object Main {
  def main(args: Array[String]): Unit = {
    val world = new World(100)
    world.grid(3)

    println("Hello, world!")
  }
}

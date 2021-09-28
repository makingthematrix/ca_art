# CA Art

Hi there. 
This repository consists of a few examples of Cellular Automata, the common part of the coe ("the caart.engine") and a wrapper over a JavaFX and FXGL.

After compilation, you can run an example from the `sbt` shield, typing eg. `run example 1`. There are four of them:
1. Game of Life
2. Langton's Ant
3. Langton's Ants in color
4. Chase

Apart from the `example` keyword you can also use `dim` to change the default length of the board's edge (for the discussion wtf is the board and other concepts, see my talk, or simply read the Scaladoc comments in the code), and `scale` to change the size of the square which symbolizes a single cell on the screen. By default `dim` is set to 100 cells, and `scale` is set to 8 pixels which means that the whole board, when displayed, is 800x800 pixels. Sometimes you may also be interested in the keyword `step` which describes one per how many boards will be displayed on the screen (it speeds up the animation a bit, but also makes it more rough), and for non-interactive examples the keyword `it` defines how many iterations should there be before the animation stops.

There is also a keyword `enforcegc`. The cellular automaton generates a new board every turn and discards the old one. This creates a lot of garbage to collect and if your JDK use a slow, unoptimized GC (like, for example, GraalVM Serial GC) then after a few hundreds turns you will start to experience more and more hiccups and eventually the app will freeze. `enforcegc`, which is set to `true` by default, enforces GC every turn. It results in slower simulations (on my laptop every garbage collections takes around 60-90ms) but it fixes the problem. But maybe in your case it's not necessary - type `run example 1 enforcegc false` and see for yourself.  

If you run the Game of Life example (with `run example 1`) you will see an empty, white window. This is the initial board. You can populate it with the left mouse clicks. You can also use "drag", that is, you can push down the left button, drag the mouse over the screen, and release the button, thus marking the whole area as if you clicked on each of the cells in it. When you're done, start the animation by hitting the space bar. You can always stop the animation again (with the space bar) and edit the board. 

### If you want to write your own cellular automata ...

... watch [the talk](https://www.youtube.com/watch?v=0ABjVP0st08), read the comments in the code, figure out how the Game of Life example works, and start from writing its copy. Apart from the cell's case class you will also need to tell the graphic library how to turn a cell into a colored square on the screen (the `toColor` method), and how to interpret clicks. You can also contact me on Twitter ([@makingthematrix](https://twitter.com/makingthematrix)).

A link to the "slides" - the animation in VideoScribe that was a big part of the talk: [here](https://drive.google.com/file/d/1wsKXR3r-_lGhPVG-KyxA7aJ_OOL5EVy2/view?usp=sharing)

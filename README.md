# CA Art

Hi there. 
This repository consists of a few examples of Cellular Automata, the common part of the code ("the engine") and the graphical user interface made in JavaFX and FXGL.

After compilation, you can run an example from the `sbt` shield. There are five of them:
1. Game of Life ("life")
2. Langton's Ant ("ant")
3. Langton's Ants in color ("antc")
4. Chase ("chase")
5. Snake ("snake")

You can run each one by writing `run <example name>` in the sbt console or if you want more fine control over the example's configuration you can use:
```
>run example=[...] dim=[...] scale=[...] delay=[...] enforcegc=[...]
```
where the arguments are:
 - `example` - the name of the example (no default, choose something)
 - `dim` - the length of one side of the board (default = 100)
 - `scale` - the length of one side of a cell (default = 8)
 - `delay` - the forced delay between turns in milliseconds (default = 0)
There is also `enforcegc`. The cellular automaton generates a new board every turn and discards the old one. This creates a lot of garbage to collect and if your JDK use a slow, unoptimized GC then after a few hundreds turns you will start to experience more and more hiccups. `enforcegc`, if set to `true`, enforces GC every turn. It results in slower simulations but it fixes the problem. Type `run example 1 enforcegc true` and see for yourself.  

If you run the Game of Life example (with `run life`) you will see an empty, white window. This is the initial board. You can populate it with the left mouse clicks. When you're done, start the animation by hitting the space bar. You can always stop the simulation again (with the space bar) and edit the board. 

### If you want to write your own cellular automata ...

... watch [the talk](https://www.youtube.com/watch?v=0ABjVP0st08), read the comments in the code, figure out how the Game of Life example works, and start from writing its copy. Apart from the cell's case class you will also need to tell the graphic library how to turn a cell into a colored square on the screen (the `toColor` method), and how to interpret clicks. You can also contact me on Twitter ([@makingthematrix](https://twitter.com/makingthematrix)).

A link to the "slides" - the animation in VideoScribe that was a big part of the talk: [here](https://drive.google.com/file/d/1wsKXR3r-_lGhPVG-KyxA7aJ_OOL5EVy2/view?usp=sharing)

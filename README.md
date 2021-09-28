# CA Art

Hi there. 
This repository consists of a few examples of Cellular Automata, the common part of the coe ("the caart.engine") and a wrapper over a graphic library [SimGraf](http://hans-hermann-bode.de/en/content/simgraf-simple-scala-graphics-library) by Hans-Hermann Bode. It's actually so simple that I think it could be a good exercise for anyone just starting to learn Scala to pull this repo and write their own cellular automata. (Which, I hope, some of you will do).

After compilation, you can run an example from the `sbt` shield, typing eg. `run example 1i`. There are four of them:
1. Game of Life
2. Langton's Ant
3. Langton's Ants in color
4. Chase

The letter "i" stands for "interactive". In case of both Langton's Ants examples there are also non-interactive versions which can be use for... I don't know, performance testing maybe. Here I will focus on the Game of Life interactive example.

Apart from the `example` keyword you can also use `dim` to change the default length of the board's edge (for the discussion wtf is the board and other concepts, see my talk, or simply read the Scaladoc comments in the code), and `scale` to change the size of the square which symbolizes a single cell on the screen. By default `dim` is set to 100 cells, and `scale` is set to 8 pixels which means that the whole board, when displayed, is 800x800 pixels. Sometimes you may also be interested in the keyword `step` which describes one per how many boards will be displayed on the screen (it speeds up the animation a bit, but also makes it more rough), and for non-interactive examples the keyword `it` defines how many iterations should there be before the animation stops.

If you run the Game of Life example (with `run example 1i`) you will see an empty, white window. This is the initial board. You can populate it with the left mouse clicks. You can also use "drag", that is, you can push down the left button, drag the mouse over the screen, and release the button, thus marking the whole area as if you clicked on each of the cells in it. When you're done, start the animation by hitting the space bar. You can always stop the animation again (with the space bar) and edit the board. Quit the example by hitting "q" (you should also be able to simply close the window, but, uhm, there's a bug).

### If you want to write your own cellular automata ...

... watch [the talk](https://www.youtube.com/watch?v=0ABjVP0st08), read the comments in the code, figure out how the Game of Life example works, and start from writing its copy. Apart from the cell's case class you will also need to tell the graphic library how to turn a cell into a colored square on the screen (the `toColor` method), and how to interpret clicks. You can also contact me on Twitter ([@makingthematrix](https://twitter.com/makingthematrix)) and Wire (@maciek - sorry, no permalinks yet).

A link to the "slides" - the animation in VideoScribe that was a big part of the talk: [here](https://drive.google.com/file/d/1wsKXR3r-_lGhPVG-KyxA7aJ_OOL5EVy2/view?usp=sharing)

### My other work

Apart from fame of being a conference speaker and sheer terror of standing in front of people expecting me to say something, I have one more reason to work on this project. My other idea, [The GAI Library](https://github.com/makingthematrix/gailibrary), is a very early stage of a small AI library. Right now it consists mainly of a lot of design notes and some bad Rust code, because I use it to learn Rust by combat. If I ever manage to get it to a usable shape, I will try to apply it in computer games and maybe (that's a big maybe) also for robots controlled by Raspberry Pi. You may view **CA Art** as a way to reason about **GAI** in a language which is right now much more natural to me than Rust (although Rust is quite similar to Scala). It's something like a sandbox where I can quickly test my ideas before the Rust compile will start yelling at me.

You can also check my older project: [Artificial Neural Networks in Akka](https://github.com/makingthematrix/ann). It's an attempt to write a special kind of a neural network - asynchronous and time-aware - in Akka. It's a bit neglected right now, but for sure I'll get back to it at some point.

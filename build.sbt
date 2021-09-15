name := "ca_art"

version := "0.3"
scalaVersion := "2.13.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding",
  "utf8",
  "-opt:unreachable-code",
  "-opt:simplify-jumps",
  "-opt:compact-locals",
  "-opt:copy-propagation",
  "-opt:redundant-casts",
  "-opt:box-unbox",
  "-opt:nullness-tracking",
  "-opt:closure-invocations",
  "-opt:allow-skip-core-module-init",
  "-opt:assume-modules-non-null",
  "-opt:allow-skip-class-loading",
  "-opt:inline"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
  //Test dependencies
  "org.scalameta" %% "munit" % "0.7.26" % "test"
)

testFrameworks += new TestFramework("munit.Framework")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases"

Test / parallelExecution := true

developers := List(
  Developer("makingthematrix", "Maciej Gorywoda", "maciej.gorywoda@wire.com", url("https://github.com/makingthematrix"))
)

licenses := Seq("GPL 3.0" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html"))

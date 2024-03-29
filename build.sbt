name := "ca_art"

version := "0.3"
scalaVersion := "2.13.7"

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
  "com.wire" %% "wire-signals" % "1.0.0",
  "ch.qos.logback" % "logback-classic" % "1.2.6",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "com.github.almasb" % "fxgl"% "11.17",
  //Test dependencies
  "org.scalameta" %% "munit" % "0.7.26" % "test"
)

lazy val nativeImage =
  project
    .in(file("."))
    .enablePlugins(NativeImagePlugin)
    .settings(
      Compile / mainClass := Some("caart.Main"),
      nativeImageInstalled := true
    )

testFrameworks += new TestFramework("munit.Framework")

Test / parallelExecution := true
fork := true
Test / fork := true

developers := List(
  Developer("makingthematrix", "Maciej Gorywoda", "maciej.gorywoda@wire.com", url("https://github.com/makingthematrix"))
)

licenses := Seq("GPL 3.0" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html"))

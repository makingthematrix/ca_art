name := "ca_art"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "de.h2b.scala.lib" % "simgraf_2.12" % "1.3.0"
libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.8.2"
libraryDependencies += "com.storm-enroute" %% "scalameter-core" % "0.8.2"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false
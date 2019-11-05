name := "monix-grocery-crawler"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "io.monix" %% "monix" % "3.1.0-2156c0e"
libraryDependencies += "com.typesafe.play" %% "play-json-joda" % "2.8.0-M7"
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.0-RC2"
libraryDependencies += "com.typesafe.play" %% "play-ws-standalone-json" % "2.1.0-RC2"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0-RC2"
libraryDependencies += "com.github.inmyth" % "scala-mylogging" % "26b5b2c"
excludeDependencies += "commons-logging" % "commons-logging"

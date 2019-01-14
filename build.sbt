name := "scala-akka"

version := "1.0"

organization := "com.example"

libraryDependencies ++= {
  val akkaVersion = "2.5.4"
  val akkaHttpVersion = "10.0.10"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.6",
    "com.github.nscala-time" %% "nscala-time" % "2.16.0",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "org.specs2" %% "specs2-core" % "4.3.4" % "test"
  )
}

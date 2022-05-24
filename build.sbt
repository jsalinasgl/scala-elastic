
name := "scala-elastic"

version := "1.0"

scalaVersion := "2.13.8"

resolvers += DefaultMavenRepository

lazy val akkaHttpVersion = "10.1.15"
lazy val akkaVersion    = "2.6.19"
lazy val elastic4sVersion = "8.1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http"  % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.sksamuel.elastic4s" % "elastic4s-core_2.13" % elastic4sVersion,
  "com.sksamuel.elastic4s" % "elastic4s-http_2.13" % "6.7.8",
  "com.sksamuel.elastic4s" % "elastic4s-client-esjava_2.13" % elastic4sVersion,
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
)
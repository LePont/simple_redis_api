name := "test_redis"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

val circe_version = "0.8.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % Test,
  "org.scalatest" %% "scalatest" % "latest.integrations" % Test,
  "com.github.etaty" %% "rediscala" % "1.8.0",
  "io.circe" %% "circe-core" % circe_version,
  "io.circe" %% "circe-generic" % circe_version,
  "io.circe" %% "circe-parser" % circe_version
)

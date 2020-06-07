name := "leocode-task"
version := "0.1"
scalaVersion := "2.13.2"

//core
lazy val http4sVersion = "0.21.3"
lazy val catsVersion="2.1.1"
lazy val pureConfigVersion="0.12.3"
lazy val logbackClassicVersion="1.2.3"
lazy val scalaLoggingVersion="3.9.2"
lazy val scalaTestVersion="3.1.1"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
   "ch.qos.logback" % "logback-classic" % logbackClassicVersion,

  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
)

//JSON encoder/decoder
lazy val circeVersion = "0.13.0"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
)

//DB
lazy val doobieVersion = "0.9.0"
libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-h2" % doobieVersion,
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature"
)



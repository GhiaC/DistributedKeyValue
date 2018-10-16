scalaVersion := "2.12.6"

name := "Memcache"
organization := "me.ghiasi"
version := "1.0"

credentials += Credentials(Path.userHome / ".credentials")

libraryDependencies ++= Seq(
  "ai.bale" %% "lati-core" % "0.1.3"
)


import sbt.Keys.libraryDependencies
import scalapb.compiler.Version.grpcJavaVersion

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
libraryDependencies += "io.grpc" % "grpc-alts" % grpcJavaVersion
libraryDependencies += "io.grpc" % "grpc-protobuf" % grpcJavaVersion
libraryDependencies += "io.grpc" % "grpc-stub" % grpcJavaVersion
libraryDependencies += "io.grpc" % "grpc-testing" % grpcJavaVersion % "test"
libraryDependencies += "io.netty" % "netty-tcnative-boringssl-static" % "2.0.7.Final"

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
)

libraryDependencies += "com.typesafe.akka" %% "akka-http2-support" % "10.1.5"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.17",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.87",
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.87",
  "com.typesafe.akka" %% "akka-remote" % "2.5.14"
  //  "org.scalatest" %% "scalatest" % "2.2.4" % Test,

  //  "org.slf4j" % "slf4j-nop" % "1.6.4",
  //  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  //  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  //  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test
  //  "com.typesafe.slick" %% "slick" % "2.1.0",
  //"ch.qos.logback" % "logback-classic" % "1.2.3"
)

val circeVersion = "0.9.3"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

//https://github.com/xuwei-k/grpc-scala-sample/blob/master/build.sbt
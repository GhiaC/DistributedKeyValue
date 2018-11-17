scalaVersion := "2.12.6"

name := "DistributedKeyValue"
organization := "ai.bale"
version := "1.0"

credentials += Credentials(Path.userHome / ".credentials")

import sbt.Keys.libraryDependencies
import scalapb.compiler.Version.grpcJavaVersion

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "ai.bale" %% "lati-core" % "0.1.3",
  "ai.bale" %% "lati-cli" % "0.1.3",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.91",
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.91" % Test
)

//libraryDependencies += "io.netty" % "netty-transport-native-epoll" % "4.1.15.Final"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.17",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.17",
  "com.typesafe.akka" %% "akka-remote" % "2.5.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.17",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.17",
  "com.github.ben-manes.caffeine" % "caffeine" % "2.2.7",
  //  "org.json4s" %% "json4s-native" % "3.6.0",
  "com.okumin" %% "akka-persistence-sql-async" % "0.5.1",
  //  "com.github.mauricio" %% "postgresql-async" % "0.2.+",
)

//libraryDependencies += "be.wegenenverkeer" %% "akka-persistence-pg" % "0.10.0"

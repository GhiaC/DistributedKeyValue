scalaVersion := "2.12.6"

name := "DistributedKeyValue"
organization := "ai.bale"
version := "1.0"

credentials += Credentials(Path.userHome / ".credentials")

import sbt.Keys.libraryDependencies

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "ai.bale" %% "lati-core" % "0.1.3",
  "ai.bale" %% "lati-cli" % "0.1.3",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.91",
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.91" % Test,
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.17",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.17",
  "com.typesafe.akka" %% "akka-remote" % "2.5.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.17",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.17",
  "com.github.ben-manes.caffeine" % "caffeine" % "2.2.7"
)



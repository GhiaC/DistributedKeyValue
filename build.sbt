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

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-alts" % grpcJavaVersion,
  "io.grpc" % "grpc-protobuf" % grpcJavaVersion,
  "io.grpc" % "grpc-stub" % grpcJavaVersion,
  "io.grpc" % "grpc-testing" % grpcJavaVersion % "test",
  "io.netty" % "netty-tcnative-boringssl-static" % "2.0.7.Final",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
)


//libraryDependencies ++= Seq(
//  "org.scalactic" %% "scalactic" % "3.0.5",
////  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
//)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.17",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.87",
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.87",
  "com.typesafe.akka" %% "akka-remote" % "2.5.14",

  "org.json4s" %% "json4s-native" % "3.6.0",
  "com.okumin" %% "akka-persistence-sql-async" % "0.5.1",
  "com.github.mauricio" %% "postgresql-async" % "0.2.+",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.25",
)

libraryDependencies += "be.wegenenverkeer" %% "akka-persistence-pg" % "0.10.0"


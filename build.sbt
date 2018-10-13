scalaVersion := "2.12.6"

name := "Memcache"
organization := "me.ghiasi"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster-sharding" %  "2.5.14",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.14",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.14",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.87"
)
libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
)


//val circeVersion = "0.9.3"
//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)

//libraryDependencies ++= Seq(
//  "com.okumin" %% "akka-persistence-sql-async" % "0.5.1",
//  "com.github.mauricio" %% "mysql-async" % "0.2.21",
//)

//libraryDependencies += Seq(
//  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.87",
//  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.87"
//)
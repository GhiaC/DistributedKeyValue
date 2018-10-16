**Internship Project 2 - Backend**

There is a project definition in the main directory called project2.pdf

# Lati #

**Lati** is a **distributed load test framework** for **scala** programming language. 

## Installing ##

Add the following to your **`build.sbt`** file:

```scala

libraryDependencies += "ai.bale" %% "lati" % "0.1.1-SNAPSHOT"

```

## How to use ##

To use the Lati, you must create a scenario class like the following:

```scala

import ai.bale.lati.scenario.AbstractScenario
import scala.concurrent.{ExecutionContext, Future}

class SimpleScenario extends AbstractScenario {

    /*
      This function is performed by the user before running the main scenario
     */
    override def beforeScenario()(implicit ec: ExecutionContext): Future[Any] = {
      // body of the method
    }
    \\
    /*
      This is your main scenario function
     */
    override def scenario()(implicit ec: ExecutionContext): Future[Any] = {
      // body of the method
    }
    
    /*
      This function is performed by the user after running the main scenario
     */
    override def afterScenario()(implicit ec: ExecutionContext): Future[Any] = {
      // body of the method
    }
}

```

Once you have created your scenario classes, you need to create a **Lati cluster** and a **cli** to do the load test.

Run the cluster like this below:

```scala

import scenarios.SimpleScenario
import ai.bale.lati.starter.LatiCoreStarter

object Core extends App {

  /*
    A list of your scenario classes.
    The scenario number is the index of the scenario class in the list starting from zero.
   */
  val scenarios = List(classOf[SimpleScenario])
  
  LatiCoreStarter.start(scenarios)

}

```

Run the cli like this below:

```scala

import ai.bale.lati.starter.LatiCliStarter

object Cli extends App {

  LatiCliStarter.start()

}

```

## Configuration ##

You must be add the following to your **`application.conf`** in your project:

```

lati {
  cluster {
    remote {
      host = "127.0.0.1" // cluster node host
      port = 3001 // cluster node port
      leader-host = "127.0.0.1" // cluster leader node host
      leader-port = 3001 // cluster leader node port
    }
  }
  cli {
    remote {
      host = "127.0.0.1" // cli host
      port = 4001 // cli port
      // cluster nodes addresses
      nodes = [
        "127.0.0.1:3001"
      ]
    }
  }
  option {
    // numbers of shards in cluster
    shards-number = 10 // 10 shard for all cluster nodes
    // maximum virtual users in total nodes
    max-virtual-users = 10000 // maximum virtual users
    // maximum user per node
    max-users-per-node = 10000 // 10000 users per mode
    // maximum scenario duration time
    max-duration-time = 1800000 // 30 minutes
    // Static timeout for get answer
    static-timeout = 5000 // 5 second
    // Up users rate
    user-up-rate = 1000
    // Before scenario method run rate
    before-scenario-rate = 1000
    // Timeout for run rour beforeScenario function
    before-scenario-timeout = 5000 // 5 second
    // Timeout for run rour afterScenario function
    after-scenario-timeout = 5000 // 5 second
    // Delay for start your scenario after run beforeScenario function
    start-scenario-delay = 10000 // 10 second
  }
}
    
```

## Important ##

* do not use any blocking action in your scenario methods.
* use 10 shards for every cluster nodes.
* use between 0 to 20000 users for every cluster nodes.

## Example ##

For example, please see the **lati-test-simple** module and root directory of this project.

## Authors ##

* **Rashad Ansari**

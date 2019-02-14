package com.example.blog.specs2

import org.specs2.mutable._
import akka.actor._
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import EventFilterExampleSpec._
import akka.event.Logging
import org.specs2.reporter.NoStdOutAroundEach
import org.specs2.specification.AfterAll

class EventFilterExampleSpec extends TestKit(testSystem)
  with SpecificationLike
  with AfterAll
  with NoStdOutAroundEach {

  override def afterAll(): Unit = testSystem.terminate()

  "MyEventActor" >> {
    "logging valid info when msg is 'foo'" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[MyEventActor].withDispatcher(dispatcherId)
      val myEventActor = system.actorOf(props)
      EventFilter.info(message = "MyEvent: foo", occurrences = 1).intercept {
        myEventActor ! "foo"
      }
      ok
    }
    "logging valid info when msg is 'foo' with pattern" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[MyEventActor].withDispatcher(dispatcherId)
      val myEventActor = system.actorOf(props)
      EventFilter.info(source = myEventActor.path.toString, pattern = "f.*").intercept {
        myEventActor ! "foo"
      }
      ok
    }
  }

  // when you want to change log level
  // e.g. changeLevel(Logging.DebugLevel){ /* test code */ }
  def changeLevel[T](level: Logging.LogLevel)(f: => T): T = {
    val originalLevel = system.eventStream.logLevel
    val es = system.eventStream
    try {
      es.setLogLevel(level)
      f
    } finally {
      es.setLogLevel(originalLevel)
    }
  }
}


object EventFilterExampleSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("myEventSystem", config)
  }
}

class MyEventActor extends Actor with ActorLogging {
  def receive = {
    case msg => log.info(s"MyEvent: ${msg}")
  }
}

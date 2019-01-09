package com.example.akkainaction.testdriven

import org.scalatest.{WordSpecLike, MustMatchers}
import akka.testkit.TestKit
import akka.actor._

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {
  // check failed test because SilentActor receive not implemented yet.
  "A Silent Actor" must {
    "change state when it receives a message, single threaded" ignore {
      fail("not implemented yet")
    }
    "change state when it receives a message, multi threaded" ignore {
      fail("not implemented yet")
    }
  }
}

class SilentActor extends Actor {
  def receive = {
    case msg =>
  }
}

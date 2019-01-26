package com.example.blog.akka

import akka.actor._
import akka.testkit._
import org.specs2.mutable._
import org.specs2.specification.{AfterAll, Scope}

class HelloActorSpec extends TestKit(ActorSystem("testHelloActor"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with Tables with AfterAll {

  def afterAll = system.terminate()

  trait TestHelloActor extends Scope {
    val helloActor = system.actorOf(Props[HelloActor], "helloActor")
  }

  "HelloActor" >> {
    "#receive" >> {
      "it returns valid response" in new TestHelloActor {
        "msg" | "expected" |
          "Hello" ! "World" |
          "How are you?" ! "I'm fine thank you!" |
          "foo" ! "unknown message." |> {
          (msg: String, expected: String) => {
            helloActor ! msg
            expectMsgPF() {
              case actual => actual must_== expected
            }
          }
        }
      }
    }
  }
}


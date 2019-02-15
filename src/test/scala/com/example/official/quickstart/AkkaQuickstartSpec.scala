package com.example.official.quickstart

import org.specs2.mutable._
import Greeter._
import Printer._

import scala.concurrent.duration._
import akka.actor._
import akka.testkit._
import org.specs2.specification.AfterAll

class AkkaQuickstartSpec extends TestKit(ActorSystem("testAkkaQuickStart"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with Tables with AfterAll {

  override def afterAll = system.terminate()


  object Fixtures {
    val helloGreetingMessage = "hello"
    val fooGreetingMessage = "foo"
    val greetPerson = "Akka"
  }

  "AkkaQuickStart" >> {
    import Fixtures._
    "pass on a greeting message when instructed to helloGreeter" in {
      val testProbe = TestProbe()
      val helloGreeter = system.actorOf(Greeter.props(helloGreetingMessage, testProbe.ref))
      helloGreeter ! WhoToGreet(greetPerson)
      helloGreeter ! Greet
      testProbe.expectMsg(500 millis, Greeting(helloGreetingMessage + ", " + greetPerson))
      ok
    }

    "pass on a greeting message when instructed to fooGreeter" in {
      val testProbe = TestProbe()
      val fooGreeter = system.actorOf(Greeter.props(fooGreetingMessage, testProbe.ref))
      fooGreeter ! WhoToGreet(greetPerson)
      fooGreeter ! Greet
      testProbe.expectMsg(500 millis, Greeting(fooGreetingMessage + ", " + greetPerson))
      ok
    }
  }

}

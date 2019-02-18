package com.example.official.testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._

class MySpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An Echo actor" must {

    "send back messages 'hello world' unchanged" in {
      val echo = system.actorOf(TestActors.echoActorProps)
      echo ! "hello world"
      expectMsg("hello world")
    }
    "send back messages 'foo' unchanged" in {
      val echo = system.actorOf(TestActors.echoActorProps)
      echo ! "foo"
      expectMsg("foo")
    }
    "using multiple probe actors" in {
      val probe1 = TestProbe()
      val probe2 = TestProbe()
      val actor = system.actorOf(Props[MyDoubleEcho])
      actor ! ((probe1.ref, probe2.ref))
      actor ! "hello"
      probe1.expectMsg(500 millis, "hello")
      probe2.expectMsg(500 millis, "hello")
    }
  }
}

class MyDoubleEcho extends Actor {
  var dest1: ActorRef = _
  var dest2: ActorRef = _

  def receive = {
    case (d1: ActorRef, d2: ActorRef) =>
      dest1 = d1
      dest2 = d2
    case x =>
      dest1 ! x
      dest2 ! x
  }
}

package com.example.official.testing

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout
import scala.util.Success

import scala.concurrent.duration._

class MySpec() extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val timeout = Timeout(3 seconds)

  "An Testing Actor" must {
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
    "actor names in valid when have many test probes" in {
      val worker = TestProbe("worker")
      val aggregator = TestProbe("aggregator")

      worker.ref.path.name should startWith("worker")
      aggregator.ref.path.name should startWith("aggregator")
    }
    "watching other actors from probes" in {
      val probe = TestProbe()
      val target = system.actorOf(TestActors.echoActorProps)
      probe.watch(target)
      target ! PoisonPill
      probe.expectTerminated(target)
    }
    "replying to messages received by probes" in {
      val probe = TestProbe()
      val future = probe.ref ? "hello"
      probe.expectMsg(0 millis, "hello") // TestActor runs on CallingThreadDispatcher
      probe.reply("world")
      assert(future.isCompleted && future.value.contains(Success("world")))
    }
    "forwarding messages received by probes" in {
      val probe = TestProbe()
      val source = system.actorOf(Props(classOf[Source], probe.ref))
      val dest = system.actorOf(Props[Destination])
      source ! "start"
      probe.expectMsg("work")
      probe.forward(dest)
    }
    "testing parent-child relationships" in {
      val parent = TestProbe()
      val child = parent.childActorOf(Props(new Child))
      parent.send(child, "ping")
      parent.expectMsg("pong")
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

class Source(target: ActorRef) extends Actor {
  def receive = {
    case "start" => target ! "work"
  }
}

class Destination extends Actor {
  def receive = {
    case x => // Do something..
  }
}

class Parent extends Actor {
  val child = context.actorOf(Props[Child], "child")
  var ponged = false

  def receive = {
    case "pingit" => child ! "ping"
    case "pong" => ponged = true
  }
}

class Child extends Actor {

  def receive = {
    case "ping" => context.parent ! "pong"
  }
}

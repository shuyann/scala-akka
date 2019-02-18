package com.example.official.actors

import akka.actor._

case class MyValueClass(v: Int) extends AnyVal

class ValueActor(value: MyValueClass) extends Actor {
  def receive = {
    case multiplier: Long => sender ! (value.v) * multiplier
    case _ => sys.error("unsupported case")
  }
}

class DefaultValueActor1(a: Int, b: Int = 5) extends Actor {
  def receive = {
    case x: Int => sender ! ((a + x) * b)
    case _ => sys.error("unsupported case")
  }
}

object DefaultValueActor1 {
  def props(a: Int, b: Int = 5) = Props(new DefaultValueActor1(a, b))
}

class DefaultValueActor2(b: Int = 5) extends Actor {
  def receive = {
    case x: Int => sender ! (x * b)
    case _ => sys.error("unsupported case")
  }
}

object DefaultValueActor2 {
  def props(b: Int = 5) = Props(new DefaultValueActor2(b))
}

class EdgeCases extends App {
  val system = ActorSystem("edgecases")

  val actor1 = system.actorOf(DefaultValueActor1.props(10))
  val actor2 = system.actorOf(DefaultValueActor1.props(3, 3))
  val actor3 = system.actorOf(DefaultValueActor2.props())
  val actor4 = system.actorOf(DefaultValueActor2.props(10))

  actor1 ! 5
  actor2 ! 5
  actor3 ! 5
  actor4 ! 5

  system.terminate()
}

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

class DefaultValueActor2(b: Int = 5) extends Actor {
  def receive = {
    case x: Int => sender ! (x * b)
    case _ => sys.error("unsupported case")
  }
}

class EdgeCases extends App {

}

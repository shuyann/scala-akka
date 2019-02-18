package com.example.official.actors

import akka.actor._

case class Argument(val value: String) extends AnyVal

class ValueClassActor(arg: Argument) extends Actor {

  def receive = {
    case _ => ()
  }

}

object ValueClassActor {
  def props1(arg: Argument) = Props(classOf[ValueClassActor], arg) // fails at runtime

  def props2(arg: Argument) = Props(classOf[ValueClassActor], arg.value) // ok

  def props3(arg: Argument) = Props(new ValueClassActor(arg)) // ok
}

class ValueClassConstructor extends App {
  val system = ActorSystem("testSystem")
  val arg = Argument("foo")
  try {
    val valueClassActor1 = system.actorOf(ValueClassActor.props1(arg))
    valueClassActor1 ! "foo"
  } catch {
    case e: Exception => println(s"runtime error ${e.toString}")
  }
  val valueClassActor2 = system.actorOf(ValueClassActor.props2(arg))
  val valueClassActor3 = system.actorOf(ValueClassActor.props3(arg))
  valueClassActor2 ! "foo"
  valueClassActor3 ! "foo"

  system.terminate()
}

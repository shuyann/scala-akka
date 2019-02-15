package com.example.official.quickstart

import akka.actor._

object Greeter {
  def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))

  case class WhoToGreet(who: String)

  case object Greet

  case object Say

}

class Greeter(message: String, printerActor: ActorRef) extends Actor {

  import Greeter._
  import Printer._

  var greeting = ""

  def receive = {
    case WhoToGreet(who) => greeting = s"${message}, ${who}"
    case Greet => printerActor ! Greeting(greeting)
    case Say => printerActor ! Saying(s"Say, ${greeting}!")
  }

}


object Printer {
  def props: Props = Props[Printer]

  case class Greeting(greeting: String)

  case class Saying(message: String)

}

class Printer extends Actor with ActorLogging {

  import Printer._

  def receive = {
    case Greeting(greeting) => log.info(s"Greeting received (from ${sender()}) ${greeting}")
    case Saying(message) => log.info(s"Saying message (from ${sender()}) ${message}")
  }

}


object AkkaQuickstart extends App {

  import Greeter._

  // create the actor system
  val system = ActorSystem("helloAkka")

  // create the printer actor
  val printer = system.actorOf(Printer.props, "printerActor")

  // create the 'greeter' actors
  val howdyGreeter = system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
  val helloGreeter = system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
  val goodDayGreeter = system.actorOf(Greeter.props("GoodDay", printer), "goodDayGreeter")
  howdyGreeter ! WhoToGreet("Akka")
  howdyGreeter ! Greet

  howdyGreeter ! WhoToGreet("Lightbend")
  howdyGreeter ! Greet

  helloGreeter ! WhoToGreet("Scala")
  helloGreeter ! Greet

  goodDayGreeter ! WhoToGreet("Play")
  goodDayGreeter ! Greet

  goodDayGreeter ! Say

  system.terminate()
}

package com.example.blog.kamon

import akka.actor._
import kamon.Kamon
import kamon.prometheus.PrometheusReporter
import kamon.zipkin.ZipkinReporter

import scala.util.Random

object Main extends App {

  import Greeter._
  // Start Reporting your Data
  Kamon.addReporter(new PrometheusReporter())
  Kamon.addReporter(new ZipkinReporter())

  // create the actor system
  val system = ActorSystem("helloAkka")

  // create the printer actor
  val printer = system.actorOf(Printer.props, "printerActor")

  // create the 'greeter' actors
  val howdyGreeter = system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
  val helloGreeter = system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
  val goodDayGreeter = system.actorOf(Greeter.props("GoodDay", printer), "goodDayGreeter")


  val allGreeters = Vector(howdyGreeter, helloGreeter, goodDayGreeter)

  def randomGreeter = allGreeters(Random.nextInt(allGreeters.length))

  while (true) {
    randomGreeter ! Greet
    Thread.sleep(100)
  }

}

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


package com.example.github

import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

class SupervisorActor extends Actor with ActorLogging {

  val childActor = context.actorOf(Props[WorkerActor], "workerActor")
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

  def receive = {
    case result: Result => childActor.tell(result, sender)
    case msg: Object => childActor ! msg
  }

}

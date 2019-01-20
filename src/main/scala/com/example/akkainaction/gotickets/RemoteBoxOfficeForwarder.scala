package com.example.akkainaction.gotickets


import akka.actor._
import akka.util.Timeout

import scala.concurrent.duration._

object RemoteBoxOfficeForwarder {
  def props(implicit timeout: Timeout) = {
    Props(new RemoteBoxOfficeForwarder)
  }

  def name = "forwarder"
}

class RemoteBoxOfficeForwarder(implicit timeout: Timeout)
  extends Actor with ActorLogging {
  context.setReceiveTimeout(3 seconds)

  // remote deploy and watch boxOffice
  deployAndWatch()

  def deployAndWatch(): Unit = {
    val actor = context.actorOf(BoxOffice.props, BoxOffice.name)
    val selection = context.actorSelection(actor.path)
    // remote actor(boxOffic) lookup
    selection ! Identify(selection.pathString)
  }

  def receive = deploying

  def deploying: Receive = {
    case ActorIdentity(_, Some(actor)) =>
      context.setReceiveTimeout(Duration.Undefined)
      log.info("switching to maybe active state")
      context.become(maybeActive(actor))
      context.watch(actor)

    case ActorIdentity(path, None) =>
      log.error(s"Remote actor with path $path is not available.")

    case ReceiveTimeout =>
      deployAndWatch()

    case msg: Any =>
      log.error(s"Ignoring message $msg, remote actor is not ready yet.")
  }

  def maybeActive(actor: ActorRef): Receive = {
    case Terminated(_) =>
      log.info("Actor $actorRef terminated.")
      log.info("switching to deploying state")
      context.become(deploying)
      context.setReceiveTimeout(3 seconds)
      deployAndWatch()

    case msg: Any => actor forward msg
  }
}

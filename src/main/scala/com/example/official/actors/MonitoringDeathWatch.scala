package com.example.official.actors

import akka.actor._

class WatchActor extends Actor with ActorLogging {
  val child = context.actorOf(Props.empty, "child")
  context.watch(child) // this is the only call needed for registration
  var lastSender = context.system.deadLetters

  override def preStart(): Unit = {
    log.info("start hooks")
    super.preStart()
  }

  override def postStop(): Unit = {
    log.info("stop hooks")
    super.postStop()
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info("restart hooks")
    super.postRestart(reason)
  }


  def receive = {
    case "kill" =>
      context.stop(child)
      lastSender = sender()
    case Terminated(`child`) =>
      lastSender ! "finished"
  }
}

class Follower extends Actor {
  val identifyId = 1
  context.actorSelection("/user/another") ! Identify(identifyId)

  def receive = {
    case ActorIdentity(`identifyId`, Some(ref)) =>
      context.watch(ref)
      context.become(active(ref))
    case ActorIdentity(`identifyId`, None) => context.stop()

  }

  def active(another: ActorRef): Receive = {
    case Terminated(`another`) => context.stop(self)
  }
}

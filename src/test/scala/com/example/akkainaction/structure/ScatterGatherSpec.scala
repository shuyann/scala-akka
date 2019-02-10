package com.example.akkainaction.structure

import java.util.Date
import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.AfterAll

import scala.concurrent.duration._

class ScatterGatherSpec extends TestKit(ActorSystem("testScatterGather"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with AfterAll {

  def afterAll = system.terminate()

  override val timeout = 2 seconds

  object Fixtures {
    val photoDate = new Date()
    val photoSpeed = 60
    val msg = PhotoMessage("id1", ImageProcessing.createPhotoString(photoDate, photoSpeed))
    val combinedMsg = PhotoMessage(msg.id, msg.photo, Some(photoDate), Some(photoSpeed))
  }

  "ScatterGather" >> {
    import Fixtures._
    "when scatter the message and gather them again" in {
      val endProbe = TestProbe()
      val aggregateRef = system.actorOf(
        Props(new Aggregator(2 seconds, endProbe.ref)))
      val speedRef = system.actorOf(
        Props(new GetSpeed(aggregateRef)))
      val timeRef = system.actorOf(
        Props(new GetTime(aggregateRef)))
      val actorRef = system.actorOf(
        Props(new RecipientList(Seq(speedRef, timeRef))))
      actorRef ! msg
      endProbe.expectMsg(combinedMsg)
      ok
    }
  }
}

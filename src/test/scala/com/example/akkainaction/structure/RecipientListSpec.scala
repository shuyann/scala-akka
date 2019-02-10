package com.example.akkainaction.structure

import akka.actor._
import akka.testkit._
import org.specs2.mutable._
import org.specs2.specification.AfterAll

import scala.concurrent.duration._

class RecipientListSpec extends TestKit(ActorSystem("testRecipientList"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with AfterAll {

  def afterAll = system.terminate()

  override val timeout = 2 seconds

  "ScatterGather" >> {
    "with scatter messsage" >> {
      "it returns valid response" in {
        val endProbe1 = TestProbe()
        val endProbe2 = TestProbe()
        val endProbe3 = TestProbe()
        val list = Seq(endProbe1.ref, endProbe2.ref, endProbe3.ref)
        val recipientList = system.actorOf(Props(new RecipientList(list)))
        val msg = "message"
        recipientList ! msg
        endProbe1.expectMsg(msg)
        endProbe2.expectMsg(msg)
        endProbe3.expectMsg(msg)
        ok
      }
    }
  }
}

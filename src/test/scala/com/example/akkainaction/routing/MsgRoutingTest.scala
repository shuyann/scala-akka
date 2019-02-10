package com.example.akkainaction.routing

import scala.concurrent.duration._

import akka.actor._

import org.scalatest._

import akka.testkit.{TestProbe, TestKit}

class MsgRoutingTest
  extends TestKit(ActorSystem("MsgRoutingTest"))
    with WordSpecLike with BeforeAndAfterAll {

  override def afterAll() = {
    system.terminate()
  }

  "The Router" must {
    "routes depending on speed" in {

      val normalFlowProbe = TestProbe()
      val cleanupProbe = TestProbe()
      val router = system.actorOf(Props.empty.withRouter(
        new SpeedRouterPool(50,
          Props(new RedirectActor(normalFlowProbe.ref)),
          Props(new RedirectActor(cleanupProbe.ref)))
      ))

      val msg = new Photo(license = "123xyz", speed = 60)
      router ! msg

      cleanupProbe.expectNoMsg(1 second)
      normalFlowProbe.expectMsg(msg)

      val msg2 = new Photo(license = "123xyz", speed = 45)
      router ! msg2

      cleanupProbe.expectMsg(msg2)
      normalFlowProbe.expectNoMsg(1 second)
    }
  }
}

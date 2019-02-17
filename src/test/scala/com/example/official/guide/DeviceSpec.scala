package com.example.official.guide

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import org.specs2.mutable.{SpecificationLike, Tables}
import org.specs2.specification.AfterAll

class DeviceSpec extends TestKit(ActorSystem("testDevice"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with Tables with AfterAll {


  override def afterAll = system.terminate()

  "Device" >> {
    "reply with latest temperature reading" in {
      val probe = TestProbe()
      val deviceActor = system.actorOf(Device.props("group", "device"))

      deviceActor.tell(Device.RecordTemperature(requestId = 1, 24.0), probe.ref)
      probe.expectMsg(Device.TemperatureRecorded(requestId = 1))

      deviceActor.tell(Device.ReadTemperature(requestId = 2), probe.ref)
      val response1 = probe.expectMsgType[Device.RespondTemperature]
      response1.requestId should ===(2L)
      response1.value should ===(Some(24.0))

      deviceActor.tell(Device.RecordTemperature(requestId = 3, 55.0), probe.ref)
      probe.expectMsg(Device.TemperatureRecorded(requestId = 3))

      deviceActor.tell(Device.ReadTemperature(requestId = 4), probe.ref)
      val response2 = probe.expectMsgType[Device.RespondTemperature]
      response2.requestId should ===(4L)
      response2.value should ===(Some(55.0))
      ok
    }
  }
}

package com.example.akkainaction.structure

import akka.actor._
import akka.testkit._
import org.specs2.mutable._
import org.specs2.specification.AfterAll

import scala.concurrent.duration._


class PipeAndFilterSpec extends TestKit(ActorSystem("testsystem"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with AfterAll {

  def afterAll = system.terminate

  override val timeout = 2 seconds

  "Filters" >> {
    "when filter message in license and speed" >> {
      "it returns valid response" in {
        val endProbe = TestProbe()
        val speedFilterRef = system.actorOf(Props(new SpeedFilter(50, endProbe.ref)))
        val licenseFilterRef = system.actorOf(Props(new LicenseFilter(speedFilterRef)))
        val msg = Photo("123xyz", 60)

        licenseFilterRef ! msg
        endProbe.expectMsg(msg)

        licenseFilterRef ! new Photo("", 60)
        endProbe.expectNoMsg()

        licenseFilterRef ! new Photo("123xyz", 49)
        endProbe.expectNoMsg()
        ok
      }
    }
    "when filter message in speed and license" >> {
      "it returns valid response" in {
        val endProbe = TestProbe()
        val licenseFilterRef = system.actorOf(Props(new LicenseFilter(endProbe.ref)))
        val speedFilterRef = system.actorOf(Props(new SpeedFilter(50, licenseFilterRef)))
        val msg = Photo("123xyz", 60)

        speedFilterRef ! msg
        endProbe.expectMsg(msg)

        speedFilterRef ! new Photo("", 60)
        endProbe.expectNoMsg()

        speedFilterRef ! new Photo("123xyz", 49)
        endProbe.expectNoMsg()
        ok
      }
    }
  }
}

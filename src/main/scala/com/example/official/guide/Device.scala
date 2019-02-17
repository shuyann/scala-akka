package com.example.official.guide

import akka.actor._

object Device {
  def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))

  case class RecordTemperature(requestId: Long, value: Double)

  case class TemperatureRecorded(requestId: Long)

  case class ReadTemperature(requestId: Long)

  case class RespondTemperature(requestId: Long, value: Option[Double])

}

class Device(groupId: String, deviceId: String) extends Actor with ActorLogging {

  import Device._

  var lastTemperatureReading: Option[Double] = None

  override def preStart(): Unit = log.info(s"Device actor ${groupId}-${deviceId} started.")

  override def postStop(): Unit = log.info(s"Device actor ${groupId}-${deviceId} stopped.")

  override def receive: Receive = {
    case RecordTemperature(id, value) =>
      log.info(s"Recorded temperature reading ${value} with ${id}.")
      lastTemperatureReading = Some(value)
      sender ! TemperatureRecorded(id)

    case ReadTemperature(id) =>
      sender ! RespondTemperature(id, lastTemperatureReading)
  }
}


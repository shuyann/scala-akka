package com.example.akkainaction.structure

import java.text.SimpleDateFormat
import java.util.Date
import akka.actor._

case class PhotoMessage(id: String, photo: String,
                        creationTime: Option[Date] = None,
                        speed: Option[Int] = None)

object ImageProcessing {
  val dateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS")


  def makeAttributes(image: String) = image.split('|')

  def getSpeed(image: String): Option[Int] = {
    val attributes = makeAttributes(image)
    if (attributes.size == 3) Some(attributes(1).toInt) else None
  }

  def getTime(image: String): Option[Date] = {
    val attributes = makeAttributes(image)
    if (attributes.size == 3) Some(dateFormat.parse(attributes(0))) else None
  }

  def getLicense(image: String): Option[String] = {
    val attributes = makeAttributes(image)
    if (attributes.size == 3) Some(attributes(2)) else None
  }

  def createPhotoString(date: Date, speed: Int): String = {
    createPhotoString(date, speed, " ")
  }

  def createPhotoString(date: Date, speed: Int, license: String): String = {
    "%s|%s|%s".format(dateFormat.format(date), speed, license)
  }

}

class GetSpeed(pipe: ActorRef) extends Actor {
  def receive = {
    case msg: PhotoMessage => {
      pipe ! msg.copy(speed = ImageProcessing.getSpeed(msg.photo))
    }
  }
}

class GetTime(pipe: ActorRef) extends Actor {
  def receive = {
    case msg: PhotoMessage => {
      pipe ! msg.copy(creationTime = ImageProcessing.getTime(msg.photo))
    }
  }
}

class RecipientList(recipientList: Seq[ActorRef]) extends Actor {
  def receive = {
    case msg: AnyRef => recipientList.foreach(_ ! msg)
  }
}

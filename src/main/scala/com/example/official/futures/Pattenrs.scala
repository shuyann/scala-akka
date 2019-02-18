package com.example.official.futures

import akka.actor._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent._

import akka.pattern._

// within actor
class A extends Actor {

  import context.dispatcher

  val f = Future("hell")

  def receive = {
    case _ =>
  }

}

// use the pipe pattern
class ActorUsingPipeTo(target: ActorRef) extends Actor {
  implicit val ec: ExecutionContext = context.dispatcher
  implicit val timeout: Timeout = 5 seconds

  def receive = {
    case _ =>
      val future = target ? "some message"
      future pipeTo sender // use the pipe pattern
  }
}

object UserProxyActor {

  sealed trait Message

  case object GetUserData extends Message

  case object GetUserActivities extends Message

}

case class UserData(data: String)

case class UserActivity(activity: String)

class UserDataActor extends Actor with ActorLogging {

  import UserDataActor._

  // holds the user data internally
  val internalData: UserData = UserData("initial data")

  def receive = {
    case Get => sender ! internalData
    case _ => sys.error("unknown message")
  }
}

object UserDataActor {

  case object Get

}


trait UserActivityRepository {
  def queryHistoricalActivities(userId: String): Future[List[UserActivity]]
}


class UserActivityActor(val userId: String, repository: UserActivityRepository) extends Actor {

  import UserActivityActor._

  implicit val ec: ExecutionContext = context.dispatcher

  def receive = {
    case Get => repository.queryHistoricalActivities(userId) pipeTo sender()
  }
}

object UserActivityActor {

  case object Get

}

class UserProxyActor(userData: ActorRef, userActivities: ActorRef) extends Actor {

  import UserProxyActor._

  implicit val ec: ExecutionContext = context.dispatcher

  implicit val timeout = Timeout(5 seconds)

  def receive = {
    // send back the result to the sender() we used the pipeTo
    case GetUserData =>
      (userData ? UserDataActor.Get) pipeTo sender()
    case GetUserActivities =>
      (userActivities ? UserActivityActor.Get) pipeTo sender()
  }
}


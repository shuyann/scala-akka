package com.example.blog.akka_stream


object Main extends App {

  import scala.concurrent._
  import akka.actor._
  import akka.stream._
  import akka.stream.scaladsl._

  implicit val system = ActorSystem("streamTest")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  // Source
  val s1 = Source.empty
  val s2 = Source.single("single element")
  val s3 = Source(1 to 3)
  val s4 = Source.repeat(5)
  s3 runForeach println
  s4 take (3) runForeach println

  def run(actor: ActorRef) = {
    Future {
      Thread.sleep(300)
      actor ! 1
    }
    Future {
      Thread.sleep(200)
      actor ! 2
    }
    Future {
      Thread.sleep(100)
      actor ! 3
    }
  }

  val s5 = Source
    .actorRef[Int](bufferSize = 0, OverflowStrategy.fail)
    .mapMaterializedValue(run)
  s5 runForeach println

  // Sink
  val sink = Sink.foreach[Int](v => println(s"Sink received: ${v}"))
  val flow = s3 to sink
  flow.run()

  val actor = system.actorOf(Props(
    new Actor {
      def receive = {
        case msg => println(s"Actor received: ${msg}")
      }
    }
  ))

  val actorSink = Sink.actorRef[Int](actor, onCompleteMessage = "stream completed")
  val actorFlow = s3 to actorSink
  actorFlow.run()

  // Flow
  val invert = Flow[Int].map(_ * -1)
  val doubler = Flow[Int].map(_ * 2)
  val runnable = s3 via invert via doubler to sink
  runnable.run()

  val r1 = Source(1 to 3) via invert to sink
  val r2 = Source(-3 to -1) via invert to sink
  r1.run()
  r2.run()

  Source(1 to 3).map(_ * 2).runWith(sink) // 2,4,6
}

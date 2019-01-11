package com.example.akkainaction.faulttolerance

import akka.actor._
import java.io.File
import akka.actor.SupervisorStrategy.{Stop, Resume, Restart}
import akka.actor.OneForOneStrategy
import scala.concurrent.duration._
import language.postfixOps

package dbstrategy3 {

  object LogProcessingApp extends App {
    val sources = Vector("file://source1/", "file://source2/")
    val system = ActorSystem("logprocessing")
    val databaseUrl = "http://mydatabase"

    val writerProps = Props(new DbWriter(databaseUrl))
    val dbSuperProps = Props(new DbSupervisor(writerProps))
    val logProcSuperProps = Props(
      new LogProcSupervisor(dbSuperProps))
    val topLevelProps = Props(new FileWatcherSupervisor(
      sources,
      logProcSuperProps))
    system.actorOf(topLevelProps)
  }

  class FileWatcherSupervisor(sources: Vector[String],
                              logProcSuperProps: Props) extends Actor {
    var fileWatchers: Vector[ActorRef] = sources.map { source =>
      val logProcSupervisor = context.actorOf(logProcSuperProps)
      val fileWatcher = context.actorOf(
        Props(new FileWatcher(source, logProcSupervisor))
      )
      context.watch(fileWatcher)
      fileWatcher
    }

    override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
      case _: DiskError => Stop
    }

    def receive = {
      case Terminated(fileWatcher) =>
        fileWatchers = fileWatchers.filterNot(_ == fileWatcher)
        if (fileWatchers.isEmpty) self ! PoisonPill
    }
  }

  class FileWatcher(sourceUri: String,
                    logProcSupervisor: ActorRef) extends Actor with FileWatchingAbilities {
    register(sourceUri)

    import FileWatcherProtocol._
    import LogProcessingProtocol._

    def receive = {
      case NewFile(file, _) => logProcSupervisor ! LogFile(file)
      case SourceAbandoned(uri) if uri == sourceUri => self ! PoisonPill
    }
  }

  class LogProcSupervisor(dbSupervisorProps: Props) extends Actor {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: CorruptedFileException => Resume
    }

    val dbSupervisor = context.actorOf(dbSupervisorProps)
    val logProcProps = Props(new LogProcessor(dbSupervisor))
    val logProcessor = context.actorOf(logProcProps)

    def receive = {
      case m => logProcessor.forward(m)
    }
  }

  class LogProcessor(dbSupervisor: ActorRef) extends Actor with LogParsing {

    import LogProcessingProtocol._

    def receive = {
      case LogFile(file) =>
        val lines = parse(file)
        lines.foreach(dbSupervisor ! _)
    }
  }

  class DbImpatientSupervisor(writeProps: Props) extends Actor {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(
      maxNrOfRetries = 5,
      withinTimeRange = 60 seconds
    ) {
      case _: DbBrokenConnectionException => Restart
    }

    val writer = context.actorOf(writeProps)

    def receive = {
      case m => writer.forward(m)
    }
  }

  class DbSupervisor(writeProps: Props) extends Actor {
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: DbBrokenConnectionException => Restart
    }

    val writer = context.actorOf(writeProps)

    def receive = {
      case m => writer.forward(m)
    }
  }

  class DbWriter(databaseUri: String) extends Actor {
    val connection = new DbCon(databaseUri)

    import LogProcessingProtocol._

    def receive = {
      case Line(time, message, messageType) =>
        connection.write(Map(
          'time -> time,
          'message -> message,
          'messageType -> messageType
        ))
    }
  }

  class DbCon(uri: String) {
    def write(map: Map[Symbol, Any]): Unit = ???

    def close(): Unit = ???
  }

  @SerialVersionUID(1L)
  class DiskError(msg: String) extends Error(msg) with Serializable

  @SerialVersionUID(1L)
  class CorruptedFileException(msg: String, val file: File) extends Exception(msg) with Serializable

  @SerialVersionUID(1L)
  class DbBrokenConnectionException(msg: String) extends Exception(msg) with Serializable

  trait LogParsing {

    import LogProcessingProtocol._

    def parse(file: File): Vector[Line] = {
      Vector.empty[Line]
    }
  }

  object FileWatcherProtocol {

    case class NewFile(file: File, timeAdded: Long)

    case class SourceAbandoned(uri: String)

  }

  trait FileWatchingAbilities {
    def register(uri: String): Unit = ???
  }

  object LogProcessingProtocol {

    case class LogFile(file: File)

    case class Line(time: Long, message: String, messageType: String)

  }

}

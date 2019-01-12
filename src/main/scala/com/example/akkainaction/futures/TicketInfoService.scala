package com.example.akkainaction.futures

import scala.concurrent.Future
import com.github.nscala_time.time.Imports._
import scala.util.control.NonFatal
import scala.collection.immutable._

trait TicketInfoService extends WebServiceCalls {

  // defined implicit global execution context
  import scala.concurrent.ExecutionContext.Implicits.global


  // type to minimize copy and paste.
  type Recovery[T] = PartialFunction[Throwable, T]

  // recover with None.
  def withNone[T]: Recovery[Option[T]] = {
    case NonFatal(_) => None
  }

  // recover with empty Seq.
  def withEmptySeq[T]: Recovery[Seq[T]] = {
    case NonFatal(_) => Seq()
  }

  // recover with previous ticket info.
  def withPreviousInfo(previous: TicketInfo): Recovery[TicketInfo] = {
    case NonFatal(_) => previous
  }

  def getTicketInfo(ticketNr: String, location: Location): Future[TicketInfo] = {
    val emptyTicketInfo = TicketInfo(ticketNr = ticketNr, userLocation = location)
    val eventInfo = getEvent(ticketNr, location).recover(withPreviousInfo(emptyTicketInfo))

    eventInfo.flatMap { info =>
      val infoWithWeather = getWeather(info)

      val infoWithTravelAdvice = info.event.map { event =>
        getTravelAdvice(info, event)
      }.getOrElse(eventInfo)

      val suggestedEvents = info.event.map { event =>
        getSuggestions(event)
      }.getOrElse(Future.successful(Seq()))

      val ticketInfos = Seq(infoWithTravelAdvice, infoWithWeather)

      val infoWithTravelAndWeather: Future[TicketInfo] = Future.foldLeft(ticketInfos)(info) { (acc, elem) =>
        val (travelAdvice, weather) = (elem.travelAdvice, elem.weather)

        acc.copy(travelAdvice = travelAdvice.orElse(acc.travelAdvice), weather = weather.orElse(acc.weather))
      }

      for (info <- infoWithTravelAndWeather;
           suggestions <- suggestedEvents
      ) yield info.copy(suggestions = suggestions)
    }
  }

  def getTraffic(ticketInfo: TicketInfo): Future[TicketInfo] = {
    ticketInfo.event.map { event =>
      // call traffic service synchronously
      callTrafficService(ticketInfo.userLocation, event.location, event.time).map { routeResponse =>
        ticketInfo.copy(travelAdvice = Some(TravelAdvice(routeByCar = routeResponse)))
      }
    }.getOrElse(Future.successful(ticketInfo))
  }

  def getCarRoute(ticketInfo: TicketInfo): Future[TicketInfo] = {
    ticketInfo.event.map { event =>
      // call traffic service synchronously
      callTrafficService(ticketInfo.userLocation, event.location, event.time).map { routeResponse =>
        val newTravelAdvice = ticketInfo.travelAdvice.map(_.copy(routeByCar = routeResponse))
        ticketInfo.copy(travelAdvice = newTravelAdvice)
      }.recover(withPreviousInfo(ticketInfo))
    }.getOrElse(Future.successful(ticketInfo))
  }

  def getPublicTransportAdvice(ticketInfo: TicketInfo): Future[TicketInfo] = {
    ticketInfo.event.map { event =>
      // call public transport service synchronously
      callPublicTransportService(ticketInfo.userLocation, event.location, event.time).map { publicTransportAdvice =>
        val newTravelAdvice = ticketInfo.travelAdvice.map(_.copy(publicTransportAdvice = publicTransportAdvice))
        ticketInfo.copy(travelAdvice = newTravelAdvice)
      }.recover(withPreviousInfo(ticketInfo))
    }.getOrElse(Future.successful(ticketInfo))
  }

  def getTravelAdvice(info: TicketInfo, event: Event): Future[TicketInfo] = {
    val futureRoute = callTrafficService(info.userLocation, event.location, event.time).recover(withNone)
    val futurePublicTransport = callPublicTransportService(info.userLocation, event.location, event.time).recover(withNone)

    futureRoute.zip(futurePublicTransport).map { case (routeByCar, publicTransport) =>
      val travelAdvice = TravelAdvice(routeByCar, publicTransport)
      info.copy(travelAdvice = Some(travelAdvice))
    }
  }

  def getWeather(ticketInfo: TicketInfo): Future[TicketInfo] = {
    val futureWeatherX = callWeatherXService(ticketInfo).recover(withNone)
    val futureWeatherY = callWeatherYService(ticketInfo).recover(withNone)
    Future.firstCompletedOf(Seq(futureWeatherX, futureWeatherY)).map { weatherResponse =>
      ticketInfo.copy(weather = weatherResponse)
    }
  }

  def getPlannedEventsWithTraverse(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    Future.traverse(artists) { artist =>
      callArtistCalendarService(artist, event.location)
    }
  }

  def getPlannedEvents(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    val events = artists.map(callArtistCalendarService(_, event.location))
    Future.sequence(events)
  }

  def getSuggestions(event: Event): Future[Seq[Event]] = {
    val futureArtists = callSimilarArtistsService(event).recover(withEmptySeq)
    for (artists <- futureArtists.recover(withEmptySeq);
         events <- getPlannedEvents(event, artists).recover(withEmptySeq)
    ) yield events
  }

  def getSuggestionsWithFlatMapAndMap(event: Event): Future[Seq[Event]] = {
    val futureArtists = callSimilarArtistsService(event).recover(withEmptySeq)
    futureArtists.flatMap { artists =>
      Future.traverse(artists)(callArtistCalendarService(_, event.location))
    }.recover(withEmptySeq)
  }

  def getTravelAdviceUsingForComprehension(info: TicketInfo, event: Event): Future[TicketInfo] = {
    val futureRoute = callTrafficService(info.userLocation, event.location, event.time).recover(withNone)
    val futurePublicTransport = callPublicTransportService(info.userLocation, event.location, event.time).recover(withNone)
    for ((routeByCar, publicTransportService) <- futureRoute.zip(futurePublicTransport);
         travelAdvice = TravelAdvice(routeByCar, publicTransportService)
    ) yield info.copy(travelAdvice = Some(travelAdvice))
  }
}


trait WebServiceCalls {
  def getEvent(ticketNr: String, location: Location): Future[TicketInfo]

  def callWeatherXService(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callWeatherYService(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callTrafficService(origin: Location, destination: Location, time: DateTime): Future[Option[RouteByCar]]

  def callPublicTransportService(origin: Location, destination: Location, time: DateTime): Future[Option[PublicTransportAdvice]]

  def callSimilarArtistsService(event: Event): Future[Seq[Artist]]

  def callArtistCalendarService(artist: Artist, nearLocation: Location): Future[Event]
}

package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, ActorLogging, PoisonPill, Props }
import com.michalplachta.workshop.akka.movies.MovieData.Movie
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class MovieMetadataParser extends Actor with ActorLogging {
  def receive: Receive = {
    case Parse(resourceName) ⇒
      log.debug(s"Starting parsing $resourceName")
      val result = MovieMetadataParsing.parseMoviesFromResource(resourceName)
      log.debug(s"Finished parsing $resourceName")
      sender() ! ParsedMovies(result.toList)
      self ! PoisonPill
  }
}

object MovieMetadataParser {
  case class Parse(resourceName: String)
  case class ParsedMovies(newMovies: List[Movie])

  def props: Props = Props[MovieMetadataParser].withDispatcher("blocking-io-dispatcher")
}

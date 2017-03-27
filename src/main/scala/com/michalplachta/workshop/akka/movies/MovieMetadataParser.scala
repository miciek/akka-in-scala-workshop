package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, Props }
import com.michalplachta.workshop.akka.movies.MovieData.Movie
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class MovieMetadataParser extends Actor {
  def receive: Receive = {
    case Parse(resourceName) ⇒
      val result = MovieMetadataParsing.parseMoviesFromResource(resourceName)
      sender() ! ParsedMovies(result.toList)
  }
}

object MovieMetadataParser {
  case class Parse(resourceName: String)
  case class ParsedMovies(newMovies: List[Movie])

  def props: Props = Props[MovieMetadataParser]
}

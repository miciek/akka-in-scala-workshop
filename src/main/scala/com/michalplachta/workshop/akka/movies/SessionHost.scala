package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.routing.RoundRobinPool
import com.michalplachta.workshop.akka.movies.SessionHost._
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class SessionHost(parseMovieMetadataResource: String ⇒ List[Movie]) extends Actor with ActorLogging {
  var movies: List[Movie] = List.empty

  val parser: ActorRef =
    context.actorOf(RoundRobinPool(2).props(MovieMetadataParser.props(parseMovieMetadataResource)), "parser")

  override def preStart(): Unit = {
    log.debug("Loading movies...")
    parser ! Parse("movie_metadata.csv")
  }

  def receive: Receive = {
    case ParsedMovies(newMovies) ⇒
      movies = newMovies
      log.debug(s"Loaded ${movies.size} movies.")

    case SpawnSession(previousWinner) ⇒
      val movieA: Option[Movie] = previousWinner.orElse(Movie.randomMovie(movies))
      val movieB: Option[Movie] = Movie.randomMovie(movies)
      val response = movieA.flatMap(a ⇒ movieB.map(b ⇒ Session(a, b)))
      sender() ! response.getOrElse(CannotSpawnSession)
      log.debug("Updating movies...")
      parser ! Parse("movie_metadata_big.csv")
  }
}

object SessionHost {
  case class SpawnSession(previousWinner: Option[Movie])
  case class Session(movieA: Movie, movieB: Movie)
  case object CannotSpawnSession

  def props(parseMovieMetadataResource: String ⇒ List[Movie]): Props = Props(classOf[SessionHost], parseMovieMetadataResource)
}

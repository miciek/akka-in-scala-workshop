package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.routing.RoundRobinPool
import com.michalplachta.workshop.akka.movies.GameHost._
import com.michalplachta.workshop.akka.movies.MovieData.Movie
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class GameHost(parseMovieMetadataResource: String ⇒ List[Movie]) extends Actor with ActorLogging {
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

    case SpawnGame(previousWinner) ⇒
      val movieA: Option[Movie] = previousWinner.orElse(MovieData.randomMovie(movies))
      val movieB: Option[Movie] = MovieData.randomMovie(movies)
      val response = movieA.flatMap(a ⇒ movieB.map(b ⇒ Game(a, b)))
      sender() ! response.getOrElse(CannotSpawnGame)
      log.debug("Updating movies...")
      parser ! Parse("movie_metadata_big.csv")
  }
}

object GameHost {
  case class SpawnGame(previousWinner: Option[Movie])
  case class Game(movieA: Movie, movieB: Movie)
  case object CannotSpawnGame

  def props(parseMovieMetadataResource: String ⇒ List[Movie]): Props = Props(classOf[GameHost], parseMovieMetadataResource)
}

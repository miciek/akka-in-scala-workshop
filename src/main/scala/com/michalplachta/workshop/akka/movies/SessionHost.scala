package com.michalplachta.workshop.akka.movies

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{ Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy }
import akka.routing.RoundRobinPool
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }
import com.michalplachta.workshop.akka.movies.SessionHost._

import scala.util.Random

class SessionHost(parseMovieMetadataResource: String ⇒ List[Movie]) extends Actor with ActorLogging {
  var movies: List[Movie] = List.empty
  var updateResourceName: String = "movie_metadata_fail.csv"

  val parser: ActorRef =
    context.actorOf(RoundRobinPool(2).props(MovieMetadataParser.props(parseMovieMetadataResource)), "parser")

  override def preStart(): Unit = {
    log.debug("Loading movies...")
    parser ! Parse("movie_metadata.csv")
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case ex: Exception ⇒
      log.debug(s"Worker failed with exception ${ex.getMessage}")
      updateResourceName = "movie_metadata_big.csv" // simulate "fixing" the issue
      Restart
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
      parser ! Parse(updateResourceName)
  }
}

object SessionHost {
  case class SpawnSession(previousWinner: Option[Movie])
  sealed trait SessionSpawnResult
  case class Session(movieA: Movie, movieB: Movie) extends SessionSpawnResult
  case object CannotSpawnSession extends SessionSpawnResult

  def props(parseMovieMetadataResource: String ⇒ List[Movie]): Props =
    Props(classOf[SessionHost], parseMovieMetadataResource)
}

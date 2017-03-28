package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import com.michalplachta.workshop.akka.movies.GameHost._
import com.michalplachta.workshop.akka.movies.MovieData.Movie
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class GameHost extends Actor with ActorLogging {
  var movies: List[Movie] = List.empty

  var parserId: Int = 0
  def parser: ActorRef = {
    parserId += 1
    context.actorOf(MovieMetadataParser.props, s"parser-$parserId")
  }

  override def preStart(): Unit = {
    log.debug("Loading movies...")
    parser ! Parse("movie_metadata.csv")
  }

  def receive: Receive = {
    case ParsedMovies(newMovies) ⇒
      movies = newMovies
      log.debug(s"Loaded ${movies.size} movies.")

    case SpawnGame(previousWinner) ⇒
      val rand = util.Random
      val movieA = previousWinner.getOrElse(movies(rand.nextInt(movies.size)))
      val movieB = movies(rand.nextInt(movies.size))
      sender() ! Game(movieA, movieB)
      log.debug("Updating movies...")
      parser ! Parse("movie_metadata_big.csv")
  }
}

object GameHost {
  case class SpawnGame(previousWinner: Option[Movie])
  case class Game(movieA: Movie, movieB: Movie)

  def props: Props = Props[GameHost]
}

package com.michalplachta.workshop.akka.movies

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.michalplachta.workshop.akka.movies.GameHost.{ Game, SpawnGame }
import com.michalplachta.workshop.akka.movies.MovieData.Movie

import scala.concurrent.Future
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object HotOrNotAsyncApp extends App {
  val system = ActorSystem("HotOrNot")
  val host = system.actorOf(GameHost.props)

  def play(previousWinner: Option[Movie]): Unit = {
    implicit val timeout: Timeout = 5.seconds
    val futureGame: Future[Game] = (host ? SpawnGame(previousWinner)).mapTo[Game]
    futureGame.onComplete {
      case Success(game) ⇒
        println(s" > ${game.movieA.title} (a) or ${game.movieB.title} (b) or ? (q to quit)")
        val c = io.StdIn.readChar().toLower
        if (c == 'q') System.exit(0)
        val winner = c match {
          case 'a' ⇒ Some(game.movieA)
          case 'b' ⇒ Some(game.movieB)
          case _   ⇒ None
        }
        println(s"You chose ${winner.map(_.title).getOrElse("nothing")}")
        play(winner)

      case Failure(exception) ⇒
        println(s"Couldn't spawn a new game: ${exception.getMessage}. Restarting the game...")
        play(None)
    }
  }

  play(None)
}

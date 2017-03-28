package com.michalplachta.workshop.akka.movies

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.michalplachta.workshop.akka.movies.GameHost.{CannotSpawnGame, Game, SpawnGame}
import com.michalplachta.workshop.akka.movies.MovieData.Movie
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GameHostSpec extends TestKit(ActorSystem("GameHostSpec"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val mockedMovieSet = List(Movie("Matrix", None), Movie("Lion King", None))

  "GameHost" should {
    "spawn a game using data from parsed resource" in {
      val underTest = system.actorOf(GameHost.props(_ => mockedMovieSet))
      def waitForGame: Game = {
        underTest ! SpawnGame(None)
        expectMsgPF() {
          case CannotSpawnGame => waitForGame
          case game: Game => game
        }
      }
      val game = waitForGame
      mockedMovieSet should contain (game.movieA)
      mockedMovieSet should contain (game.movieB)
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}

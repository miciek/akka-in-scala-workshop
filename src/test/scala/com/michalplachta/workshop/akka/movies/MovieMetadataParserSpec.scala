package com.michalplachta.workshop.akka.movies

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{Parse, ParsedMovies}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class MovieMetadataParserSpec extends TestKit(ActorSystem("MovieMetadataParser"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  val matrix = Movie("Matrix", Some(10.0))
  val lionKing = Movie("Lion King", Some(10.0))

  def mockedParseMovieMetadata(resourceName: String): List[Movie] = resourceName match {
    case "justMatrix" => List(matrix)
    case "matrixAndLionKing" => List(matrix, lionKing)
    case _ => List.empty
  }

  "MovieMetadataParser" should {
    "reply with list of one movie parsed from the resource" in {
      val underTest = system.actorOf(MovieMetadataParser.props(mockedParseMovieMetadata))
      underTest ! Parse("justMatrix")
      expectMsg(ParsedMovies(List(matrix)))
    }

    "reply with list of movies parsed from the resource" in {
      val underTest = system.actorOf(MovieMetadataParser.props(mockedParseMovieMetadata))
      underTest ! Parse("matrixAndLionKing")
      expectMsg(ParsedMovies(List(matrix, lionKing)))
    }

    "reply with empty list of movies parsed from a non-existing resource" in {
      val underTest = system.actorOf(MovieMetadataParser.props(mockedParseMovieMetadata))
      underTest ! Parse("nonExistingResource")
      expectMsg(ParsedMovies(List.empty))
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}

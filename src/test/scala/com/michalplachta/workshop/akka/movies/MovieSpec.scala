package com.michalplachta.workshop.akka.movies

import org.scalatest.{ Matchers, WordSpec }

class MovieSpec extends WordSpec with Matchers {
  "randomMovie" should {
    "return a Movie randomly selected from a given list" in {
      val casinoRoyale = Movie("Casino Royale", Some(10.0))
      val spectre = Movie("Spectre", Some(5.0))
      val movies = List(casinoRoyale, spectre)
      val result = Movie.randomMovie(movies)
      result should (be(Some(casinoRoyale)) or be(Some(spectre)))
    }

    "return None if given list is empty" in {
      val movies = List()
      val result = Movie.randomMovie(movies)
      result should be(None)
    }
  }
}

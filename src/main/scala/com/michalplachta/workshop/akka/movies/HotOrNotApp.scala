package com.michalplachta.workshop.akka.movies

import scala.annotation.tailrec

object HotOrNotApp extends App {
  def loadMovies(): List[Movie] = {
    println("Loading movies...")
    val movies = MovieMetadataParsing.parseMoviesFromResource("movie_metadata.csv")
    println(s"Loaded ${movies.size} movies.")
    movies
  }

  def updateMovies(): List[Movie] = {
    println("Updating movies...")
    MovieMetadataParsing.parseMoviesFromResource("movie_metadata_big.csv")
  }

  @tailrec
  def nextSession(movies: List[Movie], previousWinner: Option[Movie]): Unit = {
    val rand = util.Random
    val movieA = previousWinner.getOrElse(movies(rand.nextInt(movies.size)))
    val movieB = movies(rand.nextInt(movies.size))

    println(s" > ${movieA.title} (a) or ${movieB.title} (b) or ? (q to quit)")
    val c = io.StdIn.readChar().toLower
    if (c == 'q') System.exit(0)
    val winner = c match {
      case 'a' ⇒ Some(movieA)
      case 'b' ⇒ Some(movieB)
      case _   ⇒ None
    }
    println(s"You chose ${winner.map(_.title).getOrElse("nothing")}")
    nextSession(updateMovies(), winner)
  }

  nextSession(loadMovies(), None)
}

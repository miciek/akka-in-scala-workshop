package com.michalplachta.workshop.akka.movies

import com.michalplachta.workshop.akka.movies.MovieData.Movie

import scala.annotation.tailrec

object HotOrNotApp extends App {
  def loadMovies(): List[Movie] = {
    println("Loading movies...")
    val movies = MovieMetadataParsing.parseMoviesFromResource("movie_metadata.csv").toList
    println(s"Loaded ${movies.size} movies.")
    movies
  }

  def updateMovies(): List[Movie] = {
    println("Updating movies...")
    MovieMetadataParsing.parseMoviesFromResource("movie_metadata_big.csv").toList
  }

  @tailrec
  def play(movies: List[Movie], previousWinner: Option[Movie]): Unit = {
    val rand = util.Random
    val movie1 = previousWinner.getOrElse(movies(rand.nextInt(movies.size)))
    val movie2 = movies(rand.nextInt(movies.size))

    println(s" > ${movie1.title} (a) or ${movie2.title} (b) or ? (q to quit)")
    val c = io.StdIn.readChar().toLower
    if (c == 'q') System.exit(0)
    val winner = c match {
      case 'a' ⇒ Some(movie1)
      case 'b' ⇒ Some(movie2)
      case _   ⇒ None
    }
    println(s"You chose ${winner.map(_.title).getOrElse("nothing")}")
    play(updateMovies(), winner)
  }

  play(loadMovies(), None)
}

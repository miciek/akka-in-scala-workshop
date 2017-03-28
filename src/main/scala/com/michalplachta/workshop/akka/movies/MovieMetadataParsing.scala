package com.michalplachta.workshop.akka.movies

import com.michalplachta.workshop.akka.movies.MovieData.Movie

import scala.util.Try

object MovieMetadataParsing {
  def parseMoviesFromResource(resourceName: String): Set[Movie] = {
    val bufferedSource = io.Source.fromResource(resourceName)

    val rawMovieLines = bufferedSource.getLines().toList
    bufferedSource.close
    if (rawMovieLines.nonEmpty) {
      val keys = rawMovieLines.head.split(",").map(_.trim).toList
      val movieTitleIndex = keys.indexOf("movie_title")
      val scoreIndex = keys.indexOf("imdb_score")
      rawMovieLines.drop(1).map { line ⇒
        val cols = line.split(",").map(_.trim)
        Movie(cols(movieTitleIndex), Try(cols(scoreIndex).toDouble).toOption)
      }.toSet
    } else Set.empty[Movie]
  }
}

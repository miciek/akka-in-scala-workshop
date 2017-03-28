package com.michalplachta.workshop.akka.movies

import scala.util.Try

object MovieData {
  case class Movie(title: String, score: Option[Double])

  val random = util.Random
  def randomMovie(movies: List[Movie]): Option[Movie] = {
    Try(movies(random.nextInt(movies.size))).toOption
  }
}

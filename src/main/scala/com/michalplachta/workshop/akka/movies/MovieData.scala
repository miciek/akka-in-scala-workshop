package com.michalplachta.workshop.akka.movies

object MovieData {
  case class Movie(title: String, score: Option[Double])
}

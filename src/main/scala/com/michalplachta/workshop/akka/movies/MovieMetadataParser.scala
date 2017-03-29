package com.michalplachta.workshop.akka.movies

import akka.actor.{ Actor, ActorLogging, Props }
import com.michalplachta.workshop.akka.movies.MovieMetadataParser.{ Parse, ParsedMovies }

class MovieMetadataParser(parse: String ⇒ List[Movie]) extends Actor with ActorLogging {
  override def preStart(): Unit = {
    log.debug("Starting parser")
  }

  def receive: Receive = {
    case Parse(resourceName) ⇒
      log.debug(s"Starting parsing $resourceName")
      val result = parse(resourceName)
      log.debug(s"Finished parsing $resourceName")
      sender() ! ParsedMovies(result)
  }
}

object MovieMetadataParser {
  case class Parse(resourceName: String)
  case class ParsedMovies(newMovies: List[Movie])

  def props(parse: String ⇒ List[Movie]): Props =
    Props(classOf[MovieMetadataParser], parse).withDispatcher("blocking-io-dispatcher")
}

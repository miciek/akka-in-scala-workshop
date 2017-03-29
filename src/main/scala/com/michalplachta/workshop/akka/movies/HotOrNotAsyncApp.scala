package com.michalplachta.workshop.akka.movies

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.michalplachta.workshop.akka.movies.SessionHost.{ CannotSpawnSession, Session, SpawnSession }
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object HotOrNotAsyncApp extends App {
  val log = LoggerFactory.getLogger(HotOrNotAsyncApp.getClass)
  val system = ActorSystem("HotOrNot")
  val host = system.actorOf(SessionHost.props(MovieMetadataParsing.parseMoviesFromResource), "sessionHost")

  def nextSession(previousWinner: Option[Movie]): Unit = {
    implicit val timeout: Timeout = 5.seconds
    val futureSession: Future[Any] = host ? SpawnSession(previousWinner)
    futureSession.onComplete {
      case Success(response) ⇒
        response match {
          case CannotSpawnSession ⇒
            Thread.sleep(5000)
            nextSession(previousWinner)
          case session: Session ⇒
            println(s" > ${session.movieA.title} (a) or ${session.movieB.title} (b) or ? (q to quit)")
            val c = io.StdIn.readChar().toLower
            if (c == 'q') System.exit(0)
            val winner = c match {
              case 'a' ⇒ Some(session.movieA)
              case 'b' ⇒ Some(session.movieB)
              case _   ⇒ None
            }
            println(s"You chose ${winner.map(_.title).getOrElse("nothing")}")
            nextSession(winner)
        }

      case Failure(exception) ⇒
        println(s"Couldn't spawn a new session: ${exception.getMessage}. Restarting the session...")
        nextSession(None)
    }
  }

  nextSession(previousWinner = None)
}

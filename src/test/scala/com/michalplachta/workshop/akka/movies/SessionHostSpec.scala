package com.michalplachta.workshop.akka.movies

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.michalplachta.workshop.akka.movies.SessionHost.{CannotSpawnSession, Session, SpawnSession}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SessionHostSpec extends TestKit(ActorSystem("SessionHostSpec"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val mockedMovieSet = List(Movie("Matrix", None), Movie("Lion King", None))

  "SessionHost" should {
    "spawn a session using data from parsed resource" in {
      val underTest = system.actorOf(SessionHost.props(_ => mockedMovieSet))
      def waitForSession: Session = {
        underTest ! SpawnSession(None)
        expectMsgPF() {
          case CannotSpawnSession => waitForSession
          case session: Session => session
        }
      }
      val session = waitForSession
      mockedMovieSet should contain (session.movieA)
      mockedMovieSet should contain (session.movieB)
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}

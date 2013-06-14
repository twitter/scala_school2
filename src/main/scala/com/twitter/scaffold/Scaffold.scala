package com.twitter.scaffold

import spray.routing.SimpleRoutingApp

object Scaffold extends App with SimpleRoutingApp {
  import spray.httpx.TwirlSupport._

  implicit val system = akka.actor.ActorSystem("scaffold")
  val console = new Console

  val route = {
    import spray.http.StatusCodes._
    get {
      path(Slash) {
        complete { html.index() }
      } ~
      getFromResourceDirectory("") ~
      path(Segment) {
        Document.render(_) match {
          case Some(html) => complete { html }
          case None => reject
        }
      }
    } ~
    post {
      entity(as[String]) {
        console.interpret(_) match {
          case Right(result) => complete { result }
          case Left(error)   => respondWithStatus(BadRequest) { complete { error } }
        }
      }
    } ~
    delete {
      complete {
        console.reset()
        NoContent
      }
    }
  }

  startServer(interface = "localhost", port = 8080)(route)
}

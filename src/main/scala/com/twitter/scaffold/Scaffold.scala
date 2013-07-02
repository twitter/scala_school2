package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef }
import spray.http.StatusCodes.{ BadRequest, NoContent }
import spray.routing.HttpService

class Scaffold(val console: ActorRef) extends Actor with HttpService {
  /* HttpService */
  override val actorRefFactory = context

  /* Actor */
  override def receive = runRoute(assetsRoute ~ markdownRoute ~ consoleRoute)

  /* Scaffold */
  private[this] val assetsRoute =
    pathPrefix("assets") {
      getFromResourceDirectory("META-INF/resources/webjars") ~
      getFromResourceDirectory("assets")
    }

  private[this] val markdownRoute = 
    get {
      import spray.httpx.TwirlSupport._
      path(Slash) {
        complete { html.index() }
      } ~
      path(Rest) {
        Document.render(_) match {
          case Some(html) => complete { html }
          case None => reject
        }
      }
    }

  private[this] val consoleRoute =
    post {
      import com.twitter.spray._
      entity(as[String]) {
        Console.Interpret(_) ~> console ~> {
          case Console.Success(message) => complete { message }
          case Console.Failure(message) => respondWithStatus(BadRequest) { complete { message } }
        }
      }
    } ~
    delete {
      complete {
        console ! Console.Reset
        NoContent
      }
    }

}

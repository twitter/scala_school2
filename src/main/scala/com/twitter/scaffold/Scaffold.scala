package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.io.IO
import scala.collection.mutable
import scala.util.Random
import spray.can.Http
import spray.http.StatusCodes.{ BadRequest, NoContent }
import spray.routing.HttpService
import spray.routing.directives.CachingDirectives._

class Scaffold extends Actor with HttpService {
  import Scaffold.ConsoleId

  /* HttpService */
  override val actorRefFactory = context

  /* Actor */
  override def receive = runRoute(assetsRoute ~ markdownRoute ~ consoleRoute)

  /* Cache */
  val requestCache = routeCache()

  /* Scaffold */
  private[this] val assetsRoute =
    pathPrefix("assets") {
      cache(requestCache) {
        getFromResourceDirectory("META-INF/resources/webjars") ~
        getFromResourceDirectory("assets")
      }
    }

  private[this] val markdownRoute = 
    get {
      cache(requestCache) {
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
    }

  private[this] val consoles = mutable.Map.empty[ConsoleId, ActorRef]
  private[this] val random = new Random()

  private[this] val consoleRoute =
    path("console") {
      post {
        complete {
          val id = random.nextInt().abs
          val console = context.actorOf(Console.props, "console-%d".format(id))
          consoles(id) = console
          id.toString
        }
      }
    } ~
    path("console" / LongNumber) { id =>
      post {
        import com.twitter.spray._
        consoles.get(id) match {
          case Some(console) =>
            entity(as[String]) {
              Console.Interpret(_) ~> console ~> {
                case Console.Success(message) => complete { message }
                case Console.Failure(message) => respondWithStatus(BadRequest) { complete { message } }
              }
            }
          case None => reject
        }
      } ~
      delete {
        consoles.get(id) match {
          case Some(console) =>
            complete {
              console ! Console.Reset
              NoContent
            }
          case None => reject
        }
      }
    }
}

object Scaffold extends App {
  implicit val system = akka.actor.ActorSystem("scaffold-system")

  type ConsoleId = Long

  val props = Props[Scaffold]
  val scaffold = system.actorOf(props, "scaffold")

  IO(Http) ! Http.Bind(
    listener  = scaffold,
    interface = "localhost",
    port      = 8080
  )
}

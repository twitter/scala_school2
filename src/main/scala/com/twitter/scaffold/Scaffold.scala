package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.io.IO
import scala.collection.mutable
import scala.util.Random
import spray.can.Http
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes.{ BadRequest, Created, NoContent, NotFound }
import spray.routing.{HttpService, Route}

class Scaffold extends Actor with HttpService {
  import Scaffold.ConsoleId

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

  private[this] val consoles = mutable.Map.empty[ConsoleId, ActorRef]
  private[this] val random = new Random()

  private def withConsole(id: ConsoleId)(f: ActorRef => Route): Route = {
    consoles.get(id) match {
      case Some(console) => f(console)
      case None => complete(NotFound)
    }
  }

  private[this] val consoleRoute =
    path("console") {
      post {
        dynamic {
          val id = random.nextLong().abs
          val console = context.actorOf(Console.props, "console-%d".format(id))
          consoles(id) = console
          val uri = "/console/%d".format(id)
          respondWithSingletonHeader(Location(uri)) { complete(Created) }
        }
      }
    } ~
    path("console" / LongNumber) { id =>
      post {
        import com.twitter.spray._
        withConsole(id) { console =>
          entity(as[String]) {
            Console.Interpret(_) ~> console ~> {
              case Console.Success(message) => complete { message }
              case Console.Failure(message) => respondWithStatus(BadRequest) { complete { message } }
            }
          }
        }
      } ~
      delete {
        withConsole(id) { console =>
          complete {
            console ! Console.Die
            consoles -= id
            NoContent
          }
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

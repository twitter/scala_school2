package com.twitter.scaffold

import annotation.tailrec
import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.event.Logging._
import akka.io.IO
import com.twitter.spray._
import scala.collection.mutable
import scala.util.Random
import spray.can.Http
import spray.http.HttpHeaders.Location
import spray.http.HttpRequest
import spray.http.StatusCodes.{ BadRequest, Created, NoContent, NotFound }
import spray.httpx.SprayJsonSupport._
import spray.httpx.TwirlSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing.{HttpService, Route}
import spray.routing.directives.LogEntry
import spray.util._

class Scaffold extends Actor with HttpService with SprayActorLogging {
  import Scaffold.InterpreterId

  /* HttpService */
  override val actorRefFactory = context

  /* Actor */
  override def receive = runRoute(assetsRoute ~ markdownRoute ~ interpreterRoute)

  /* Scaffold */
  private[this] val assetsRoute =
    logRequest(showRequest _) {
      pathPrefix("assets") {
        getFromResourceDirectory("META-INF/resources/webjars") ~
        getFromResourceDirectory("assets")
      }
    }

  private[this] val markdownRoute =
    get {
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

  private[this] val interpreters = mutable.Map.empty[InterpreterId, ActorRef]

  private def withInterpreter(id: InterpreterId)(f: ActorRef => Route): Route = {
    interpreters.get(id) match {
      case Some(interpreter) => f(interpreter)
      case None => complete(NotFound)
    }
  }

  private[this] val interpreterRoute =
    path("autocomplete" / LongNumber) { id =>
      post {
        withInterpreter(id) { interpreter =>
          entity(as[String]) {
            Interpreter.Complete(_) ~> interpreter ~> {
              case Interpreter.Completions(results) => complete { results }
            }
          }
        }
      }
    } ~
    path("interpreter") {
      post {
        dynamic {
          val id = Random.nextLong().abs
          val interpreter = context.actorOf(Interpreter.props, "interpreter-%d".format(id))
          interpreters(id) = interpreter
          val uri = "/interpreter/%d".format(id)
          respondWithSingletonHeader(Location(uri)) { complete(Created) }
        }
      }
    } ~
    path("interpreter" / LongNumber) { id =>
      post {
        withInterpreter(id) { interpreter =>
          entity(as[String]) {
            Interpreter.Interpret(_) ~> interpreter ~> {
              case Interpreter.Success(message) => complete { message }
              case Interpreter.Failure(message) =>
                respondWithStatus(BadRequest) { complete { message } }
            }
          }
        }
      } ~
      delete {
        withInterpreter(id) { interpreter =>
          complete {
            interpreter ! Interpreter.Die
            interpreters -= id
            NoContent
          }
        }
      }
    }

  // Output logs
  def showRequest(request: HttpRequest) = LogEntry((" content: " + request.entity + " url:" + request.uri).replaceAll("\n",""), InfoLevel)
}

object Scaffold extends App {
  implicit val system = akka.actor.ActorSystem("scaffold-system")

  type InterpreterId = Long

  val props = Props[Scaffold]
  val scaffold = system.actorOf(props, "scaffold")
  val flags = Flags(args)

  IO(Http) ! Http.Bind(
    listener  = scaffold,
    interface = flags.interface,
    port      = flags.port
  )
}

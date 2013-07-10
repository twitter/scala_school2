package com.twitter.scaffold

import akka.actor._
import akka.io.IO
import com.twitter.spray._
import spray.can.Http
import spray.routing.directives.CachingDirectives._
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.httpx.TwirlSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing._

class Scaffold extends Actor with HttpService {

  /* HttpService */
  override val actorRefFactory = context

  /* Actor */
  override def receive = runRoute(assetsRoute ~ markdownRoute ~ interpreterRoute)

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

  private[this] val interpreters = collection.mutable.Map.empty[Long, ActorRef]

  private def withInterpreter(id: Long)(f: ActorRef => Route): Route = {
    interpreters.get(id) match {
      case Some(interpreter) => f(interpreter)
      case None => complete(NotFound)
    }
  }

  private[this] val interpreterRoute =
    path("interpreter") {
      post {
        dynamic {
          val id = util.Random.nextLong().abs
          val interpreter = context.actorOf(Interpreter.props, "interpreter-%d".format(id))
          interpreters(id) = interpreter
          val uri = "/interpreter/%d".format(id)
          respondWithSingletonHeader(Location(uri)) { complete(Created) }
        }
      }
    } ~
    path("interpreter" / LongNumber) { id =>
      post {
        entity(as[String]) { expression =>
          withInterpreter(id) { interpreter =>
            Interpreter.Interpret(expression) ~> interpreter ~> {
              case Interpreter.Success(message) =>
                complete { message }
              case Interpreter.Failure(message) =>
                respondWithStatus(BadRequest) { complete { message } }
            }
          }
        }
      } ~
      delete {
        withInterpreter(id) { interpreter =>
          complete {
            interpreter ! PoisonPill
            interpreters -= id
            NoContent
          }
        }
      }
    } ~
    path("interpreter" / LongNumber / "completions") { id =>
      post {
        entity(as[String]) { expression =>
          withInterpreter(id) { interpreter =>
            Interpreter.Complete(expression) ~> interpreter ~> {
              case Interpreter.Completions(results) => complete { results }
            }
          }
        }
      }
    }

}

object Scaffold extends App {
  implicit val system = akka.actor.ActorSystem("scaffold-system")

  val props = Props[Scaffold]
  val scaffold = system.actorOf(props, "scaffold")
  val flags = Flags(args)

  IO(Http) ! Http.Bind(
    listener  = scaffold,
    interface = flags.interface,
    port      = flags.port
  )
}

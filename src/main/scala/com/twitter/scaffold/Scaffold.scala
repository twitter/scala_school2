package com.twitter.scaffold

import akka.actor._
import akka.event.Logging.InfoLevel
import akka.io.IO
import com.twitter.spray._
import spray.can.Http
import spray.http._
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.httpx.TwirlSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing._
import spray.routing.directives._
import spray.util._

trait ScaffoldService extends HttpService with CachingDirectives {
  private[this] def requestCache = routeCache()
  private[this] val interpreters = collection.mutable.Map.empty[Long, ActorRef]
  private[this] def withInterpreter(id: Long)(f: ActorRef => Route): Route = {
    interpreters.get(id) match {
      case Some(interpreter) => f(interpreter)
      case None => complete(NotFound)
    }
  }

  def assetsRoute =
    pathPrefix("assets") {
      cache(requestCache) {
        getFromResourceDirectory("META-INF/resources/webjars") ~
        getFromResourceDirectory("assets")
      }
    }

  def markdownRoute =
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

  def interpreterRoute =
    path("interpreter") {
      post {
        dynamic {
          val id = util.Random.nextLong().abs
          val interpreter = actorRefFactory.actorOf(Interpreter.props, "interpreter-%d".format(id))
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
            Interpreter.Interpret(expression) -!> interpreter -!> {
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
            Interpreter.Complete(expression) -!> interpreter -!> {
              case Interpreter.Completions(results) => complete { results }
            }
          }
        }
      }
    }

  def route = assetsRoute ~ markdownRoute ~ interpreterRoute
}

class Scaffold extends Actor with ScaffoldService {
  override val actorRefFactory = context
  override def receive = runRoute(logRequestResponse("scaffold", InfoLevel) { route })
}

object Scaffold extends App {
  implicit val system = ActorSystem("scaffold-system")

  val props = Props[Scaffold]
  val scaffold = system.actorOf(props, "scaffold")
  val flags = Flags(args)

  {
    // pre-warm the interpreter
    val warm = system.actorOf(Interpreter.props)
    warm ! Interpreter.Interpret("1 + 1")
    warm ! PoisonPill
  }

  IO(Http) ! new Http.Bind(
    listener  = scaffold,
    endpoint  = new java.net.InetSocketAddress(flags.port),
    backlog   = 100,
    options   = Nil,
    settings  = None
  )
}

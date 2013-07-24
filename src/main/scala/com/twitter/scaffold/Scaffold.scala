package com.twitter.scaffold

import akka.actor._
import akka.event.Logging.InfoLevel
import akka.io.IO
import concurrent.duration._
import com.twitter.spray._
import spray.can.Http
import spray.http._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.httpx.TwirlSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing._
import spray.routing.directives._
import spray.util._

trait ScaffoldService extends HttpService with CachingDirectives {

  def interpreters: ActorRef
  private[this] def requestCache = routeCache()

  import Interpreter._
  import InterpreterSupervisor._

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
        Create -!> interpreters -!> {
          case Created(id) => created(s"/interpreter/$id")
        }
      }
    } ~
    path("interpreter" / Segment) { id =>
      (post & entity(as[String])) { expression =>
        Request(id, Interpret(expression)) -!> interpreters -!> {
          case Success(message) =>
            complete { message }
          case Failure(message) =>
            respondWithStatus(BadRequest) { complete { message } }
          case NoSuchInterpreter =>
            complete { NotFound }
        } 
      } ~
      delete {
        Destroy(id) -!> interpreters -!> {
          case Destroyed =>
            complete { NoContent }
          case NoSuchInterpreter =>
            complete { NotFound }
        }
      }
    } ~
    path("interpreter" / Segment / "completions") { id =>
      (post & entity(as[String])) { expression =>
        Request(id, Complete(expression)) -!> interpreters -!> {
          case Completions(results) =>
            complete { results }
          case NoSuchInterpreter =>
            complete { NotFound }
        }
      }
    }

  def route = assetsRoute ~ markdownRoute ~ interpreterRoute
}

class Scaffold(val interpreters: ActorRef) extends Actor with ScaffoldService {
  override val actorRefFactory = context
  override def receive = runRoute(logRequestResponse("scaffold", InfoLevel) { route })
}

object Scaffold extends App {
  implicit val system = ActorSystem("scaffold-system")

  def props(interpreters: ActorRef) = Props(classOf[Scaffold], interpreters)

  val interpreters = system.actorOf(InterpreterSupervisor.props(10 minutes), "interpreter-supervisor")
  val scaffold = system.actorOf(props(interpreters), "scaffold")
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

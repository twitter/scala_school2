package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.io.IO
import annotation.tailrec
import spray.can.Http
import spray.http.StatusCodes.{ BadRequest, NoContent }
import spray.routing.HttpService

class Scaffold extends Actor with HttpService {

  // TODO #6: there should be many interpreter actors supervised by Scaffold, one per user session
  val interpreter = context.actorOf(Interpreter.props, "interpreter")

  /* HttpService */
  override val actorRefFactory = context

  /* Actor */
  override def receive = runRoute(assetsRoute ~ markdownRoute ~ interpreterRoute)

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

  private[this] val interpreterRoute =
    post {
      import com.twitter.spray._

      path("autocomplete") {
        import spray.json.DefaultJsonProtocol._
        import spray.httpx.SprayJsonSupport._

        entity(as[String]) {
          Interpreter.Complete(_) ~> interpreter ~> {
            case Interpreter.Completions(results) => complete { results }
          }
        }
      } ~
      path(Rest) { rest =>
        entity(as[String]) {
          Interpreter.Interpret(_) ~> interpreter ~> {
            case Interpreter.Success(message) => complete { message }
            case Interpreter.Failure(message) => respondWithStatus(BadRequest) { complete { message } }
          }
        }
      }
    } ~
    delete {
      complete {
        interpreter ! Interpreter.Reset
        NoContent
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

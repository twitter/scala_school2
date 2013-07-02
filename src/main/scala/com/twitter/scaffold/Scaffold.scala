package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef, Props }
import spray.http.StatusCodes.{ BadRequest, NoContent }
import spray.routing.{ Directives, HttpService, RequestContext, Route }

class Scaffold(val console: ActorRef) extends Actor with HttpService {
  import Scaffold._

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
      entity(as[String]) { expression => ctx =>
        // TODO #38: ctx should stay hidden as much as possible...
        val continuation = actorRefFactory.actorOf(Props(classOf[ConsoleContinuation], ctx))
        console ! Console.Interpret(expression, continuation)
      }
    } ~
    delete {
      complete {
        console ! Console.Reset
        NoContent
      }
    }

}

object Scaffold {

  // TODO #38: there must be a better way to structure this...
  private class ConsoleContinuation(ctx: RequestContext) extends Actor {
    import Directives._
    def receive = {
      case Console.Success(message) => continue { complete { message } }
      case Console.Failure(message) => continue { respondWithStatus(BadRequest) { complete { message } } }
    }

    private[this] def continue(route: Route): Unit =
      try route(ctx)
      catch { case util.control.NonFatal(e) ⇒ ctx.failWith(e) }
      finally context.stop(self)
  }

}

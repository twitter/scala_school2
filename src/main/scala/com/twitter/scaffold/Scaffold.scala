package com.twitter.scaffold

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import spray.http.StatusCodes.{ BadRequest, NoContent }
import spray.routing.HttpService

class Scaffold extends Actor with HttpService {

  // TODO #6: there should be many console actors supervised by Scaffold, one per user session
  val console = context.actorOf(Console.props, "console")

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

object Scaffold extends App {
  implicit val system = akka.actor.ActorSystem("scaffold-system")
  type ConfigMap = Map[Symbol, Any]

  val props = Props[Scaffold]
  val scaffold = system.actorOf(props, "scaffold")

  // Parses the command line arguments.
  val configMap = parseConfig(Map(), args.toList)
  val host_name = configMap.getOrElse('h, "localhost").asInstanceOf[String]
  val port_number = configMap.getOrElse('p, 8080).asInstanceOf[Int]

  IO(Http) ! Http.Bind(
    listener  = scaffold,
    interface = host_name,
    port      = port_number
  )

  /**
   * Parses the configuration from a list of input arguments.
   */
  def parseConfig(map: ConfigMap, list: List[String]) : ConfigMap = {
    def isSwitch(s : String) = (s(0) == '-')
    list match {
      case Nil => map
      case ("-h" | "--host") :: value :: tail =>
                             parseConfig(map ++ Map('h -> value), tail)
      case ("-p" | "--port") :: value :: tail =>
                             parseConfig(map ++ Map('p -> value.toInt), tail)
      case option :: tail => println("Unknown command line parameter: " + option) 
                             exit(1) 
    }
  }
}

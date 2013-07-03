package com.twitter.scaffold

import akka.actor.{ Actor, Props }

class Console extends Actor {
  import Console._

  import java.io.ByteArrayOutputStream
  import scala.tools.nsc._
  import interpreter._

  private[this] val console = new IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  })

  def receive = {
    case Interpret(expression) =>
      val out = new ByteArrayOutputStream
      val result = scala.Console.withOut(out) { console.interpret(expression) }
      val response = result match {
        case Results.Success => Success(out.toString)
        case Results.Error | Results.Incomplete => Failure(out.toString)
      }
      sender ! response
    case Reset =>
      console.reset()
  }
}

object Console {

  val props = Props[Console]

  // requests
  case class Interpret(expression: String)
  case object Reset

  // responses
  case class Success(output: String)
  case class Failure(output: String)

}

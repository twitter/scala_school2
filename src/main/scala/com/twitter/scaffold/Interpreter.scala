package com.twitter.scaffold

import akka.actor.{ Actor, Props }

class Interpreter extends Actor {
  import Interpreter._

  import java.io.ByteArrayOutputStream
  import scala.tools.nsc._
  import scala.tools.nsc.interpreter._

  private[this] val interpreter = new IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  })
  // Warms up the interpreter to avoid slow first call.
  self ! Interpret("1 + 1")

  private[this] val completion = new JLineCompletion(interpreter)

  def receive = {
    case Complete(expression) =>
      val result = completion.topLevelFor(Parsed.dotted(expression, expression.length) withVerbosity 4)
      sender ! Completions(result)
    case Interpret(expression) =>
      val out = new ByteArrayOutputStream
      val result = Console.withOut(out) { interpreter.interpret(expression) }
      val response = result match {
        case Results.Success => Success(out.toString)
        case Results.Error | Results.Incomplete => Failure(out.toString)
      }
      sender ! response
    case Reset =>
      interpreter.reset()
  }
}

object Interpreter {

  val props = Props[Interpreter]

  // requests
  case class Interpret(expression: String)
  case class Complete(expression: String)
  case object Reset

  // responses
  case class Success(output: String)
  case class Failure(output: String)
  case class Completions(results: Seq[String])

}
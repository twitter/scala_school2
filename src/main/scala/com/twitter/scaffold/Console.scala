package com.twitter.scaffold

class Console {
  import java.io.ByteArrayOutputStream
  import scala.tools.nsc._
  import interpreter._

  private[this] val console = new IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  })

  def interpret(e: String): Either[String, String] = {
    val out = new ByteArrayOutputStream
    val result = Console.withOut(out) { console.interpret(e) }

    import Results._
    result match {
      case Success => Right(out.toString)
      case Error | Incomplete => Left(out.toString)
    }
  }

  def reset(): Unit = console.reset()

  reset() // warm prior to use
}

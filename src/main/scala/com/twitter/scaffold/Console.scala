package com.twitter.scaffold

class Console {
  import java.io._
  import scala.tools.nsc._
  import interpreter._

  private[this] val writer = new StringWriter
  private[this] val console = new IMain({
    val settings = new Settings
    settings.usejavacp.value = true
    settings
  }, new PrintWriter(writer))

  def interpret(e: String): Either[String, String] = {
    writer.getBuffer.setLength(0)
    val result = console.interpret(e)
    val output = writer.toString

    import Results._
    result match {
      case Success => Right(output)
      case Error | Incomplete => Left(output)
    }
  }

  def reset(): Unit = console.reset()
}

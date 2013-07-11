package com.twitter.scaffold

case class Flags(interface: String = "localhost", port: Int = 8080)

object Flags {
  def apply(args: Seq[String]): Flags = {
    @annotation.tailrec
    def go(flags: Flags, args: Seq[String]): Flags = args match {
      case ("-p" | "--port") +: port +: tail =>
        go(flags.copy(port = port.toInt), tail)
      case unknown +: tail =>
        throw new IllegalArgumentException("Unknown flag: %s".format(unknown))
      case _ =>
        flags
    }
    go(Flags(), args)
  }
}

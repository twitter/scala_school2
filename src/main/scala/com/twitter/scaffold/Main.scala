package com.twitter.scaffold

object Main extends App {
  import akka.actor.{ ActorSystem, Props }
  import akka.io.IO
  import spray.can.Http

  implicit val system = akka.actor.ActorSystem("scaffold")

  val console = system.actorOf(Props(classOf[Console]), "console")
  val scaffold = system.actorOf(Props(classOf[Scaffold], console), "scaffold")

  IO(Http) ! Http.Bind(
    listener = scaffold,
    interface = "localhost",
    port = 8080
  )
}

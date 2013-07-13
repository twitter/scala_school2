package com.twitter.scaffold

import akka.actor._
import collection.mutable.Map
import concurrent.duration.FiniteDuration

class InterpreterSupervisor(idleTimeout: FiniteDuration) extends Actor {
  import InterpreterSupervisor._
  implicit def dispatcher = context.system.dispatcher

  private[this] class SupervisedInterpreter(val interpreter: ActorRef, var supervisor: Cancellable)
  private[this] val interpreters = Map.empty[String, SupervisedInterpreter]

  private[this] def supervisor(id: String): Cancellable =
    context.system.scheduler.scheduleOnce(idleTimeout, self, Destroy(id))

  def receive = {
    case Create =>
      val id = randomId()
      val interpreter = context.actorOf(Interpreter.props, s"interpreter-$id")
      interpreters += id -> new SupervisedInterpreter(interpreter, supervisor(id))
      sender ! Created(id)

    case Destroy(id) =>
      interpreters.remove(id) match {
        case Some(supervisedInterpreter) =>
          supervisedInterpreter.interpreter ! PoisonPill
          supervisedInterpreter.supervisor.cancel()
          sender ! Destroyed
        case None =>
          sender ! NoSuchInterpreter
      }

    case Request(id, request) =>
      interpreters.get(id) match {
        case Some(supervisedInterpreter) =>
          supervisedInterpreter.interpreter.forward(request)
          supervisedInterpreter.supervisor.cancel()
          supervisedInterpreter.supervisor = supervisor(id)
        case None =>
          sender ! NoSuchInterpreter
      }
  }

}

object InterpreterSupervisor {
  def props(idleTimeout: FiniteDuration) = Props(classOf[InterpreterSupervisor], idleTimeout)

  def randomId(): String = util.Random.alphanumeric.take(32).mkString

  // requests
  case object Create
  case class  Destroy(id: String)
  case class  Request(id: String, request: Any)

  // responses
  case class  Created(id: String)
  case object Destroyed
  case object NoSuchInterpreter
}

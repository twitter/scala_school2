package com.twitter.scaffold

import akka.actor._
import akka.testkit._
import org.scalatest._
import org.scalatest.matchers.MustMatchers

class InterpreterSpec
    extends TestKit(ActorSystem("interpreter-spec"))
    with ImplicitSender
    with WordSpec
    with MustMatchers {

  "interpreter" should {
    "evaluate expressions" in {
      val interpreter = system.actorOf(Interpreter.props)
      interpreter ! Interpreter.Interpret("val two = 1 + 1")
      expectMsg(Interpreter.Success("two: Int = 2\n"))
      interpreter ! PoisonPill
    }
  }

}

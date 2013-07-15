package com.twitter.scaffold

import akka.actor.{ ActorRef, ActorSystem, PoisonPill, Props }
import akka.pattern.ask
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit }
import akka.util.Timeout
import concurrent.Await
import concurrent.duration._
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach, WordSpec }
import org.scalatest.matchers.MustMatchers
import util.{Try, Success, Failure}

class InterpreterSpec
    extends WordSpec
    with MustMatchers
    with BeforeAndAfterEach {

  implicit val timeout: Timeout = 10 seconds
  private[this] var testInterpreter:ActorRef = _

  implicit val system = ActorSystem("IntepreterSpec")
  import system._

  def interpretShouldSucceed(request: String, response: String) {
    val responseFuture = testInterpreter ? Interpreter.Interpret(request)
    Await.result(responseFuture, 10 seconds) match {
      case Interpreter.Success(resp) => resp must include (response)
      case _ => assert(false, "Interpreter did not return Success")
    }
  }

  def interpretShouldFail(request: String, response: String) = {
    val responseFuture = testInterpreter ? Interpreter.Interpret(request)
    Await.result(responseFuture, 10 seconds) match {
      case Interpreter.Failure(resp) => resp must include (response)
      case _ => assert(false, "Interpreter did not return Failure")
    }
  }

  override def beforeEach() {
    testInterpreter = TestActorRef( new Interpreter() )
  }

  override def afterEach() {
    testInterpreter ! PoisonPill
  }

  "The Interpreter" should {
    "succeed" when {
      "adding two integers" in {
        interpretShouldSucceed("1 + 1", "Int = 2")
      }

      "add two strings" in {
        interpretShouldSucceed("\"foo\" + \"bar\"", "foobar")
      }

      "retrieving a previously set value" in {
        interpretShouldSucceed("val p = 7", "Int = 7")
        interpretShouldSucceed("p", "Int = 7")
      }

      "reassigning a variable" in {
        interpretShouldSucceed("var m = 8654", "Int = 8654")
        interpretShouldSucceed("m = 900", "Int = 900")
      }

      "importing a package" in {
        interpretShouldSucceed("import scala.util.matching.Regex", "import scala.util.matching.Regex")
        interpretShouldSucceed("val x = new Regex(\"happy\")", "scala.util.matching.Regex = happy")
      }

      "defining a class" in {
        interpretShouldSucceed("class Bar(val x: String) { def printTheThing { println(\"hdtweyhdfhdfg\") } }", "")
        interpretShouldSucceed("val z = new Bar(\"hello\")", "Bar = Bar@")
        interpretShouldSucceed("z.x", "String = hello")
        interpretShouldSucceed("z.printTheThing", "hdtweyhdfhdfg")
      }

      "defining a case class" in {
        interpretShouldSucceed("case class Foo(bar: Int)", "")
        interpretShouldSucceed("val q = new Foo(12)", "Foo(12)")
      }
    }

    "report an error" when {
      "assigning a String to an Int" in {
        interpretShouldFail("val x: Int = \"booo\"", "required: Int")
      }

      "reassigning a value" in {
        interpretShouldSucceed("val d: Int = 5", "Int = 5")
        interpretShouldFail("d = 9", "error: reassignment to val")
      }

      "reassigning an Int to a String" in {
        interpretShouldSucceed("var x = \"hey\"", "String = hey")
        interpretShouldFail("x = 5", "required: String")
      }

      "an exception is thrown" in {
        interpretShouldFail("throw new Exception", "java.lang.Exception")
      }

      "interpreting a System.exit()" ignore {
        interpretShouldFail("System.exit()", "java.lang.Exception")
      }
    }
  }
}

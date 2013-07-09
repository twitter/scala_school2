import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.scaffold.Interpreter
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import scala.concurrent.Await
import scala.concurrent.duration._

class InterpreterSpec extends WordSpec with MustMatchers {
  implicit val system = akka.actor.ActorSystem("scaffold-system")
  implicit val timeout = Timeout(5 seconds)
  val testInterpreter = system.actorOf(Props[Interpreter])

  def interpretShouldSucceed(request: String, response: String) {
    val f = testInterpreter ? Interpreter.Interpret(request)
    val responseString = Await.result(f, timeout.duration) match {
      case Interpreter.Success(resp) => resp
      case _ => "FAILED"
    }
    responseString must include (response)
  }

  def interpretShouldFail(request: String, response: String) {
    val f = testInterpreter ? Interpreter.Interpret(request)
    val responseString = Await.result(f, timeout.duration) match {
      case Interpreter.Failure(resp) => resp
      case _ => "SUCCEEDED"
    }
    responseString must include (response)
  }

  "Interpreter#receive" can {
    "add two numbers" in {
      interpretShouldSucceed("1 + 1", "Int = 2")
    }

    "add two strings" in {
      interpretShouldSucceed("\"foo\" + \"bar\"", "foobar")
    }

    "retrieve a previously set value" in {
      interpretShouldSucceed("val p = 7", "Int = 7")
      interpretShouldSucceed("p", "Int = 7")
    }

    "reassign a variable" in {
      interpretShouldSucceed("var m = 8654", "Int = 8654")
      interpretShouldSucceed("m = 900", "Int = 900")
    }

    "import a package" in {
      interpretShouldSucceed("import scala.util.matching.Regex", "import scala.util.matching.Regex")
      interpretShouldSucceed("val x = new Regex(\"happy\")", "scala.util.matching.Regex = happy")
    }

    "define a class" in {
      interpretShouldSucceed("class Bar(val x: String) { def printTheThing { println(\"hdtweyhdfhdfg\") } }", "")
      interpretShouldSucceed("val z = new Bar(\"hello\")", "Bar = Bar@")
      interpretShouldSucceed("z.x", "String = hello")
      interpretShouldSucceed("z.printTheThing", "hdtweyhdfhdfg")
    }

    "define a case class" in {
      interpretShouldSucceed("case class Foo(bar: Int)", "")
      interpretShouldSucceed("val q = new Foo(12)", "Foo(12)")
    }

    "reset the Interpreter" in {
      interpretShouldSucceed("val x = 1234", "Int = 1234")
      testInterpreter ! Interpreter.Reset
      interpretShouldFail("x", "error: not found: value x")
    }
  }

  "Interpreter#receive reports an error" when {
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
  }
}

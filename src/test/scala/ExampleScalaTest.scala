import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

// Testing documentation
// http://doc.scalatest.org/1.0/org/scalatest/WordSpec.html
// http://doc.scalatest.org/1.0/org/scalatest/matchers/MustMatchers.html

class ExampleScalaTest extends WordSpec with MustMatchers {
  
  "Any test" must {
    "accurately perform comparisons" in {
      1 must equal (1)
    }
    
    "accurately perform calculations" in {
      (1 + 1) must equal (2)
    }
    
    "handle strings properly" in {
      "Hello World!" must include ("World")
      "Hello World!" must not include ("Dog")
    }
  }
  
}
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class ExampleTest extends WordSpec with MustMatchers {
  
  "Any test" must {
    "accurately perform comparisons" in {
      1 must equal (1)
    }
    
    "accurately perform calculations" in {
      (1 + 1) must equal (2)
    }
  }
  
}
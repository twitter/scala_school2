import org.scalatest.FunSpec

class ExampleTest extends WordSpec with MustMatchers {
  
  describe("One Test") {
    it("should pass an assert") {
      assert(1 === 1)
    }
  }
  
}
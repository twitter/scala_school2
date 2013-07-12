package com.twitter.scaffold

import concurrent.duration._
import org.scalatest.OptionValues._
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest

class ScaffoldSpec extends WordSpec with MustMatchers with ScalatestRouteTest with ScaffoldService {
  override def actorRefFactory = system
  implicit val routeTestTimeout = RouteTestTimeout(10 seconds) 

  "The assets route" should {
    "render a lesson" is (pending)
  }

  "The markdown route" should {
    "respond to GET /" in {
      Get("/") ~> sealRoute(markdownRoute) ~> check {
        status must be (OK)
      }
    }
  }

  "The interpreter" should {
    "respond Not Found for non-existent interpreter ids" when {
      "interpreting" in {
        Post("/interpreter/0", "1 + 1") ~> sealRoute(interpreterRoute) ~> check {
          status must be (NotFound)
        }
      }

      "deleting" in {
        Delete("/interpreter/0") ~> sealRoute(interpreterRoute) ~> check {
          status must be (NotFound)
        }
      }

      "autocompleting" in {
        Post("/interpreter/0/completions", "") ~> sealRoute(interpreterRoute) ~> check {
          status must be (NotFound)
        }
      }
    }

    "be able to create a new console" in {
      Post("/interpreter","") ~> sealRoute(interpreterRoute) ~> check {
        status must be (Created)
        header("Location") must be ('defined)
        // Get the Option value and then get the HttpHeader Value
        header("Location").value.value must fullyMatch regex ("""^/interpreter/(\d+)$""")
      }
    }

    "not share namespace with other valid consoles" in {
      Post("/interpreter","") ~> sealRoute(interpreterRoute) ~> check {
        val newInterpreterLocation = header("Location").value.value
        Post(newInterpreterLocation, "def myThingA = 1 + 1") ~> route ~> check {
          status must be (OK)

          Post("/interpreter","") ~> sealRoute(interpreterRoute) ~> check {
            val secondInterpreterLocation = header("Location").value.value
            Post(secondInterpreterLocation, "myThingA") ~> route ~> check {
              status must be (BadRequest)
            }
          }
        }
      }
    }
    
    "not interpret code after being deleted" in {
      Post("/interpreter","") ~> sealRoute(interpreterRoute) ~> check {
        val newInterpreterLocation = header("Location").value.value
        Delete(newInterpreterLocation) ~> route ~> check {
          status must be (NoContent)

          Post(newInterpreterLocation, "1 + 1") ~> route ~> check {
            status must be (NotFound)
          }
        }
      }
    }
  }
}

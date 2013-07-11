package com.twitter.scaffold

import concurrent.duration._
import org.scalatest.OptionValues._
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes._



class ScaffoldSpec extends WordSpec with MustMatchers with ScalatestRouteTest with ScaffoldService {
  override def actorRefFactory = system
  implicit val routeTestTimeout = RouteTestTimeout(5 seconds) 


  "The asset route" should {
    "respond to GET /" in {
      Get("/") ~> sealRoute(assetsRoute) ~> check {
        entityAs[String] must not have length (0)
      }
    }

    "render a lesson" is (pending)
  }

  "The interpreter route" which afterWord("has") {
    "not been created" must {
      "not respond to GET requests" in {
        Get("/interpreter") ~> sealRoute(interpreterRoute) ~> check {
          status must be (MethodNotAllowed)
        }
      }

      "not interpret code to fake interpreter" in {
        Post("/interpreter/0","1 + 1") ~> sealRoute(interpreterRoute) ~> check {
          status must be (NotFound)
        }
      }

      "not delete invalid consoles" in {
        Delete("/interpreter/0","1 + 1") ~> sealRoute(interpreterRoute) ~> check {
          status must be (NotFound)
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
    }

    "been created" must {
      "interpret code" in {
        Post("/interpreter","") ~> sealRoute(interpreterRoute) ~> check {
          val newInterpreterLocation = header("Location").value.value
          Post(newInterpreterLocation, "1 + 1") ~> route ~> check {
            status must be (OK)
          }
        }
      }

      "not share namespace with other console" in {
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
    }

    "been deleted" must {
      "not interpret code" in {
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
}

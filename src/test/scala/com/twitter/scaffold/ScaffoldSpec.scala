package com.twitter.scaffold

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.testkit.ScalatestRouteTest
import scala.util.Random

class ScaffoldSpec extends WordSpec with MustMatchers with ScalatestRouteTest with ScaffoldService {
  override def actorRefFactory = system

  "The asset route" should {
    "respond to GET /" in {
      Get("/") ~> route ~> check {
        entityAs[String] must not have length (0)
      }
    }

    "render a lesson" is (pending)
  }

  "The interpreter route" which afterWord("has") {
    "not been created" must {
      "not respond to GET requests" is (pending)
      "not interpret code to fake interpreter" is (pending)

      "not delete invalid consoles" is (pending)
      "be able to create a new console" is (pending)
    }

    "been created" must {
      "interpret code" is (pending)
      "not share namespace with other console" is (pending)
    }

    "been deleted" must {
      "not interpret code" is (pending)
    }

  }

}

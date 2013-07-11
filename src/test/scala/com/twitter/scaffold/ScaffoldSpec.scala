package com.twitter.scaffold

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.testkit.ScalatestRouteTest

class ScaffoldSpec extends WordSpec with MustMatchers with ScalatestRouteTest with ScaffoldService {
  override def actorRefFactory = system

  "The router" should {
    "respond to GET /" in {
      Get("/") ~> route ~> check {
        entityAs[String] must not have length (0)
      }
    }

    "render a lesson"  is (pending)
  }

  "A console" when {
  	"not created" must {
  		"not interpret code" is (pending)
  		"not delete invalid consoles" is (pending)
  		"be able to create a new console" is (pending)
  	}

  	"created" must {
   		"interpret code" is (pending)
   		"not share namespace with other console" is (pending)
  	}

  	"deleted" must {
  		"not interpret code" is (pending)
  	}

  }

}

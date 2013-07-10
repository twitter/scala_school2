package com.twitter.scaffold

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.testkit.ScalatestRouteTest

class ScaffoldSpec extends WordSpec with MustMatchers with ScalatestRouteTest with ScaffoldService {
  override def actorRefFactory = system

  "scaffold" should {
    "respond to GET /" in {
      Get("/") ~> route ~> check {
        entityAs[String] must not be 'empty
      }
    }
  }

}

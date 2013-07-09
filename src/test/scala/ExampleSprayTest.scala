import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import spray.testkit.ScalatestRouteTest
import spray.routing.HttpService
import spray.http.StatusCodes._

// Spray test kit information
// http://spray.io/documentation/1.1-M8/spray-testkit/

class ExampleSprayTest extends WordSpec with MustMatchers with ScalatestRouteTest with HttpService {
  def actorRefFactory = system // connect the DSL to the test ActorSystem

  val smallRoute =
    get {
      path("") {
        complete {
          <html>
            <body>
              <h1>Say hello to <i>spray</i>!</h1>
            </body>
          </html>
        }
      } ~
      path("ping") {
        complete("PONG!")
      }
    }

  "The service" should {
    "return a greeting for GET requests to the root path" in {
          Get() ~> smallRoute ~> check {
            entityAs[String] must include ("Say hello")
          }
        }

        "return a 'PONG!' response for GET requests to /ping" in {
          Get("/ping") ~> smallRoute ~> check {
            entityAs[String] must be === ("PONG!")
          }
        }

        "leave GET requests to other paths unhandled" in {
          Get("/kermit") ~> smallRoute ~> check {
            handled must be (false)
          }
        }

        "return a MethodNotAllowed error for PUT requests to the root path" in {
          Put() ~> sealRoute(smallRoute) ~> check {
            status === MethodNotAllowed
            entityAs[String] === "HTTP method not allowed, supported methods: GET"
          }
        }
      }
    }
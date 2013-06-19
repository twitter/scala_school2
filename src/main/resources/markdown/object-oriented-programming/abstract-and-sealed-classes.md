# Abstract and Sealed Classes

Yr mustache placeat ennui irony YOLO. Umami gluten-free beard incididunt brunch.

# Type aliases

    type HttpRequestMethod = String
    type HttpResponseStatus = Int

# Inheritance and subtype polymorphism

    class HttpMessage(
      val headers: Map[String, String],
      val entity: String
    )

    class HttpRequest(
      val method: HttpRequestMethod,
          headers: Map[String, String] = Map.empty,
          entity:  String              = ""
    ) extends HttpMessage(headers, entity)

    class HttpResponse(
      val status:  HttpResponseStatus,
          headers: Map[String, String] = Map.empty,
          entity:  String              = ""
    ) extends HttpMessage(headers, entity)

# Abstract classes and methods

    abstract class HttpMessage {
      def headers: Map[String, String]
      def entity:  String
    }

    case class HttpRequest(
      method:  HttpRequestMethod,
      headers: Map[String, String] = Map.empty,
      entity:  String              = ""
    ) extends HttpMessage

    case class HttpResponse(
      status:  HttpResponseStatus,
      headers: Map[String, String] = Map.empty,
      entity:  String              = ""
    ) extends HttpMessage

# Sealed, algebraic data types

    def getMethod(m: HttpMessage): Option[HttpRequestMethod] = m match {
      case HttpRequest(method, _, _) => Some(method)
    }

# Enums

    sealed abstract class HttpRequestMethod
    object HttpRequestMethod {
      case object GET extends HttpRequestMethod
      case object POST extends HttpRequestMethod
    }

    sealed abstract class HttpResponseStatus(val code: Int)
    object HttpResponseStatus {
      case object Ok              extends HttpResponseStatus(200)
      case object EnhanceYourCalm extends HttpResponseStatus(420)
    }

*TODO* override, overriding def with val
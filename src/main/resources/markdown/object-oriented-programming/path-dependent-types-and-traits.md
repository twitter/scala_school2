# Path-Dependent Types and Traits

Est selfies ad hoodie deserunt artisan, mlkshk shabby chic keffiyeh.

# Abstract type declarations

    abstract class Service {
      type Request
      type Response
      def apply(request: Request): Response
    }

    abstract class HttpService extends Service {
      type Request  = HttpRequest
      type Response = HttpResponse
    }

    class HttpEchoService extends HttpService {
      def apply(request: HttpRequest): HttpResponse = {
        HttpResponse(
          status = HttpResponseStatus.Ok,
          entity = request.entity)
      }
    }

# Path-dependent types

    abstract class Filter {
      val service: Service
      def apply(request: service.Request): service.Response
    }

    class HttpLoggingFilter(val service: HttpService) extends Filter {
      def apply(request: HttpRequest): HttpResponse = {
        import java.util.Date
        
        println("Request <%s>: %s".format(new Date, request))
        val response = service(request)
        println("Response <%s>: %s".format(new Date, response))
        response
      }
    }

# Traits as interfaces, traits as mix-ins

    trait Logging {
      def logger: java.io.PrintStream
      def log(message: String): Unit = logger.println(message)
    }

    class ConsoleLoggingHttpEchoService extends HttpService with Logging {
      val logger = Console.out

      def apply(request: HttpRequest): HttpResponse = {
        import java.util.Date

        log("Request <%s>: %s".format(new Date, request))
        val response = HttpResponse(
          status = HttpResponseStatus.Ok,
          entity = request.entity)
        log("Response <%s>: %s".format(new Date, response))
        response
      }
    }

> #### Exercise: access modifiers?

# abstract override and refinements

    trait LoggingService extends Service with Logging {
      abstract override def apply(request: Request): Response = {
        import java.util.Date

        log("Request <%s>: %s".format(new Date, request))
        val response = super.apply(request)
        log("Response <%s>: %s".format(new Date, response))
        response
      }
    }

    val consoleLoggingHttpEchoService = new HttpEchoService with Logging {
      val logger = Console.out
    }

*TODO* conflicts, linearization algorithm, how traits are delegates under the covers

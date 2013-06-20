# Abstract and Sealed Classes

Creating enumerations and algebraic data types from primitive, object-oriented building blocks.

# Enumerations: `sealed`

> #### Note: `scala.Enumeration`
> There is an abstract class in Scala's standard library called [`Enumeration`](http://www.scala-lang.org/api/current/index.html#scala.Enumeration). Check it out, if you like; it's terribad. Fortunately there's no reason to ever use it.

There are a few common use cases for _subtype polymorphism_---generic programming based on superclass-subclass relationships---and inheritance in Scala. The first we'll look at here is building enumerations:

    abstract class RequestMethod
    object RequestMethod {
      case object DELETE  extends RequestMethod
      case object GET     extends RequestMethod
      case object HEAD    extends RequestMethod
      case object OPTIONS extends RequestMethod
      case object POST    extends RequestMethod
      case object PUT     extends RequestMethod
    }

    abstract class ResponseStatus(val code: Int)
    object ResponseStatus {
      case object Ok              extends ResponseStatus(200)
      case object NotModified     extends ResponseStatus(304)
      case object EnhanceYourCalm extends ResponseStatus(420)
    }

So far so good: you declare an abstract class, and some number of case objects extending it. But if the compelling purpose for enumerations is to provide an exhaustive set of possible values, we have two problems:

* What's to stop anybody from extending the same abstract base class? Yes, you could make the base class constructors `private` and define all subclasses in the companion object as we've done here. But we still have another problem...
* When you're pattern matching over the case objects, how do you know you've covered all of them? Alternatively, when you add a case object to the enumeration, how do you know what client code needs to be updated? For example:

        import RequestMethod._
        def isSafe(method: RequestMethod): Boolean = method match {
          case GET  => true
          case POST => false
        }

This code obviously fails to match the majority of declared `RequestMethod` objects, but maybe it's not always so obvious. The solution is to mark the base classes as `sealed`.

> #### Exercise: `sealed`
> Do it, and observe the difference when you define `isSafe`.

This limits subclassing from that base class to be allowed only within the same file, and allows the compiler to verify that pattern match expressions are exhaustive.

# Abstract methods and `override`

Another common use case for _subtype polymorphism_ is to implement so-called "algebraic data types," (more accurately known as sum types; also known as discriminated unions, tagged variants, etc.) which can be considered a more general form of enumerations. For example:

    sealed abstract class Message {
      def headers: Map[String, String]
      def entity:  String
    }

    object Message {
      case class Request(
        method:  RequestMethod   = RequestMethod.GET,
        headers: Map[String, String] = Map.empty,
        entity:  String              = ""
      ) extends Message

      case class Response(
        status:  ResponseStatus  = ResponseStatus.Ok,
        headers: Map[String, String] = Map.empty,
        entity:  String              = ""
      ) extends Message
    }

Here, instead of singleton case objects, we use case classes because there will be many instances of each case (each with its own data). Pattern matching over these is epic win, but that's beside the point: the base class here has method _declarations_ with no implementations. These methods are abstract.

Hey, wait a minute! Those abstract `def`s in the base class are being implemented as `val`s in the case classes! Yes, this is legal and encouraged. In general, the choice of whether to implement a member as a `def` or a `val` is regarded as an implementation detail, which shouldn't make any difference to code referencing that member. This is known as the "uniform access principle."

> #### Exercise: overriding
> Try providing a concrete, default implementation for `Message.entity`, to see how it affects the subclasses.

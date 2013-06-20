# Traits

Hey, calm down you two, new Shimmer is a floor wax _and_ a dessert topping!

# Composing classes

Let's say we want to define some simple "services" that accept requests and produce responses:

    import Message._

    abstract class Service {
      def apply(request: Request): Response
    }

    class HelloService extends Service {
      def apply(request: Request): Response =
        Response(status = ResponseStatus.Ok, entity = "hello")
    }

    class EchoService extends Service {
      def apply(request: Request): Response =
        Response(status = ResponseStatus.Ok, entity = request.entity)
    }

Reasonably clean design, so far: these services do exactly what their names suggest, and no more. But now say that we'd like to add some simple metrics functionality, without polluting the code as it stands. The traditional object-oriented design pattern for this is to create a [decorator](http://en.wikipedia.org/wiki/Decorator_pattern):

    final class Counter {
      private[this] var mutableCount: Int = 0
      def increment(): Unit = mutableCount += 1
      def value(): Int = mutableCount
    }

    class CountingFilter(service: Service) extends Service {
      private[this] val counter: Counter = new Counter

      def apply(request: Request): Response = {
        counter.increment()
        service(request)
      }

      def value(): Int = counter.value()
    }

    val countingHelloService = new CountingFilter(new HelloService)
    val countingEchoService = new CountingFilter(new EchoService)

Not bad so far. Now let's say we want to add logging as well:

    final class Logger(owner: String) {
      def log(message: Any): Unit =
        println("%s <%s>: %s".format(owner, new java.util.Date, message))
    }

    class LoggingFilter(service: Service) extends Service {
      private[this] val logger: Logger =
        new Logger(service.getClass.getName)

      def apply(request: Request): Response = {
        logger.log(request)
        val response = service(request)
        logger.log(response)
        response
      }
    }

    val loggingCountingHelloService = new LoggingFilter(countingHelloService)
    val loggingCountingEchoService = new LoggingFilter(countingEchoService)

Now things are starting to get clumsy: we're composing these decorators in some arbitrary order, and only the outermost decorator's methods are exposed to us. In this example, if you want to get the current value of the `Counter` attached to `HelloService`, you'd better have held onto that `countingHelloService` reference before feeding it to the `LoggingFilter`:

    class CountingLoggingFilter(service: Service) extends Service {
      private[this] val countingService = new CountingFilter(service)
      private[this] val loggingService = new LoggingFilter(countingService)

      def apply(request: Request): Response = loggingService(request)
      def value(): Int = countingService.value()
    }

The effort here is obviously not going to scale. The more classes we want to mix and match like this, the more of these forwarding shims we have to write. Despite trying to observe the object-oriented design principle of [preferring composition over inheritance](http://en.wikipedia.org/wiki/Composition_over_inheritance) we've hit a wall. Inheritance is looking justifiably appealing, not merely for the convenience of code reuse, but for the fact that the service we're building _is a_ counting, logging service. Alas, the JVM doesn't allow us to inherit from both `HelloService`, `CountingFilter` and `LoggingFilter`.

# Composing `traits`

Relief comes from the realization that the process of constructing these forwarding shims is completely mechanical, something the compiler ought to be good at. Starting with the `Counter` and `Logger` classes:

    trait Counter {
      private[this] var mutableCount: Int = 0
      protected[this] def increment(): Unit = mutableCount += 1
      def value(): Int = mutableCount
    }

    trait Logger {
      protected[this] def log(message: Any): Unit =
        println("%s <%s>: %s".format(
          getClass.getName,
          new java.util.Date,
          message))
    }

When we rework these to be `trait`s, it signals to the compiler that _at least two_ classes should be synthesized for each: one containing the concrete behavior, and another containing forwarding methods.

> #### Note for Java refugees
> If you're familiar with `interface`s in Java, those play a role here as well in the compiled bytecode. For a given trait, an interface is generated containing all of its methods. The class containing the forwarding methods implements that interface. If any of those methods are abstract in the Scala trait, they will be abstract in the resulting compiled class as well.

    class CountingFilter(service: Service) extends Service with Counter {
      def apply(request: Request): Response = {
        increment()
        service(request)
      }
    }

    class LoggingFilter(service: Service) extends Service with Logger {
      def apply(request: Request): Response = {
        log(request)
        val response = service(request)
        log(response)
        response
      }
    }

> #### Exercise: conflicts?
> What happens if you try to mix in multiple traits that conflict in some way?

The `CountingFilter` now _is a_ `Counter`, and the `LoggingFilter` now _is a_ `Logger`. But we haven't solved our real problem yet, which is that the filters themselves still don't compose nicely; they need to be traits as well.

# Mind == Blown: `abstract override`

The crux here is that each filter is really meant to extend some underlying service---either `HelloService`, `EchoService` or something else---and if we were weaving a filter together with a service individually by hand, we'd have the filter override its superclass `apply` method to augment the service's behavior. However, assuming we have no prior knowledge of where each filter will be used, the only thing we know for sure about its superclass is that it's some abstract `Service`.

    trait CountingFilter extends Service with Counter {
      abstract override def apply(request: Request): Response = {
        increment()
        super.apply(request)
      }
    }

    trait LoggingFilter extends Service with Logger {
      abstract override def apply(request: Request): Response = {
        log(request)
        val response = super.apply(request)
        log(response)
        response
      }
    }

> #### Spoiler Alert
> This is one of the wackiest things (in a good way, mostly) in all of Scala.

The `abstract override` combo indicates that you're overriding behavior in _some_ concrete superclass, but _you don't know_ what that superclass actually is yet. Putting it all together:

    val loggingCountingHelloService =
      new HelloService with CountingFilter with LoggingFilter
    val loggingCountingEchoService =
      new EchoService with CountingFilter with LoggingFilter

# Linearization algorithm

Lomo mlkshk Brooklyn esse swag Tonx 3 wolf moon. Narwhal tattooed Truffaut, occupy sriracha dolore beard tempor gastropub master cleanse fashion axe street art blue bottle. Delectus mumblecore VHS, culpa Tonx odio est Etsy Odd Future Vice lomo messenger bag nisi. American apparel roof party helvetica, et biodiesel disrupt pour-over. Delectus chillwave ut lo-fi try-hard single-origin coffee. Neutra wolf Godard ethical next level. Quis YOLO tattooed, skateboard salvia Bushwick cillum Portland occupy.

Vegan hella incididunt deep v, esse nesciunt yr brunch. Art party mixtape bicycle rights dreamcatcher, readymade fugiat qui master cleanse intelligentsia gastropub non deep v lomo. Try-hard bespoke messenger bag, nesciunt in chillwave excepteur aliqua cray cred ethnic scenester 3 wolf moon tempor biodiesel. VHS aliquip authentic, letterpress disrupt meh Terry Richardson nulla before they sold out 3 wolf moon yr accusamus. Twee officia sartorial, 90's bicycle rights High Life kitsch cornhole squid hoodie Godard banjo YOLO. Banh mi aliqua VHS, disrupt ex bitters pitchfork farm-to-table authentic. Tousled Brooklyn Austin enim.

# Methods, Side Effects and Unit

Defining methods, best practices for type inference, and identifying side effects with Unit.

# Methods: `def`

Method `def`s are syntactically uniform with `var` and `val` declarations. The only difference is that `def`s can take parameters:

    def add(a: Int, b: Int): Int = a + b

The type ascription is in the same place, right before the `=`. Also note: no braces or `return` statement are needed; the body of the `add` method is just an expression. Now, how about type inference?

    def add(a, b) = a + b

We can guess that `a` and `b` might be `Int`, but that's not the only type for which a `+` method is defined. They could be `String`s, for example, so the compiler can't guess. Type ascriptions are _always_ required for method parameters (note: this is enforced by the language syntax, not the type checker), but you _can_ usually omit the method's result type ascription:

    def add(a: Int, b: Int) = a + b

Since we know `a` and `b` are both `Int`, the expression `a + b` must be `Int`, and therefore the method can be inferred to produce `Int`. The same rules you saw previously, about least upper bounds, apply here as well:

    import util.Random._
    def contrived(a: Int, b: String) = if (nextBoolean()) a else b

# Named and default arguments

When calling a method, you don't have to pass the arguments in the order they're declared in the method parameter list; you can use named arguments instead:

    def formatUser(userId: Long, userName: String, realName: String): String =
      "%s <%d>: %s".format(userName, userId, realName)

    formatUser(
      realName = "Dan Rosen",
      userName = "drosen",
      userId = 31337
    )

> #### Note for C refugees
> Don't use terrible names for your method parameters. In Scala, those names are part of your public API, not just implementation detail.

When calling a method that takes many parameters (many of which might have the same type), it's nice to be explicit at the call sites about which parameter is bound to which argument. This provides useful documentation for anybody reading the code, and some degree of future-proofing against API changes.

Named arguments also allow us to provide defaults:

    def formatUser(
        userId: Long = 0,
        userName: String = "unknown",
        realName: String = "Unknown"
      ): String =
      "%s <%d>: %s".format(userName, userId, realName)

    formatUser(userName = "drosen")

# Explicit type ascriptions

Even though you can usually omit method result type ascriptions, most of the time it's good practice to include them, because:

* They serve as useful API documentation, saving other developers the hassle of trying to do type inference on your code in their heads.
* The type inference algorithm assumes you don't make mistakes. It occasionally infers a type you didn't intend, due to a mistake in your code. Usually this will cause a compile error, as the resulting program fails to typecheck, but occasionally---surprisingly---compilation will succeed. This is bad.

There is also a situation where you _must_ provide a method result type: recursive methods.

    def fib(n: Int) = if (n == 0 || n == 1) 1 else fib(n - 1) + fib(n - 2)

Have some sympathy for the compiler: in order to infer the result type of `fib`, it looks at the method body and determines that it must first infer the result type of `fib`. Turtles all the way down.

# Side-effecting methods and `Unit`

How about methods, or statements, that don't produce any value?

    println("hello") // what's the result type of println?

In C-influenced languages, the result type would be `void`, representing the _absence_ of a value; but the notion that some methods can produce values and some can't adds some degree of inconsistency and complexity to these languages. Scala, along with other functional languages such as Haskell and ML, introduces a type called `Unit` which has a single value: `()`. You can't actually do anything interesting with `()`---in that respect it's very similar to `void`---but its presence comes in handy when you start using higher-order functions.

Some more examples of the `Unit` type and `()` value:

    // no effect or value
    val a = { }

    // local side effect only
    val b = {
      var x = 0
      x = x + 1
    }

    // no "else" clause
    val c = if (nextBoolean()) 42

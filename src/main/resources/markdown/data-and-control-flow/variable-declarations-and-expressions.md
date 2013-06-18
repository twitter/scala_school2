# Variables and Expressions

Declaring variables and values, executing statements and composing expressions.

# Mutable variables: `var`

You can declare and assign a variable in Scala using the `var` keyword:

    var n: Int = 1 + 1

This can be read as "define a variable `n` of type `Int` with the initial value `1 + 1`." The syntax here is a bit more verbose at first than in C-influenced languages (e.g. `int n = ...`, `var n = ...` or just `n = ...`), because we explicitly state we're defining a variable with a particular type. We're not usually this pedantic---we can usually leave off the _type ascription_, `: Int`---but we'll keep it here for clarity, for now.

`var`s are mutable, so we can reassign `n`:

    n = n + 1

It turns out that `Int` is not the only data type in Scala. We also have 21<sup>st</sup> century `String` technology; but don't cross the streams:

    var s: String = "hello, scala"

    n = s // shouldn't work
    s = n // shouldn't work either

Scala is strongly typed: it's not legal to assign a `String` to an `Int` or vice-versa. It's also statically typed: the type checker runs at compile time, rather than at run time.

> #### Exercise: explicit conversions
> How do you convert a `String` to an `Int`? How do you convert an `Int` to a `String`?

# Statements vs. expressions

Imperative-style programming, as in most procedural and object-oriented languages since the 1950s, is all about the sequential execution of **statements**. The execution of a statement has some effect on the system's state (e.g. assigning a value to a variable) and different code paths can execute predicated on the current state. For example:

    import util.Random._

    var n: Int = 0
    if (nextBoolean()) {
      println("randomly true")
      n = n + 1
    } else {
      println("randomly false")
      n = n - 1
    }
    println(n)

We're accustomed to thinking about control-flow structures like `if-else` as imperative statements, that produce the net effect of the statements within them. In that style, it's not always obvious what that net effect might be. In Scala, it's more conventional to use control-flow structures as **expressions**, which have meaningful types at compile time and directly produce values at runtime. For example:

    n = if (nextBoolean()) n + 1 else n - 1

Look ma, no side effects! `n + 1` and `n - 1` are both expressions of type `Int`, so the `if-else` expression will also have type `Int`. You can keep the `println` statements if you want to, by using **block expressions**:

    n =
      if (nextBoolean()) {
        println("randomly true")
        n + 1
      } else {
        println("randomly false")
        n - 1
      }

The big deal about expressions is that they compose cleanly:

    var s: String =
      if (n % 2 == 0)
        "n is even: %d, half is: %d".format(n, n / 2)
      else
        "n is odd: %d, randomly fixing it: %d".format(n, if (nextBoolean()) n + 1 else n - 1)

> #### Exercise: "feel the burn"
> Rewrite this code in imperative style.

# Immutable values: `val`

Given that expressions compose, there isn't nearly as much need for mutable variables as there would be in an imperative style. It's typically easiest to compute values by composing expressions rather than sequencing effects. So we use `val` most of the time:

    val n: Int = 1 + 1

`val`s are immutable:

    n = n + 1 // shouldn't work

> #### Protip: use `val`
> Sometimes `var` is the right thing. Most of the time it isn't.
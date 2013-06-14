# Pattern Matching and For-Expressions

Control flow on steroids.

# Pattern matching

Old and busted:

    def monthName(n: Int): String =
      if (n == 1) "January"
      else if (n == 2) "February"
      else if (n == 3) "March"
      // ...
      else "Unknown"

New hotness:

    def monthName(n: Int): String =
      n match {
        case 1 => "January"
        case 2 => "February"
        case 3 => "March"
        // ...
      }

What happens if we fall through? `case _`.

    def daysInMonth(n: Int): Int =
      n match {
        case 1 | 3 | 5 | 7 | 8 | 10 | 12 => 31
        case 4 | 6 | 9 | 11 => 30
        case 2 => 28
      }

These are all "literal patterns." Similar to `switch`-`case` from Java.

    def whatIs(a: Any): String =
      a match {
        case 1          => "the number one"
        case "hello"    => "a common greeting"
        case List(_)    => "a list with one element"
        case List(_, _) => "a list with two elements"
      }

Typed patterns

Variable patterns

    def whatIs(a: Any): String =
      a match {
        case n: Int    => "the number " + n
        case s: String => "the string " + s
        case _         => "something else"
      }

Combining

    def whatIs(a: Any): String =
      a match {
        case List(x) => "a list with one element: %s".format(x)
        case List(x, y) => "a list with two elements: %s, %s".format(x, y)
      }

# Handle exceptions with patterns

first:

    "1".toInt

but:

    "i like pretty flowers".toInt

so:

    def safeToInt(s: String): Int =
      try {
        s.toInt
      } catch {
        case _: NumberFormatException => 0
      }

note that this is an expression that has a type.

but what about `null`?

    def safeToInt(s: String): Int =
      try {
        s.toInt
      } catch {
        case _: NumberFormatException => 0
        case _: NullPointerException => 0
      }

# Option

    def safeToInt(s: String): Option[Int] =
      try {
        Some(s.toInt)
      } catch {
        case _: NumberFormatException => None
      }

# For-comprehensions

    for (i: Int <- 1 to 10) println("hello world")

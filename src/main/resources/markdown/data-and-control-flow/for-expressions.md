# For-Expressions

Transforming collections in style

# `while`- and `for`-loops

We've all written this code in _some_ language at least once:

    var i = 0
    while (i < 10) {
      println("I'M AWESOME")
      i = i + 1
    }

or more succinctly:

    for (i <- 0 until 10)
      println("I'M AWESOME")

# Scaling up and out

Fantastic. Now let's say you're trying to do something marginally more useful:

    import collection.mutable.Buffer

    val quietWords = List("let's", "transform", "some", "collections")
    val noisyWords = Buffer.empty[String]
    for (i <- 0 until quietWords.size)
      noisyWords += quietWords(i).toUpperCase

There are a few truly awful things about this code:

* Explicitly indexing into the list is prone to bugs; you're likely to have an off-by-one error or accidentally use the wrong index variable in the wrong collection.
* The time complexity of this algorithm is O(n<sup>2</sup>), because indexing into a `List` is a linear-time operation, not constant-time (note: if we used a more appropriate collection type, such as `Vector`, this wouldn't be an issue).
* It's not nearly DRY enough: `noisyWords` appears two times and `quietWords` appears three times.

So we can do better:

    val noisyWords = Buffer.empty[String]
    for (word <- quietWords)
      noisyWords += word.toUpperCase

No more explicit indexing, and we're now linear time, but...

# `for`-expressions

The mutable `Buffer`-based approach is still dissatisfying. We've been doing very well treating control flow structures as value-producing expressions so far, so why not here?

    val noisyWords = for (word <- quietWords) yield
      word.toUpperCase

Aha, immutable and DRY. The `for-yield` expression indicates _transformation_: it directly produces a new collection, where each element is transformed from a corresponding element in the original collection. The behavior varies depending on the type of the original collection, but in this case where you start with a `List[String]`, the `for-yield` expression produces a `List[String]` in the order you'd expect. In many other programming languages, this is referred to as "list comprehensions."

> #### Note for Java refugees
> The `yield` keyword has nothing to do with Java's `Thread.yield()` method. In fact, since `yield` is a reserved keyword in Scala, if you want to call that method, you have to write ``Thread.`yield`()`` instead. Surrounding the keyword in backticks forces it to be parsed as an identifier.

One nice thing about `for`-loops and `for`-expressions is that they can contain multiple _generators_, producing a similar effect to nested loops:

    val salutations = for (hello <- List("hello", "greetings"); world <- List("world", "interwebs")) yield
      "%s %s!".format(hello, world)

This syntax starts to get a bit clunky, the more generators you have, so Scala provides an alternative syntax:

    val salutations = for {
      hello <- List("hello", "greetings")
      world <- List("world", "interwebs")
    } yield "%s %s!".format(hello, world)

This is a bit confusing-looking at first, since the braces look like they're delineating a block of statements. It's not difficult to get used to, though, and it's the recommended style when you have multiple generators.

# Assignments and filters

You can also directly assign bindings (equivalent to `val`s) inside the `for { ... }`, not directly generated from elements in a collection:

    val salutations = for {
      hello <- List("hello", "greetings")
      world <- List("world", "interwebs")
      salutation = "%s %s!".format(hello, world) // assignment here!
    } yield salutation

... which isn't necessarily useful by itself, but becomes very handy when you need to _filter_ some of the results:

    val salutations = for {
      hello <- List("hello", "greetings")
      world <- List("world", "interwebs")
      salutation = "%s %s!".format(hello, world)
      if salutation.length < 20 // tl;dr
    } yield salutation

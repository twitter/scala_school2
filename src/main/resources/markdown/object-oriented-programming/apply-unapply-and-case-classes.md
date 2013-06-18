# Apply, Unapply and Case Classes

Dropping boilerplate like Kanye drops the mic.

# The `apply` method

So far, we have three different ways to construct values:

    // literals
    val n = 1
    val s = "hello"

    // constructors
    val nvi = new NotVeryInteresting

    // magic?
    val list = List(1, 2, 3)
    val map = Map(1 -> "one", 2 -> "two")

The third category there just uses a method on the companion object named `apply`, which is treated as syntactically special by Scala: if the parser sees parameters being passed to an object, rather than a method, it translates that to a call to the object's `apply` method. This implies you can write your own:

    class Recipe(val ingredients: List[String], val directions: List[String])
    object Recipe {
      def apply(ingredients: List[String], directions: List[String]): Recipe =
        new Recipe(ingredients, directions)
    }

In cases like this, the `apply` method's special treatment provides little more than syntactic sugar, allowing you to leave off the `new` keyword when instantiating objects; not especially compelling. In cases like `List` or `Map` it's a bit more convenient, because you can't actually instantiate those with `new`; they aren't concrete classes. And spoiler alert: this is a big part of how Scala implements "first-class" functions.

# The `unapply` method

There's another bit of magic we haven't unraveled yet:

    def whatIs(a: Any): String = a match {
      case 1                       => "the number one" // literal pattern
      case nvi: NotVeryInteresting => "nothing interesting" // typed variable pattern
      case List(x, y)              => "a List containing %s and %s".format(x, y) // magic?
    }

This works because of some additional plumbing called `unapply`. which is somewhat more mind-bending than `apply`, but very powerful. In extremely rough terms, the translation the compiler applies above (ignoring the first two cases) is:

    def whatIs(a: Any): String = {
      val resultOpt: Option[(Any, Any)] = List.unapply(a)
      if (resultOpt.isDefined) {
        val result = resultOpt.get
        val x = result._1
        val y = result._2
        "a List containing %s and %s".format(x, y)
      } else {
        throw new MatchError(a)
      }
    }

> #### Exercise: lies!
> The above code doesn't actually compile, because in the case of `List` and most other collections, the real translation involves a slightly different method called `unapplySeq`. Figure out how that works.

This again implies you can write your own `unapply` (or `unapplySeq`) for your own classes:

    class Recipe(val ingredients: List[String], val directions: List[String])
    object Recipe {
      def apply(ingredients: List[String], directions: List[String]): Recipe =
        new Recipe(ingredients, directions)

      def unapply(recipe: Recipe): Option[(List[String], List[String])] =
        if (recipe eq null) None
        else Some((recipe.ingredients, recipe.directions))
    }

This is a fairly conventional implementation of `unapply`.

> #### Note: object equality
> We've been using `==` so far to determine value equality, but in the example above we use `eq` instead. This is testing for _reference equality_. You can do this on any object extending `AnyRef`. Incidentally, note that `==` doesn't yet work the way we'd want it to for `Recipe`... we'll fix that shortly.

Now that `Recipe` has a suitable `unapply` method, you can do things like:

    val pbj = new Recipe(
      ingredients = List("peanut butter", "jelly", "bread"),
      directions = List("put the peanut butter and jelly on the bread"))

    val baconPancakes = new Recipe(
      ingredients = List("bacon", "pancakes"),
      directions = List("take some bacon", "put it in a pancake"))

    def containsNuts(recipe: Recipe): Boolean = recipe match {
      case Recipe(ingredients, _) => ingredients exists { _ containsSlice("nut") }
    }

    def isSimple(recipe: Recipe): Boolean = recipe match {
      case Recipe(_, List(_)) => true
      case _                  => false
    }

> #### Boss Level Exercise: custom extractors
> It's extremely common to see hairy `if-else` style code, where the predicates determining which branch should be chosen are inseparable from the logic that happens in each branch. Custom extractors help to tease that spaghetti code apart into smaller, composable bits. For a simple example, you saw [before](/data-and-control-flow/pattern-matching#exception-handling) how to safely parse an `Int` from a `String`... Capture that logic in a `ContainsInt` extractor, for great good.

# Case classes

In most cases, when you're defining data types to model a problem domain, you just want all this stuff to work. Your implementations for `apply` and `unapply`, not to mention many others like `equals`, `hashCode`, `toString` and `copy`, will be pretty mechanical boilerplate. Scala tends to be very good about not forcing you to do repetitive, mechanical work, and this is one of the best examples:

    case class Recipe(ingredients: List[String], directions: List[String])

> #### Exercise: play around
> Create a few instances of this new case class and see how it behaves compared to the old implementations.

When you create a case class, sane implementations of all of the methods above are synthesized for you by the compiler. The fact that it's so little effort to create, maintain and debug case classes should _strongly_ encourage you to create rich domain models using them.

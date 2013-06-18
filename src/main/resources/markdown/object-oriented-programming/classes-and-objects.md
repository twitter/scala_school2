# Classes and Objects

Defining classes, declaring constructors and members, and singleton objects.

# Declaring and instantiating a `class`

Nothing special here:

    class NotVeryInteresting
    val nvi = new NotVeryInteresting

We've defined our own data type, `NotVeryInteresting`, which is a subtype of `AnyRef`. It contains no data of its own, but we can instantiate it with `new`. Not very interesting, though... How about classes with some `var` members:

    class Recipe {
      var ingredients: List[String] = _
      var directions: List[String] = _
    }

    class Cookbook {
      var recipes: Map[String, Recipe] = _
    }

> #### Exercise: default field values
> What are the default values of these fields? What if we had an `Int` field? What if they were `val`s (like they ought to be)? How would you improve this code?

Slightly more interesting. Now given these definitions, we can construct instances:

    val recipe = new Recipe
    recipe.ingredients = List("peanut butter", "jelly", "bread")
    recipe.directions = List("put the peanut butter and jelly on the bread")

    val cookbook = new Cookbook
    cookbook.recipes = Map("peanut butter and jelly sandwich" -> recipe)

# Primary and auxiliary constructors

All classes have a _primary constructor_, which is a special method that's always invoked during object instantiation. The primary constructor---informally speaking---is just the body of the class, so in our example so far, the primary constructors initialize the declared fields `ingredients`, `directions` and `recipes`.

> #### Exercise: logging
> As with any other method `def`, you can put arbitrary code in primary constructors. Insert code into the `Recipe` and `Cookbook` constructors to log object instantiations.

You can also create _auxiliary constructors_ for a class:

    class Recipe {
      var ingredients: List[String] = _
      var directions: List[String] = _

      def this(ingredients: List[String], directions: List[String]) = {
        this()
        this.ingredients = ingredients
        this.directions = directions
      }
    }

    class Cookbook {
      var recipes: Map[String, Recipe] = _

      def this(recipes: Map[String, Recipe]) = {
        this()
        this.recipes = recipes
      }
    }

This is starting to look somewhat Java-esque, but at least we can instantiate objects in a single statement now:

    val recipe = new Recipe(
      ingredients = List("peanut butter", "jelly", "bread"),
      directions = List("put the peanut butter and jelly on the bread"))
    val cookbook = new Cookbook(
      recipes = Map("peanut butter and jelly sandwich" -> recipe))

Trouble is, the `Recipe` and `Cookbook` classes are still mutable (which is generally terrible), and far more boilerplate-ish. What we really want is, the primary constructor should take parameters:

    class Recipe(ingredients: List[String], directions: List[String]) {
      val ingredients: List[String] = ingredients
      val directions: List[String] = directions
    }

    class Cookbook(recipes: Map[String, Recipe]) {
      val recipes: Map[String, Recipe] = recipes
    }

> #### Exercise: OH NOES
> That doesn't compile. Explain why.

The right way to do this turns out to be much, much simpler:

    class Recipe(val ingredients: List[String], val directions: List[String])
    class Cookbook(val recipes: Map[String, Recipe])

Just declare that the constructor parameters are `val`s!

> #### Exercise: instance methods
> Implement `def printThis(): Unit = ...` for `Recipe` and `Cookbook`.

# Singleton objects

Problem: you need a good way to declare constants and utility methods.

In some older, "impure" object-oriented languages like C++, it's possible to declare variables and methods outside the scope of a class. This suggests a clean separation: it's unlikely you'd ever make a coding error because of confusion between a class instance member and a "global." The drawback here is that the language needs to provide some other mechanism for modularization of globals (e.g. `namespace`).

In newer, "pure" object-oriented languages, such as Java and C#, _everything_ is a member of a class. So conversely, these languages need to provide some other mechanism (e.g. `static`) to distinguish instance members from non-instance members. In certain cases, it's easy to confuse these.

Scala's approach is much more similar to Ruby's `module` concept: just like you can declare a `class`, which provides a template for instantiating an arbitrary number of objects, you can also declare an `object`, which is a singleton with its own distinct type. For example:

    object Recipes {
      val pbj = new Recipe(
        ingredients = List("peanut butter", "jelly", "bread"),
        directions = List("put the peanut butter and jelly on the bread"))
      val baconPancakes = new Recipe(
        ingredients = List("bacon", "pancakes"),
        directions = List("take some bacon", "put it in a pancake"))
    }

It's not legal here to instantiate a `new Recipes`.

> #### Exercise: lazy objects
> Singleton `object`s are lazily initialized. How lazy?

# Companion objects

When a class and an object are defined with the same name, in the same source file, they're called "companions." Companion objects are tremendously useful, as demonstrated by their prevalence in the [Scala standard library](http://www.scala-lang.org/api). For example:

    class Recipe private (val ingredients: List[String], val directions: List[String])
    object Recipe {
      val pbj = new Recipe(
        ingredients = List("peanut butter", "jelly", "bread"),
        directions = List("put the peanut butter and jelly on the bread"))
      val baconPancakes = new Recipe(
        ingredients = List("bacon", "pancakes"),
        directions = List("take some bacon", "put it in a pancake"))

      def make(ingredients: List[String], directions: List[String]): Option[Recipe] =
        if (ingredients.isEmpty || directions.isEmpty)
          None
        else
          Some(new Recipe(ingredients, directions))
    }

The `Recipe` class constructor here is `private`---other code can't directly instantiate a `new Recipe`---but because the `Recipe` object is a companion, it can (this is somewhat similar to `friend` in C++).

> #### Note: access modifiers
> This is the first time we've seen `private` (on a primary constructor, no less)! You can annotate fields and methods to be `private` as well. There's also `protected`, but no `public`: everything is publicly accessible by default. This is actually pretty reasonable, considering we generally prefer `val` and immutable data structures, unlike in most other object-oriented languages where `private` encapsulation is absolutely necessary to prevent unwanted mutation.

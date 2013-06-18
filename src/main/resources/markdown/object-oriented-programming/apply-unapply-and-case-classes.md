# Apply, Unapply and Case Classes

Nihil veniam vegan intelligentsia lomo nulla. Single-origin coffee flannel tousled plaid.

# Banksy next level ex

what's special about List

    val list = List(1, 2, 3) // why no new?

`apply` method.

    class Recipe(val ingredients: List[String], val directions: List[String])
    object Recipe {
      def apply(
          ingredients: List[String] = List.empty,
          directions: List[String] = List.empty): Recipe =
        new Recipe(ingredients, directions)
    }

why can we do this?

    def isThreeElementList(a: Any): Boolean = a match {
      case List(_, _, _) => true
      case _             => false
    }

`unapply` method.

    class Recipe(val ingredients: List[String], val directions: List[String])
    object Recipe {
      def apply(
          ingredients: List[String] = List.empty,
          directions: List[String] = List.empty): Recipe =
        new Recipe(ingredients, directions)

      def unapply(recipe: Recipe): Option[(List[String], List[String])] =
        if (recipe eq null) None
        else Some((recipe.ingredients, recipe.directions))
    }

> #### Note: equality
> reference vs value equality. note that recipes aren't currently `==`.

OMG backwards-looking:

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

> #### Exercise: `String` - `Int` extractor
> Create an object `ContainsInt` to safely parse an `Int` from a `String`.

Use unapply to encapsulate/reuse branching logic.

OMG boilerplate: apply/unapply, equals/hashCode, toString, copy, ...

    case class Recipe(ingredients: List[String], directions: List[String])

This is a huge win.
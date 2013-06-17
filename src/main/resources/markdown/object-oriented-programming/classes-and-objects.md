# Object-Oriented Programming

In you probably haven't heard of them High Life fashion axe. Tattooed fanny pack wayfarers literally qui gentrify, wolf bitters you probably haven't heard of them kale chips stumptown.

# Put a bird on it

define and instantiate class:

    class Cookbook
    val c = new Cookbook

fields:

    class Recipe {
      var ingredients: List[String] = List.empty
      var directions: List[String] = List.empty
    }

    class Cookbook {
      var recipes: Map[String, Recipe] = Map.empty
    }

    val recipe = new Recipe
    recipe.ingredients = List("peanut butter", "jelly", "bread")
    recipe.directions = List("put the peanut butter and jelly on the bread")

    val cookbook = new Cookbook
    cookbook.recipes = Map("peanut butter and jelly sandwich" -> recipe)

boo mutable: constructors (broken):

    class Recipe(ingredients: List[String], directions: List[String]) {
      val ingredients: List[String] = ingredients
      val directions: List[String] = directions
    }

    class Cookbook(recipes: Map[String, Recipe]) {
      val recipes: Map[String, Recipe] = recipes
    }

fixed:

    class Recipe(val ingredients: List[String], val directions: List[String])

    class Cookbook(val recipes: Map[String, Recipe])

    val recipe = new Recipe(
      ingredients = List("peanut butter", "jelly", "bread"),
      directions = List("put the peanut butter and jelly on the bread"))
    val cookbook = new Cookbook(
      recipes = Map("peanut butter and jelly sandwich" -> recipe))

doing something "useful": primary constructor body

    class Recipe(val ingredients: List[String], val directions: List[String]) {
      println("Ingredients: " + ingredients)
      println("Directions: " + directions)
    }

    class Cookbook(val recipes: Map[String, Recipe]) {
      println("Recipes: " + recipes)
    }

can do defaults the same way we did defaults for methods, or with aux constructors (not common)

    class Recipe(val ingredients: List[String], val directions: List[String]) {
      println("Ingredients: " + ingredients)
      println("Directions: " + directions)

      def this(ingredients: List[String]) =
        this(ingredients, List.empty)
    }

    class Cookbook(val recipes: Map[String, Recipe]) {
      println("Recipes: " + recipes)
    }

> #### Access modifiers
> public by default (no `public` keyword)

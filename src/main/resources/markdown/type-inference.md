# Type Inference

More typing with less typing.

# Basic type inference

In the previous section, we kept type ascriptions on all `var` and `val` declarations for clarity, like:

    val n: Int = 1

But it's pretty obvious in this case what the type of `n` is. In many cases, you can omit the type ascriptions:

    val n = 1 + 1
    val s = "hello, type inference"

Scala is statically typed, so the compiler has to infer appropriate types for `n` and `s` from context. In this case, the static types of `n` and `s` are inferred from the types of the expressions on the right hand side of the assignments.

We can go further...

    import util.Random._
    val n = if (nextBoolean()) 1 else 0
    val s = if (nextBoolean()) "less filling" else "tastes great"

# Least upper bounds

What type is inferred here, if the types don't quite line up?

    val wat = if (nextBoolean()) 1 else "tastes great"

![lol](img/type-lattice.png)

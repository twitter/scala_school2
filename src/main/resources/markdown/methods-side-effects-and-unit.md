# Methods, Side Effects and Unit

Define methods, identify side effects and something about `Unit` I guess.

# Define methods

    def add(a: Int, b: Int): Int = a + b

Method `def`s are syntactically uniform with `var` and `val` declarations. Type ascription in the same place, use of `=`. Also note: no braces or `return` statement, the body of the `add` method is just an expression.

How about type inference?

    def add(a, b) = a + b

We can guess that `a` and `b` might be `Int`, but that's not the only type that has a `+` method defined. They could be `String`s, for example, so the compiler can't guess. The parser requires type ascriptions for type parameters, but you can leave off the result type:

    def add(a: Int, b: Int) = a + b

Since we know `a` and `b` are both `Int`, the expression `a + b` must be `Int`, and therefore the method can be inferred to produce `Int`.

## Side-effecting methods and `Unit`

How about methods that don't produce a value?

    println("hello") // what's the result type of println?

Statements, blocks, value discarding.

# When to explicitly ascribe result types

Recursive definitions need type:

    def fib(n: Int) = if (n == 0 || n == 1) 1 else fib(n - 1) + fib(n - 2)

Compiler chicken-egg problem: in order to infer the result type of `fib`, it would need to infer the type of `fib`.

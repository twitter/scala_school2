# Background

Scala's ancestry and development

# Models of computation

* **1936**: [Alonzo Church](http://en.wikipedia.org/wiki/Alonzo_Church) develops the **[lambda calculus](http://en.wikipedia.org/wiki/Lambda_calculus)** as a means of formally expressing algorithmic solutions to problems. In its simplest form, variables (such as `x`) and functions (called "lambda abstractions," such as `λx.t`) are the only kinds of entity defined by the lambda calculus, and function application (such as `f x`) is the only operation. A "program" written in the lambda calculus is evaluated algebraically, by repeatedly binding, reducing and discarding terms as appropriate until no more algebraic simplifications are possible.

    Meanwhile, in independent pursuit of the same goal as Church, [Alan Turing](http://en.wikipedia.org/wiki/Alan_Turing) describes the a-machine---now known eponymously as the **[Turing machine](http://en.wikipedia.org/wiki/Turing_machine)**---consisting of a theoretical infinite length storage tape, a read-write "head" that can move left and right along the tape, and a finite state "controller" which operates the head. A "program" in this model is evaluated imperatively, by performing the state transitions prescribed in the controller until it reaches a halt state. Turing also describes a **[universal machine](http://en.wikipedia.org/wiki/Universal_Turing_machine)**: a Turing machine that can efficiently emulate any other Turing machine by interpreting instructions encoded on its tape.

* **1939**: [J. B. Rosser](http://en.wikipedia.org/wiki/J._Barkley_Rosser) asserts that Church's and Turing's computational models---as well as a third model involving recursive functions developed by Rosser and [Stephen Kleene](http://en.wikipedia.org/wiki/Stephen_Cole_Kleene)---have equivalent expressive power. That is, if the solution to a problem can be implemented in any one of the three formal models, it can be implemented in the other two as well.

* **1945**: [John von Neumann](http://en.wikipedia.org/wiki/John_von_Neumann) describes in an unfinished, unpublished draft paper the architecture of a **[stored-program computer](http://en.wikipedia.org/wiki/Stored_program_computer)**. This architecture resembles the Universal Turing Machine: it accepts arbitrary programs as input, and its execution model is based on reading and writing to storage locations. This architecture was subsequently realized in the [EDVAC](http://en.wikipedia.org/wiki/EDVAC), and despite Turing's influence (and independent implementation, [ACE](http://en.wikipedia.org/wiki/Automatic_Computing_Engine)), became known as the **[von Neumann architecture](http://en.wikipedia.org/wiki/Von_Neumann_architecture)**.

# Major language families

With programmable computers being built, the question becomes how to program them.

* **1957**: [John Backus](http://en.wikipedia.org/wiki/John_Backus) and his team at IBM develop the first version of the **[FORTRAN](http://en.wikipedia.org/wiki/Fortran)** programming language, the first popular high-level language. The semantics of FORTRAN so closely resemble von Neumann's architecture, with variables abstractly representing mutable storage locations, that FORTRAN is considered the first of what would eventually become a very large family of [von Neumann languages](http://en.wikipedia.org/wiki/Von_Neumann_programming_languages).

    A less industrially successful, yet far more academically influential language in this family is **[ALGOL](http://en.wikipedia.org/wiki/ALGOL)**, designed initially in 1958 by a committee of 13 computer scientists including Backus, to improve on FORTRAN. ALGOL is generally considered the grandparent of [Dennis Ritchie](http://en.wikipedia.org/wiki/Dennis_Ritchie)'s [C](http://en.wikipedia.org/wiki/C_\(programming_language\)) language and its many descendants.

* **1958**: [John McCarthy](http://en.wikipedia.org/wiki/John_McCarthy_\(computer_scientist\)) creates the **[LISP](http://en.wikipedia.org/wiki/Lisp_\(programming_language\))** programming language, the first popular programming language based more closely on Church's lambda calculus than on the von Neumann architecture. Functions are defined as lambda abstractions, such as `(lambda (x) (+ x 1))`, and programs are evaluated algebraically by variable substitution. LISP is considered the first of a relatively small family of [functional languages](http://en.wikipedia.org/wiki/Functional_programming).

    Ironically, despite Backus's major contribution in FORTRAN---which may have cemented the industrial success of the von Neumann style and which garnered Backus the [ACM Turing Award](http://en.wikipedia.org/wiki/Turing_Award) in 1977---his award lecture is titled "[Can Programming Be Liberated from the von Neumann Style? A Functional Style and Its Algebra of Programs](http://www.stanford.edu/class/cs242/readings/backus.pdf)." He argues in this lecture that the imperative style doesn't allow for composability, reuse or algebraic reasoning about program fragments, and presents a new language called [FP](http://en.wikipedia.org/wiki/FP_\(programming_language\)) embodying functional principles.

# Type systems, polymorphism

Compilers for FORTRAN and many other emerging languages include some amount of type checking, to prove basic well-formedness properties of programs before they are executed. These early type systems are overly conservative, however---rejecting programs that would otherwise run without issue---so refinement is needed.

* **1967**: [Ole-Johan Dahl](http://en.wikipedia.org/wiki/Ole-Johan_Dahl) and [Kristen Nygaard](http://en.wikipedia.org/wiki/Kristen_Nygaard) publish "Class and Subclass Declarations," building on ALGOL and other research from the 1960s, to define **[Simula 67](http://en.wikipedia.org/wiki/Simula)**. Simula introduces [subtype polymorphism](http://en.wikipedia.org/wiki/Subtyping) via [virtual methods](http://en.wikipedia.org/wiki/Virtual_function).

    For example, given a base class `Widget` defining a virtual `draw` method, it's possible to write a method that draws entire user interfaces without any knowledge about which concrete subclasses, such as `Button`, `Label` or `Container`, might exist. However, this technique discards specific type information, so for example, it's not possible to express the requirement that all widgets in a container must have the same type.

* **1973**: [Robin Milner](http://en.wikipedia.org/wiki/Robin_Milner) creates the **[ML](http://en.wikipedia.org/wiki/ML_\(programming_language\))** functional programming language. ML's type system introduces [parametric polymorphism](http://en.wikipedia.org/wiki/Parametric_polymorphism), in which a type can be constructed by binding its type parameters, the same way a function is evaluated by binding its value parameters.

    For example, `'a container` might denote a container widget with children of some type variable `'a`, which can be bound to any type: `button container` is a container of buttons, `label container` is a container of labels, and the two are considered different types. However, there's no way of expressing a heterogeneous `widget container`, and in fact the `container` data type can't assume that the type parameter `'a` is a `widget` at all.

* **1985**: [Luca Cardelli](http://en.wikipedia.org/wiki/Luca_Cardelli) and [Peter Wegner](http://en.wikipedia.org/wiki/Peter_Wegner) recognize that subtype polymorphism and parametric polymorphism both have their respective strengths and weaknesses, and introduce a new notion of **[bounded](http://en.wikipedia.org/wiki/Bounded_quantification)** polymorphism. Cardelli, et al., later formalize this in [System F<sub><:</sub>](http://en.wikipedia.org/wiki/System_F-sub) in 1991.

    This system permits data type declarations such as `Container[A <: Widget]`, asserting that any concrete type bound to `A` must be a subtype of `Widget`. For example, a `Container[Button]` would be accepted and a `Container[Flower]` would be rejected. Additionally, a `Container[Widget]` may be heterogeneous, containing a mix of `Button`, `Label` and even other `Container` instances.

# From Wirth to Odersky

* **1966**: [Niklaus Wirth](http://en.wikipedia.org/wiki/Niklaus_Wirth) starts publishing a steady stream of programming language research, based initially on ALGOL, evolving to [Algol W](http://en.wikipedia.org/wiki/Algol_W), [Euler](http://en.wikipedia.org/wiki/Euler_\(programming_language\)) and [Pascal](http://en.wikipedia.org/wiki/Pascal_\(programming_language\)), and then on to [Modula](http://en.wikipedia.org/wiki/Modula), [Modula-2](http://en.wikipedia.org/wiki/Modula-2), [Oberon](http://en.wikipedia.org/wiki/Oberon_\(programming_language\)) and [Oberon-2](http://en.wikipedia.org/wiki/Oberon-2_\(programming_language\)). Many of the ideas explored by these languages, such as separate compilation, access modifiers and reflection, are considered core to modern object-oriented programming.

* **1991**: [Martin Odersky](http://en.wikipedia.org/wiki/Martin_Odersky), a doctoral student of Wirth's at ETH Zürich, submits his dissertation, "[A New Approach to Formal Language Definition and its Application to Oberon](http://e-collection.library.ethz.ch/eserv/eth:37765/eth-37765-01.pdf)." This research introduces a formalism called CADET (Calculus on Derivation Trees) to specify the static semantics of programming languages (called "context-dependent syntax" in this work, though not to be confused with "context-sensitive grammar"). CADET itself is not especially influential, but the experience serves as Odersky's TODO

# From Java to Scala

As years pass, the PL research community makes substantial additional progress both in programming models, accounting for the availability of multi-processor and distributed systems, and in type systems, allowing more useful proofs about programs to be expressed or even inferred. Very little of this research, however, is incorporated into languages popularly used in industry.

* **1996**: [Patrick Naughton](http://en.wikipedia.org/wiki/Patrick_Naughton), [James Gosling](http://en.wikipedia.org/wiki/James_Gosling) and Mike Sheridan at Sun Microsystems, in pursuit of a better environment for systems programming at Sun, develop the **[Java](http://en.wikipedia.org/wiki/Java_\(programming_language\))** programming language and virtual machine.

    Java is a von Neumann style language, strongly influenced by Simula (by way of [Modula-3](http://en.wikipedia.org/wiki/Modula-3) and [Smalltalk](http://en.wikipedia.org/wiki/Smalltalk)). In other words, as an imperative language it lacks useful functional combinators; and as a Simula derivative, its type system is already about 30 years old on its release date.

* **1997**: [Martin Odersky](http://en.wikipedia.org/wiki/Martin_Odersky) and [Phil Wadler](http://en.wikipedia.org/wiki/Philip_Wadler), well aware of Java 1.0's potential for success despite its many shortcomings compared to the state of the art in research, develop the **[Pizza](http://en.wikipedia.org/wiki/Pizza_\(programming_language\))** programming language. Pizza is a superset of Java which compiles to Java source, adding F-bounded parametric polymorphism ("[generics](http://en.wikipedia.org/wiki/Generics_in_Java)") and higher-order functions ("lambdas"), as well as a powerful control-flow construct called pattern-matching.

    Odersky and Wadler continue work on Pizza in collaboration with [Gilad Bracha](http://en.wikipedia.org/wiki/Gilad_Bracha) and Dave Stoutamire of Sun, releasing **[GJ](http://en.wikipedia.org/wiki/Generic_Java)** (Generic Java) in 1998. GJ compiles directly to Java bytecode, and a stripped-down version of the GJ compiler replaces `javac` in the JDK 1.3 release. In 2004, with the release of Java 5, generics finally become part of the language standard; lambdas are scheduled for Java 8 in 2014.

* **2000**: Odersky, frustrated by Java's constraints, develops a new, minimalistic language on the JVM called **Funnel**, based on the [join-calculus](http://en.wikipedia.org/wiki/Join-calculus). Despite being hosted on the JVM, however, the language lacks a means of reusing existing Java libraries, and its design is so Spartan that it's later described by Odersky as "not very pleasant to use in practice."

* **2003**: Starting over, with lessons learned from Funnel and GJ, Odersky develops Scala.

# Scala overview

Scala is an attempt to unify TODO


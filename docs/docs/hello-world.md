---
layout: docs
title: Hello World
permalink: /hello-world
---

# Hello World Compiler Plugin

> In the future Arrow Meta will replace the current PSI and descriptor-based frontend implementations in favor of the FIR model once that becomes stable in the Kotlin compiler (expected around the 1.7 release.)

Let's build a small compiler plug-in which automatically implements the `helloWorld` function. Regardless of their original contents, the function will be replaced with one that prints `Hello Λrrow Meta!`. 

```kotlin:diff
-fun helloWorld(): Unit = TODO()
+fun helloWorld(): Unit = println("Hello Λrrow Meta!")
```

Here's the code that implements such behavior:

```kotlin
val Meta.helloWorld: CliPlugin get() =
  "Hello World" {
    meta(
      namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->  // <-- namedFunction(...) {...}
        Transform.replace(
          replacing = c,
          newDeclaration = """|fun helloWorld(): Unit =
                              |  println("Hello Λrrow Meta!")
                              |""".function(descriptor)
        )
      }
    )
  }
```

Let's disect it. First of all, we indicate that we want our plugin to match functions whose name is `helloWorld`; this is done via `namedFunction` and a predicate to perform the match. The code inside will only run on those elements in your code, which are represented as the `c` parameter in the lambda. Inside that lambda we describe what ought to be done: in this case replace the original element `c` with a new version. In most plugin frameworks you need to build new code piece by piece, but Λrrow Meta contains a _quote_ mechanism which allows you to write the code as it would appear in Kotlin.

Take a look at the [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository for more details.

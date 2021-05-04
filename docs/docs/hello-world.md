---
layout: docs
title: Hello World
---

# Hello World Compiler Plugin

The following example shows a Hello World Compiler Plugin.

The Hello World plugin auto implements the `helloWorld` function by rewriting the Kotlin AST before the compiler proceeds.

```kotlin
val Meta.helloWorld: CliPlugin get() =
  "Hello World" {
    meta(
      namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->  // <-- namedFunction(...) {...}
        Transform.replace(
          replacing = c,
          newDeclaration = """|fun helloWorld(): Unit =
                              |  println("Hello ΛRROW Meta!")
                              |""".function(descriptor).syntheticScope
        )
      }
    )
  }
```

For any user code whose function name is `helloWorld`, our compiler plugin will replace the matching function for a
function that returns Unit and prints our message.

```kotlin:diff
-fun helloWorld(): Unit = TODO()
+fun helloWorld(): Unit =
+  println("Hello ΛRROW Meta!")
```

Take a look at the [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository for more details.

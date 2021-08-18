---
layout: docs
title: Hello World
---

# Hello World Compiler Plugin
```
NOTE: This Example is outdated as Arrow Meta will replace the current psi and descriptor based frontend implementations in favor of the FIR model once that becomes stable in the Kotlin compiler.
```

The following example shows a Hello World Compiler Plugin.

The Hello World plugin auto implements the `helloWorld` function by creating a new the Kotlin AST before the compiler proceeds.

```kotlin
val Meta.helloWorld: CliPlugin get() =
  "Hello World" {
    meta(
      namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->  // <-- namedFunction(...) {...}
        Transform.replace(
          replacing = c,
          newDeclaration = """|fun helloWorld(): Unit =
                              |  println("Hello ΛRROW Meta!")
                              |""".function(descriptor)
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

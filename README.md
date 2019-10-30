# Arrow Meta

Functional companion to Kotlin's Compiler & IDE

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Farrow-kt%2Farrow-meta%2Fbadge%3Fref%3Dmaster&style=flat)](https://actions-badge.atrox.dev/arrow-kt/arrow-meta/goto?ref=master)

## Documentation

### Getting started

Arrow Meta is a meta-programming library that cooperates with the Kotlin compiler in all it's phases bringing its full power to the community.
Writing compiler plugins, source transformations, IDEA plugins, linters, type search engines, automatic code refactoring,... are just a few of the [use cases](#use-cases) of the things that can be accomplished with Meta.

#### Creating your first compiler plugin

#### Project Setup

#### Hello World Compiler Plugin

The following example shows a Hello World Compiler Plugin. 
The Hello World plugin auto implements the `helloWorld` function by rewriting the Kotlin AST before the compiler proceeds.

```kotlin
val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        func({ name == "helloWorld" }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """|fun helloWorld(): Unit = 
               |  println("Hello ΛRROW Meta!")
               |""".function.synthetic
          )
        }
      )
    }
```

For any user code whose function name is `helloWorld` our compiler plugin will replace the matching function for a
function that returns Unit and prints our message.

```kotlin:diff
-fun helloWorld(): Unit = TODO()
+fun helloWorld(): Unit = 
+  println("Hello ΛRROW Meta!")
```

#### Hello World Compiler + IDE Plugin

#### Arrow Meta 💚 Kotlin Compiler

#### Anatomy of a Meta Plugin

## Plugins

### Higher Kinded Types
### Type classes
### Comprehensions
### Optics

## Use cases

## Contributing

## License

## Credits

**Build and run tests**

```
./gradlew buildMeta -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

**Build and run test + IDE plugin**

```
./gradlew publishAndRunIde -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

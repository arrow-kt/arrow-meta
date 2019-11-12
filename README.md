# Arrow Meta

[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=%230576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.jfrog.org%2Fartifactory%2Foss-snapshot-local%2Fio%2Farrow-kt%2Farrow-meta-compiler-plugin%2Fmaven-metadata.xml)](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/arrow-meta-compiler-plugin/)
[![Latest Gradle Plugin Version](https://img.shields.io/maven-metadata/v?color=%230576b6&label=latest%20Gradle%20Plugin%20version&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio%2Farrow-kt%2Farrow%2Fio.arrow-kt.arrow.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/io.arrow-kt.arrow)
![Publish artifacts](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Artifacts/badge.svg)
![Publish documentation](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Documentation/badge.svg)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Functional companion to Kotlin's Compiler & IDE

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
        namedFunction({ name == "helloWorld" }) { c ->
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

## Build and run in your local environment

**Build and run tests**

```
./gradlew buildMeta -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

**Build and run test + IDE plugin**

```
./gradlew publishAndRunIde -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

## Credits

## Contributing

Arrow Meta is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Arrow Meta and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Arrow Meta.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [#arrow-meta on Kotlin Slack](https://kotlinlang.slack.com/)

## Licence

```
Copyright (C) 2017 The Λrrow Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

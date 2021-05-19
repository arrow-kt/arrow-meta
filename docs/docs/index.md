---
layout: docs
title: Overview
permalink: /
video: WKR384ZeBgk
---

[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=0576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-meta%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/arrow-meta/)
[![Publish artifacts](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Artifacts/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Artifacts%22)
[![Publish documentation](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Documentation/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Documentation%22)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.5-blue.svg)](https://kotlinlang.org/docs/whatsnew15.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Functional companion to Kotlin's Compiler & IDE

Λrrow Meta is a meta-programming library designed to build compiler plugins for Kotlin.

The Λrrow org bundles independent plugins built with Λrrow meta.

# [Refined Types](/apidocs/arrow-refined-types/arrow.refinement/)

A refined type is any regular type constrained by predicates expected to hold in all possible values of the type's
constructor.

The refined-types plugin monitors all calls to Refined type constructors, ensuring arguments provided are verifiable in
the range of the declared predicates constraining the type.

Consider the use case of modeling a `port` number. Instead of using `Int` to describe a port, we will create our own `Port` type and
enable the refined-types capabilities by making the `Port` companion extend the [Refined](http://127.0.0.1:4000/apidocs/arrow-refined-types/arrow.refinement/-refined/index.html) class.

```kotlin:ank
import arrow.refinement.Refined
import arrow.refinement.ensure

@JvmInline
value class Port /* private constructor */ (val value: Int) {
  companion object : Refined<Int, Port>(::Port, {
    ensure((it in 0..65535) to "$it should be in the closed range of 0..65535 to be a valid port number")
  })
}
```

When attempting to instantiate a Port with invalid values as constants, the plugin fails at compile-time and forces us to correct the input preventing a potential runtime exception.

```kotlin:ank:silent
Port(70000)
// error: "$it should be in the closed range of 0..65535 to be a valid port number"
```

For cases where the input values are dynamic and not evaluable at compile-time, the plugin advises us to use a safe API
based on nullable types.

```kotlin:ank
fun f(n: Int) {
  Port(n)
}
// error: Prefer a safe alternative such as Port.orNull(n) or for explicit use of exceptions `Port.require(n)`
```

The refined type plugin includes a runtime API that can be used without the plugin to validate types:

```kotlin:ank
Port.orNull(5555)
```

```kotlin:ank
Port.orNull(70000)
```

```kotlin:ank
try { Port.require(70000) } catch (e: IllegalArgumentException) { e.message }
```

[Learn more about Refined Types](/apidocs/arrow-refined-types/arrow.refinement/)


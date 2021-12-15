---
layout: docs-analysis
title: Analysis - Fields
permalink: /analysis/fields
---

# Fields

The declaration of pre and post-conditions may not only talk about the value of the arguments, but also reference their properties, fields, and even some of their functions. This is used, for example, in the contract of the indexing operation of a list, in which we refer to its `size`.

```kotlin
import arrow.analysis.pre

class List<T> {
  val size: Int
    get() = TODO() // complicated computation
  
  fun get(index: Int): T {
    pre(index >= 0 && index < this.size) { "index within bounds" }
    // complicated code to get the value
  }
}
```

We use the word **field** to collectively refer to those elements of an argument we are allowed to refer to in a pre- or postcondition, or an invariant of a mutable variable or type. There are two sources for fields:

1. Properties and fields, like `size` above.
2. Instance or extension methods with _no_ arguments, this allows you to use `isNotEmpty()` as a field.

Given the rules above, the following is accepted by Λrrow Analysis:

```kotlin
import arrow.analysis.pre

fun <T> List<T>.first(): T {
  pre(this.isNotEmpty()) { "list should not be empty" }
  return this.get(0)
}
```

## Definition of fields

Actually, if you think about it, the fact that the previous code snippet is accepted is not obvious at all! There must be an additional reasoning step for Λrrow Analysis to understand that is the list is not empty, then calling `get` with `0` as index is allowed, since the precondition for `get` only mentions `size`.

It is very common, though, to have this kind of relationship between properties. Furthermore, many style guidelines suggest to use simpler Boolean predicates like `isNotEmpty()` instead of the longer `size > 0`. To establish this broken link, Λrrow Analysis follows this rule:

> If a field declares **no** preconditions, and a **single** postcondition of the form `{ it == SOMETHING }`, then `SOMETHING` is taken as the **definition** of that field.

The tool then deems each usage of the derived field as being equivalent to its definition. In our case, the `List` class would declare the postcondition in `isNotEmpty`.

```kotlin
import arrow.analysis.post

class List<T> {
  fun isNotEmpty(): Boolean {
    // complicated code
    return something.post({ this.size > 0 }) { "non-emptiness is size > 0" }
  }
}
```

We remark that this definition only applies at the level of Λrrow Analysis. The _implementation_ of `isNotEmpty` is free to use a more performant algorithm. It's during the reasoning stage within the analysis that we make use of the equivalence with `size > 0`.
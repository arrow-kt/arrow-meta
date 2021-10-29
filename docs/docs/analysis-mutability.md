---
layout: docs
title: Analysis - Mutability and loops
---

# Mutability and loops

Λrrow Analysis supports mutability at the level of functions (but not yet at the level of classes). However, when you declare something with `var`, things get tricky. 👻

When a local variable `x` is immutable

```kotlin
val x = COMPLICATED_THING
```

the check knows upfront that any usage of `x` correspond to the same `COMPLICATED_THING`, wherever it's used later on. However, if the declaration uses `var`, at the point in which we use `x` the value inside of it may be completely different from the first one!

The way out of this problem is to promise Λrrow Analysis that some condition over a mutable variable will _always_ be true for the entire computation:

- the check can base its reasoning on this promise,
- but we need to check that you obbey your promise every time you try to give a new value.

We call this promise an **invariant** of the mutable variable.

As a consequence, if you do not declare an invariant, Λrrow Analysis knows nothing about your variable. Here's an example which is "obviously" correct, but not accepted by the tool:

```kotlin
import arrow.analysis.post

fun usesMutability(x: Int): Int {
  var z = 2
  return z.post({ it > 0 }) { "greater than 0" }
}
```
```
e: declaration `usesMutability` fails to satisfy the post-condition: ($result > 0)
```

Let's declare an invariant. We use the same syntax as post-conditions, but use the special function `invariant` instead. Note that even though syntactically the invariant reads as part of the initial value, it really talks about the mutable variable being declared (`z` in this case)
. After attaching this information to the variable, we are free to re-assign the variable, but we need to keep our promise.

```kotlin
import arrow.analysis.invariant
import arrow.analysis.post

fun usesMutability(x: Int): Int {
  var z = 2.invariant({ it > 0 }) { "invariant it > 0" }
  z = 3
  return z.post({ it > 0 }) { "greater than 0" }
}
```

Of course, if we don't keep our promise, Λrrow Analysis won't be happy.

```kotlin
import arrow.analysis.invariant
import arrow.analysis.post

fun usesMutability(x: Int): Int {
  var z = 2.invariant({ it > 0 }) { "invariant it > 0" }
  z = 0 // 0 is not > 0
  return z.post({ it > 0 }) { "greater than 0" }
}
```
```
e: invariants are not satisfied in `z = 0`
```

## Loops

One place in which mutability is hard to avoid is loops. Our recommendation is to think carefully about invariants for your mutable variables, because a good choice will determine what can be checked. Here's an example in which we compute the length of a list using a mutable integral variable:

```kotlin
import arrow.analysis.invariant
import arrow.analysis.post

fun <A> List<A>.count(): Int {
  var count = 0.invariant({ it >= 0 }) { "z >= 0" }
  for (elt in this) { count = count + 1 }
  return count.post({ it >= 0 }) { "result >= 0" }
}
```

We are investigating ways to introduce additional information in the tool, like `for` looping exactly many times as the `size` of the list.
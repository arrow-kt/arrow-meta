---
layout: docs
title: Analysis - Control operators
---

# Control operators

When dealing with pre-conditions, the _environment_ in which a call takes place is very important. You introduce new information in the environment every time you use a control operator like `if` or `when`. For example, the following is accepted by Λrrow Analysis, since you are manually checking whether the `size` of the list if enough for `get`in the right value:

```kotlin
fun <A> List<A>.firstOr(default: A) =
  if (this.size > 0) this.get(0) else default
```

If your check is not strong enough (or wrong altogether), the error message provides additional information about what is known in that branch. For example, suppose you've accidentally switched the order of the branches above:

```kotlin
fun <A> List<A>.firstOr(default: A): A =
  if (this.size > 0) default else this.get(0)
```
```
e: Example.kt: (2, 35): pre-condition `index within bounds` is not satisfied in `get(0)`
  -> unsatisfiable constraint: `((0 >= 0) && (0 < this.size))`
  -> in branch: ( ! this.size > 0), cond17
```

This tells you that at that point you know that the `size` is not greater than 0 (note the `!` at the beginning of the expression). Unfortunately, sometimes "garbage" formulae (such as `cond17`) above appear in the output. We are working on ways to prune these useless constraints, in the time being just ignore them.

The environment is also important when checking post-conditions. Whenever you have some branching operation, like `if` or `when`, the post-condition is checked on **each** branch **separately**, even when the `post` appears as part of a common expression. This allows Λrrow Analysis to accept the following, in which whether the result is non-negative depends on the prior check over `n`.

```kotlin
import arrow.analysis.post

fun absoluteValue(n: Int): Int = when {
  n < 0  -> -n
  n == 0 -> 0
  else   -> n
}.post({ it >= 0 }) { "result >= 0" }
```

## Unreachable code

Being aware of the environment makes Λrrow Analysis able to detect some cases of unreachable code. Here's a simple (but not very useful) example, in which we can guarantee that `1` is never returned because the case `x < 0` cannot arise thanks to the pre-condition.

```kotlin
import arrow.analysis.pre

fun boo(x: Int): Int {
  pre(x > 0) { "x must be positive" }
  return if (x < 0) 1 else 2
}
```
```
e: unreachable code due to conflicting conditions: x < 0, (x == x), (0 == 0), (x < 0 == (x < 0)), (x > 0)
  -> main function body
```

This is another case in which we continue working on pruning useless information from the error messages. But you can see that `x < 0` and `x > 0` appear in the list, and those two expressions together are inconsistent -- that is, there's no way for a value to satisfy both.

Λrrow Analysis detects unreachable code in a best-effort basis. Conflicts may arise not only between pre-conditions and conditionals, but also between several conditionals, and even between the post-conditions of a function and its environment.

## No higher-order support

Λrrow Analysis is not able to propagate information via higher-order functions. For example, if you `map` the `absoluteValue` function defined above over a list of numbers, the knowledge that *each* element of the list is non-negative is not represented within the system. The following is rejected, for example:

```kotlin
import arrow.analysis.post

val okButRejected = listOf(-1).map { absoluteValue(it) }.first()
  .post({ it >= 0 }) { "result is non-negative" }
```

This does not mean that the tool is useless on such operations. In this case, we can still express the fact that the size is maintained by `map`, since that condition does *not* depend on the transformation function. As a result, the following is accepted, since Λrrow Analysis tracks the length of the list through `map`, and can see that the call to `first()` is correct.

```kotlin
val ok = listOf(-1).map { absoluteValue(it) }.first()
```

### Scope functions

There's a handful of higher-order functions which play a significant role in Kotlin programs, the so-called [scope functions](https://kotlinlang.org/docs/scope-functions.html) `let`, `run`, `with`, `apply`, and `also`. Given its importance, Λrrow Analysis ships with special support for them: when used with a lambda as second argument, the tool can "look inside" the body during the checks.

This means you can choose whatever code style suits you best. Λrrow Analysis can handle local variables,

```kotlin
import arrow.analysis.pre
import arrow.analysis.post

fun double(n: Int): Int {
  pre(n > 0) { "n positive" }
  val z = n + n
  val r = z + 1
  return r.post({ it > 0 }) { "result positive" }
}
```

as well as chains of scope functions,

```kotlin
import arrow.analysis.pre
import arrow.analysis.post

fun double2(n: Int): Int {
  pre(n > 0) { "n positive" }
  return (n + n).let { it + 1 }
    .post({ it > 0 }) { "result positive" }
}
```

## Null safety

Λrrow Analysis can reason about `null` values, in a similar way as the [Kotlin compiler does](https://kotlinlang.org/docs/null-safety.html). The Elvis safe call operator `?.` is recognized, and in combination with the aforementioned support for scope functions, the tool can handle idiomatic code such as the following.

```kotlin
import arrow.analysis.pre
import arrow.analysis.post

fun incrementNotNull(x: Int?): Int? {
  pre((x == null) || (x > 0)) { "x is null or positive" }
  val y = x?.let { it + 1 }
  return y.post({ (it == null) || (it > 1) }) { "null or greater than 1" }
}
```
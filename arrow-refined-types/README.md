# Module arrow-refined-types

# Package arrow.refinement

A refined type is any regular type constrained by predicates expected to hold in all possible values of the type's
constructor.

The Arrow refined-types plugin verifies at compile and runtime refined types constructor calls, ensuring values in
declared types truly represent the programmers intent.

## Setup.

The refined-types plugin is a Kotlin compiler plugin that can be enabled via Gradle in your build by adding it to the
list of your Gradle plugins.

## Issues modeling with primitives types.

Leveraging programming languages built-in types like `String` and `Int` is convenient and allows us to express a wide
range of domain problems. Unfortunately, types like `String` or `Int` accept a broader range of values than desirable in
our programs and functions. By choosing overly broad types for our models like `Int` or `String`, we move the
responsibility of validating the value to each call site.

Consider the following type that attempts to model a machine port number using the type `Int.`

```kotlin:ank
val x: Int = 5555
```

While there does not seem to be anything wrong at first sight with this definition, simply using `Int` does not prevent
invalid out-of-range inputs of what's considered a valid port number.

```kotlin:ank
val x: Int = 70000
```

Because the valid range for port numbers is `0..65353`, the value `70000` is incorrect as it would be other negative
values that `Int` accepts. In this case, the best we can do is create a new type and require in its initialization that
input arguments get validated before construction.

```kotlin:ank
@JvmInline
value class Port(val value: Int) {
  init {
    require(value in 0..65353) { "$value should be in range of 0..65353 " }
  }
}
```

Adding these requirements to `init` static blocks guarantees `Port` instances are not constructed but invoking the
constructor with an invalid input will result in an `IllegalArgumentException` thrown at runtime.
Throwing exceptions imposes the burden in the call site to preemptively `try/catch` all constructor calls or crashing the program at runtime.

```kotlin:ank
try { 
  Port(70000) 
} catch (e: IllegalArgumentException) { 
  e.message
}
```

Runtime exceptions are what most programming languages offer as the solution to validating constructor arguments. Since
the `init` static initialization blocks can't produce values and only accept statements, we can only resort to
throwing `IllegalArgumentException` if we encounter a case where the value is considered invalid.

## Refining custom types.

The refined-types plugin monitors all calls to Refined type constructors, ensuring arguments provided are verifiable in
the range of the declared predicates constraining the type.

Considering the example above, instead of using `Int` to describe a port number, we will create our own `Port` type and
enable the refined-types capabilities by making the `Port` companion extend the `Refined` class.

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

In the declaration above the `Refined` class takes as first argument a function `(Int) -> Port` with value `::Port`.
It's important to make constructors `private` so they are not visible and instead handled by the `Refined<Int, Port>`
implementation.

When attempting to instantiate a Port with invalid values as constants, the Refined Types compiler plugin fails before
our program can compile and forces us to correct the input preventing a potential runtime exception.

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

By implementing the `Refined` interface we gain access to the following validation APIs.

## Safe nullable construction.

```kotlin:ank
Port.orNull(70000)
```

```kotlin:ank
Port.orNull(5555)
```

## Resolved constraints.

```kotlin:ank
Port.constraints(70000)
```

```kotlin:ank
Port.constraints(5555)
```

```kotlin:ank
Port.isValid(70000)
```

 ```kotlin:ank
 Port.isValid(5555)
 ```

## Fold validation.

```kotlin:ank
Port.fold(70000, { "failed: $it" }, { "success: $it" })
```

```kotlin:ank
Port.fold(5555, { "failed: $it" }, { "success: $it" })
```

## Unsafe require.

```kotlin:ank
Port.require(5555)
```

```kotlin:ank
try {
  Port.require(70000)
} catch (e: IllegalArgumentException) { 
  e.message
}
```

## Refined types library.

The refined-types plugin includes a runtime library that also works standalone independently of the compiler plugin and
includes useful predicates for [chars](/booleans/arrow-refined-types/arrow.refinement.booleans/), [chars](/apidocs/arrow-refined-types/arrow.refinement.chars/), [strings](/apidocs/arrow-refined-types/arrow.refinement.strings/), [collections](/apidocs/arrow-refined-types/arrow.refinement.collections/), [digests](/apidocs/arrow-refined-types/arrow.refinement.digests/), [network](/apidocs/arrow-refined-types/arrow.refinement.network/), and [time](/apidocs/arrow-refined-types/arrow.refinement.time/) that can be composed and used alongside user type
definitions.

## Credits.

The refined-types plugin is inspired by libraries like [scala's refined](https://github.com/fthomas/refined)
, [Haskell's refined](https://github.com/nikita-volkov/refined)
and [Liquid Haskell](https://ucsd-progsys.github.io/liquidhaskell-blog/).

In contrast to a type-level implementation as we find in these libraries in Scala and Haskell, the refined-types plugin
takes a different approach and leverages the Kotlin compiler APIs and its ability to interpret IR code to offer similar
compile-time guarantees.

[Refined Types were originally introduced in ML by Freeman and Pfenning](https://www.cs.cmu.edu/afs/cs.cmu.edu/user/fp/www/papers/pldi91.pdf)

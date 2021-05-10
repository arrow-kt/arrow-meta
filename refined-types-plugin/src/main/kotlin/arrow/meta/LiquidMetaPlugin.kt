package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.liquidExpressions
import kotlin.contracts.ExperimentalContracts

/**

# Refined Types for Kotlin

The refined types plugin enables precise domain modeling by ensuring data type construction is constrained by a list of predicates verified at compile and runtime.

# Issues modeling with primitives and builtin types

Leveraging programming languages built-in types like `String,` `Int,` or collections is handy and allows us to express a wide range of domain problems with them. Unfortunately, types like `String` or `Int` accept a broader range of values than desirable in our programs and functions.

Consider the following type that attempts to model a machine port number using the type `Int.`

```kotlin
val port = 5555
```

While there does not seem to be anything wrong at first sight with this definition,  we can observe in the following expression that such modeling accepts invalid inputs that are out of the bounds of what we would consider a valid port number.

```kotlin
val port = 70000
```

Because the valid range for port numbers is `0 to 65353` the value `70000` is incorrect as it would be other negative values that `Int` accepts.
In this case, the best we can do is create a new type and require initialization of our type to ensure input arguments get validated before construction.

```kotlin
value class Port(val value: Int) {
init {
require(value in (0 to 65353))
}
}
```

Adding these requirements to `init` static blocks guarantees `Port` instances are not constructed but invoking the constructor with an invalid input will result in an `IllegalArgumentException` thrown at runtime.

```kotlin
val port = Port(70000) // throws at runtime
```

The refined types plugin improves verification of predicates at compile and runtime by providing an easy API to describe constraints over types.

```groovy
TODO() insert plugin dep
```

Considering the example above, instead of using `Int` to describe a port number, we will roll our own `Port` type to preserve the initialization semantics and provide a safe API for compile and runtime validation by making the `Port` companion extend the `Refined` class.

```kotlin
value class Port private constructor(val value: Int) {
companion object : Refined<Int, Port>(::Port, {
ensure((value in (0 to 65353)) to "Found $it but expected value between 0 to 65353")
})
}
```

When attempting to instantiate now a Port with invalid values as constants, the Arrow Refined Types compiler plugin fails before our program can compile and forces us to correct the input preventing a potential runtime exception.

```kotlin
val port = Port(70000)
// compile-time error: Found 70000 but expected value between 0 to 65353
```

For cases where the input values are dynamic and not evaluable at compile-time, the plugin advises us to use a safe API based on nullable types.

```kotlin
val port = Port(n)
// compile-time error: prefer Port.orNull(n) or use explicit throwing Port.require(n)
```

 */
open class LiquidMetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      liquidExpressions
    )
}

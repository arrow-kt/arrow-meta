# Pattern Matching Plugin

This plugin provides pattern matching capabilities to Kotlin.

## Topics

- [Build Instructions](#build-instructions)
- [Pattern Matching Design & Implementation](#pattern-matching-design--implementation)

## Build Instructions

1. You'll need Gradle and IntelliJ. Especially if you're contributing to this plugin, you'll want to ensure IntelliJ resolves the pattern matching syntax that is otherwise invalid in Kotlin.
1. You'll need JDK 8 to compile Arrow Meta.

Build and run tests:

```shell
./gradlew clean :compiler-plugin:build
```

Build without running tests:

```shell
./gradlew clean :compiler-plugin:build -x tests
```

Build and run the pattern matching tests explicitly:

```shell
./gradlew clean :compiler-plugin:test --tests "*PatternMatching*"
```

## Pattern Matching Design & Implementation

The entry point for the plugin: [PatternMatchingPlugin.kt](compiler-plugin/src/main/kotlin/arrow/meta/plugins/patternMatching/PatternMatchingPlugin.kt).

To enable the plugin, add it to the list of plugins in: [MetaPlugin.kt](compiler-plugin/src/main/kotlin/arrow/meta/MetaPlugin.kt)

Pattern matching is not supported by the current Kotlin compiler. However, Arrow Meta allows us to modify the compiler behavior so we can transform invalid code to something the Kotlin compiler can understand. This is done by transforming the tree structures in the compiler.

This plugin works by:

1. Finding the sections of the tree representing the invalid code.
1. Rewriting those invalid sections into code that the compiler understands. This process is called "desugaring".

While it is possible to have the Kotlin CLI understand this process via the Meta Quotes system, that is not sufficient for IntelliJ. Consequently, we have to do this in the _analysis phase_. Once the analysis phase has been properly accounted for, IntelliJ will understand that the pattern match expressions are valid, based on the rules provided in this plugin.

### Analysis phase

The analysis phase is where we get the `_` coming through with a `null` type. In analysis phase we are able to alter the type for `_`. We do this with `BindingTrace.record`:

```kotlin
bindingTrace.record(BindingContext.EXPRESSION_TYPE_INFO, underscoreTypeInfo.key, replacementTypeInfo)
````

where the `underscoreTypeInfo.key` is the `KtExpression` representing `_` and `replacementTypeInfo` is the new `KotlinTypeInfo` to replace the `null` type info originally assigned to `_`.

### IR Codegen Phase


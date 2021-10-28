---
layout: docs
title: Analysis - 3rd-party libraries
---

# Integration with 3rd-party libraries

Is a library you use not compiled with Λrrow Analysis? I could tell you to open an issue in their repository and convince their authors, but this is not always possible. For those cases Λrrow Analysis provides a way to declare pre- and postconditions separately from the implementation, using `@Law` annotations. In fact, this is the way we package the analysis information related to Kotlin's `stdlib`.

It might be the case that another 4rd-party provides the Λrrow Analysis information for the 3rd-party library. In that case, just include **both** in your project. Oftentimes the Λrrow Analysis information can be included as a `compileOnly` dependency, since it's not used at run-time. You do **not** have to `import` anything extra, just use the 3rd-party library as usual. 

## Attaching laws

To attach information about a 3rd-party function you declare a function somewhere else (it doesn't matter where), and annotate it with `@Law`. Its body must follow a very restrictive form: it should only contain `pre` and `post`, and a **single** call to the 3rd-party function with the arguments in the **same order** they appeared in the original definition.

For example, this is the way to declare that an empty list has size 0:

```kotlin
@Law inline fun <T> emptyListLaw(): List<T> =
  emptyList<T>().post({ it.size == 0 }) { "empty list has size 0" }
```

As a practical tip, we find useful to mark the function as `inline`. That way you avoid the case in which you call the law instead of the function you want to decorate in the body, since inline functions cannot be recursive.

### Organizing laws

If you want to define pre and postconditions for an entire library, it's often useful to organize those by package or type. To help with this task, you can define several laws inside an `object` marked with the `@Laws` annotation (notice the final `s`.)

```kotlin
@Laws object ListLaws {
  @Law inline fun <T> emptyListLaw(): List<T> =
    emptyList<T>().post({ it.size == 0 }) { "empty list has size 0" }
  @Law inline fun <T> getLaw(index: Int): T {
    pre(index >= 0 && index < this.size) { "index within bounds" }
    return get(index)
  }
  // ... and many more!
}
```

Note that each function must still be marked with `@Law`.
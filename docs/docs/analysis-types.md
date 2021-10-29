---
layout: docs
title: Analysis - Types and invariants
---

# Types and invariants

Classes and interfaces (which we shall collectively refer to as "types") play a crucial role in organizing data in Kotlin. Λrrow Analysis builds upon this idea, giving users the ability to attach Boolean expressions related to the data contained in a class. We call those _invariants_ of the type, since the tool enforces those expressions to be true throughout the whole program.

The easiest way to introduce invariants is using an `init` block with calls to `require` inside. Those initializers are run regardless of the constructor used to instantiate the class, so they provide the most compact way to declare the invariants.

```kotlin
class Positive(val value: Int) {
  init { require(value > 0) }
}
```

The invariant has dual roles with respect to the type. When you _instantiate_ the class, the invariant becomes a _pre-condition_. In the example above, to create a `Positive` object, the argument passed as `value` must be greater than 0.

```kotlin
val positiveExample = Positive(-1)
```

In any other situation, using an instance of a type brings the invariant into the _environment_. That means that the check can use that additonal information in its reasoning. For example, the analysis knows that from the fact that both `this` and `other` are instances of `Positive`, both `this.value` and `other.value` are positive; hence its addition is positive and the call to the `Positive` constructor is accepted.

```kotlin
fun Positive.add(other: Positive) = Positive(this.value + other.value)
```

In the above case we are using an extension method, but the same applies to functions declared within the class.

```kotlin
class Positive(val value: Int) {
  init { require(value > 0) }

  operator fun plus(other: Positive) = Positive(this.value + other.value)
}
```

Remember that `init` blocks apply to _every_ constructor. Λrrow Analysis checks that when you delegate to another constructor, the invariants are still respected. For example, the following is rejected, since zero does not serve as a correct `value`.

```kotlin
class Positive(val value: Int) {
  init { require(value > 0) }
  
  constructor() : this(0) { }
}
```

## Initializers with `pre` and `post`

Whereas `init` blocks are the preferred way to declare invariants, Λrrow Analysis provides a more fine-grained approach which can be useful in some cases. Remember that an invariant plays two roles: you need them to be true at instantiation time, and you can count on them at usage time; you can decide for an invariant to have only the first role by using `pre` in an initializer or constructor, and to have only the latter role by using `post`. For example, the definition of `Positive` above could be rewritten as:

```kotlin
import arrow.analysis.pre
import arrow.analysis.post

class Positive(val value: Int) {
  init { 
    pre(value > 0) { "value must be positive" }
    post({ this.value > 0 }) { "value is positive" }
  }
}
```

The most common scenario for this split is when a secondary constructor enforces additional checks on input arguments. In that case `pre` is used within the constructor.

## Inheritance

Classes and interfaces do not live in the vacuum, in fact they often go into relationships with each other. This brings up the question of the relation of pre and post-conditions between parent and subclasses. To understand how Λrrow Analysis approaches this problem, we need to look at the [Liskov Substitution Principle](https://en.wikipedia.org/wiki/Liskov_substitution_principle), which roughly states:

> If `B` is a subclass of `A`, then we should be able to use `B` (and any of its methods) **anywhere** we use `A`

This translates into the following two guidelines for the case in which `B` overrides a method `m` from class `A`:

1. The pre-conditions on `B.m` must be **weaker** than (or equivalent to) those of `A.m`. This ensures that whenever we were calling `A.m` we call `B.m` instead.
2. The post-conditions offered by `B.m` must be **stronger** than (or equivalent to) those of `A.m`. This ensures that any reasoning that relies on `A.m` still works when using `B.m`.

Let's look at a concrete example, and understand why it is rejected.

```kotlin
import arrow.analysis.post

open class A() {
  open fun f(): Int = 2.post({ it > 0 }) { "greater than 0" }
}

class B(): A() {
  override fun f(): Int = 1.post({ it >= 0 }) { "non-negative" }
}
```

None of `A.f` nor `B.f` declare a pre-condition, so in particular they are equivalent. In the post-condition front, `A.f` declares that the result must be _strictly_ greater than 0, whereas `B.f` also allows `0` in its post-condition. That means that the post-condition of `B.f` is weaker than that of `A.f`, and the code is rejected.

```
e: post-condition `greater than 0` from overridden member is not satisfied
```

### Implicit pre and post-conditions

Whenever a method in a type declares pre and post-conditions, but an overriden member does declare any, they are _implicitly_ inherited. This allows us to introduce a contract in a parent class or interface, and ensure that all their children satisfy that rule.

This slight modification of the example above is also rejected. In this case the post-condition `result > 0` is implicitly inherited by `B.f`. However, the result value computed in its body, `0`, does not satisfy that post-condition.

```kotlin
import arrow.analysis.post

open class A() {
  open fun f(): Int = 2.post({ it > 0 }) { "greater than 0" }
}

class B(): A() {
  override fun f(): Int = 0
}
```

There are exceptions to this implicit inheritance: initializer blocks and constructors. That means that invariants that hold in the parent class must be guaranteed in the subclasses, in the most extreme case by repeating those invariants.

### Post-condition or invariant?

When designing a hierarchy of classes, we often need to decide whether we attach information about a property as an invariant of each class, or as a post-condition of the property. For example, this is another way in which we could have declared our `Positive` class.

```kotlin
import arrow.analysis.post

class Positive(private n: Int) {
  init { pre(n > 0) }
  
  val value: Int = n.post({ it > 0 }) { "value is positive" }
}
```

The differences between both ways are:

- Invariants become available whenever we have an instance of that type, whereas post-conditions are only brought into consideration when we _use_ the property somewhere in the code. On that respect, invariants are slightly more powerful than post-conditions.
- Post-conditions are _inherited_, but invariants are not. As a consequence, if the property has some contract as part of the hierarchy, it should be declared as post-condition.

## Interfaces and abstract members

We have mentioned that by using pre and postconditions we have enforce a particular contract on a hierarchy of types. This holds also for `interfaces`, but we need to use a different way to attach those pre- and postconditions, since abstract members don't have a body where we can include `pre` and `post` blocks. The solution is to use the `@Law` annotation (you can learn more about it in the section about integration with 3rd-party libraries.)

Following our example, this is how we would declare `A` as an interface while keeping the promise of `f` always returning a positive number. We add an additional member marked with the `@Law` annotation, and whose body consists **only** of `pre` and `post` blocks and a call to the function we want to decorate (the name is irrelevant, but we often use `method_Law` or something similar.)

```kotlin
import arrow.analysis.Law
import arrow.analysis.post

interface A {
  fun f(): Int

  @Law fun f_Law(): Int =
    f().post({ it > 0 }) { "greater than 0" }
}
```

## Enumerations

Everything we have described above holds without change for enumerations. However, note the special syntax you need to follow with `enum class` in Kotlin: you first must introduce all the cases, then write a semicolon (`;`), and only then you are allowed to write an `init` block.

```kotlin
enum class Color(val rgb: Int) {
  RED(0xFF0000),
  GREEN(0x00FF00),
  BLUE(0x0000FF); // <-- the semicolon!

  init {
    require(rgb != 0) { "no zero color" }
  }
}
```
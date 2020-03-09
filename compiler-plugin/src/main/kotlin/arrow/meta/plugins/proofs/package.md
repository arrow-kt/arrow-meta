# Arrow Compiler Plugin

## Type Proofs

Type Proofs is a compiler plugin that interfaces with the Kotlin type system to unlock new powerful features such as 
type classes, ad-hoc polymorphism, refined types, product types, inductive derivation and union types.

## Type Classes

Type classes are interface abstractions that define a set of extension functions associated to one type. 
Type classes allow replacing inheritance for composition enabling compile time dependency injection in Kotlin.

### Getting started

Consider the following type class `Repository<A>` which is in charge of persistence operations in a given system

```kotlin
inline class Id(val value: String)

interface Repository<A> {
  fun load(id: Id): A
  interface Syntax<A> {
    val value: A
    suspend fun save(): Unit
  }
}
``` 

In this case `A` is a generic argument that can target any data model or class.

Now we will use the data type `User` in place of `A` and we will provide proof of ad-hoc conformance to the `Repository` contract:

```kotlin
data class User(val id: Id, val name: String) { //User does not need to directly extend Repository<User>
  companion object    
}
```

The `@Proof(Extension)` below activates all members of `Repository<User>` over the `User` companion

```kotlin
object UserRepository : Repository<User> {
  override fun load(id: Id): User = User(id, "Curry")
}

@Proof(Extension)
fun User.Companion.repository(): Repository<User> =
  UserRepository

User.load(Id("Curry")) //load is proofed by `repository`
//User(Id("Curry"), "Curry")
```

We can also project all members of `Repository.Syntax<User>` over values of `User`

```kotlin
inline class UserRepositorySyntax(override val value: User): Repository.Syntax<User> {
  override suspend fun save(): Unit = println("$value stored!")
}

@Proof(Extension)
fun User.syntax(): Repository.Syntax<User> =
  UserRepositorySyntax(this)

suspend fun main() {
  val curry = User.load(Id("Curry"))
  curry.save()// save is resolved by proof
}
```

In type classes we make the differentiation between constructors such as `load` that are placed in companion objects and
syntax functions that operate over existing values of the type, in this case `save` which operates over a `User` value.

Type Class proofs injection can be used at compile time as a Dependency Injection mechanism to verify a program graph
of typed dependencies are correct. In the program above the compiler can proof a `Repository.Syntax<User>` exists.
If we had not provided the `@Proof(Extension)` for `syntax` `curry.save()` would have not compiled.

No inheritance needed when you have a `@Proof` between two types to inherit their members.

### Injecting behaviors with `@given`

Type classes work with the standard kotlin constrains in `:` and `where` blocks.

Consider the following polymorphic function where `A : Repository<A>`. 

```kotlin
import arrow.given

suspend fun <A : @given Repository.Syntax<A>> List<A>.saveAndLog(): Unit =
  elements.foreach { it.save(); println("thanks for your service: $it") }
```

The compiler will inject the proof that `User` provides a `Repository.Syntax<User>` when
in the code below we access `saveAndLog()`.

When instead of using `User` values we use `Int` the compiler will report that as an error since it lacks a proof that
a `Repository.Syntax<Int>` exists

```kotlin
suspend fun main() {
  val curry = User.load(Id("Curry"))
  val howard = User.load(Id("Howard"))
  val lambek = User.load(Id("Lambek"))
  listOf(curry, howard, lambek).saveAndLog() //infers Repository<User> by proof
  listOf(1, 2, 3).saveAndLog() //does not compile because there is no proof of Repository<Int>
}
```

Type Classes are at the corner stone of functional design as they are a great tool to separate data from behaviors.

Type Classes free us from inheritance and provide an alternative where we can extend any type ad hoc with new behaviors including platform types
such as `String`, `Int`, etc that otherwise are declared final.

Functional Programming largely benefits from type classes and promotes programs described in terms of algebraic data types and type classes 
that help toward the goal of composing pure and referentially transparent programs.

## Tuples (Products)

Arrow support 

## Unions (CoProducts)
## Higher Kinded Types
## Refined types
### The Curry-Howard correspondence

These strong inheritance relationships as proposed in Kotlin using `A : B` to express that `A` is a subtype of `B` have 
a correspondence with Logic in terms of `A implies B`. The Curry-Howard correspondence shows there is a relationship between logic and computer programs.
These same logic proposition to the Kotlin type system can be instead expressed with a simple function `(A) -> B` favoring composition over inheritance and enabling
ad-hoc extensions of any type without resorting to inheritance. 

Type Proofs makes the compiler and its type system aware of types as propositions and programs as proofs allowing users to draw functions
from `(A) -> B` where otherwise an inheritance relationship like `A : B` would have been required.

Type Proofs builds upon these notions providing new rich type system features and abstractions for Kotlin:

| Proofs    | Type | Programs |                                                                     
|-----------|-------------|-------------|
| Type Classes | A implies B | `(A) -> B` | @Proof A function from `A` to `B`|
| Product Types | A and B | `Tuple22<A, B,...>` | The product of `A` and `B` and... |
| Union Types | A or B | `Union22<A, B...>` | Either `A` or `B` or ... |
| Higher Kinded Types | `F<A>` implies `Kind<F, A>` | `Kind22<F, A> <-> F<A>` | Auto kinds
| Refined Types | if `A` then `B` | `Refined<A> -> B` | Refined predicates restrict constants and runtime calls

## Road Map 


package arrow

/**
 * - Type Proofs
 *  - Introduction
 *    - Getting started
 *    - The Curry–Howard–Lambek correspondence
 *  - Strategies
 *    - Extension
 *    - Refinement
 *    - Subtyping
 *    - Negation
 *    - Inductive Derivation
 *  - Arrow Plugins
 *   - Union Types
 *    - Getting started
 *    - Associative
 *    - Commutative
 *    - Union Vs Either Vs Result
 *   - Tuples
 *   - Type Classes
 *    - Getting started
 *    - Ad-Hoc Polymorphism
 *    - Resolution
 *    - Constructors
 *    - Member Extensions
 *    - Overloading operators
 *   - Higher Kinded Types
 *    - Extending 3rd party data types
 *    - Polymorphic Functions
 *    - Combining Higher Kinds with Type Classes
 *   - Refined Types
 *    - Getting started
 *    - Validation
 *    - Types Vs Tests
 *    - Tips for Precise Algebraic Modeling
 *   - Monad Comprehensions
 *    - Getting started
 *    - Supported data types
 *      - Arrow Fx
 *      - Arrow Core
 *      - KotlinX Coroutines
 *      - Project Reactor
 *      - Rx J
 *    - Building comprehension capable data types
 *   - Optics
 *    - Getting started
 *    - The Optics DSL
 *     - Lenses
 *     - Prism
 *   - Recursion schemes
 *     - Fold Derivation
 *     - Algebras and Co-Algebras
 *   - Suspended IO
 *   - Generic Derivation
 *  - Language Compatibility
 *
 *
 *
 *
 * A [Proof] establishes a relationship of trust between an extension function and the Kotlin compiler.
 *
 * [TypeProof]'s can establish [Extension] relationship between types, enhance the member scope of a type with [Extension]'s or
 * perform type [Refinement] among other constrains.
 */
enum class TypeProof {
  /**
   * ```kotlin:ank:silent
   * import arrow.Proof
   * import arrow.TypeProof
   *
   * inline class PositiveInt(val value: Int)
   *
   * @Proof(of = [Subtyping])
   * fun PositiveInt.toInt(): Int = value
   *
   * @Proof(of = [Subtyping])
   * fun Int.toPositiveInt(): PositiveInt? =
   * ```
   */
  Extension,
  Refinement,
  Negation
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Proof(
  val of: TypeProof
)

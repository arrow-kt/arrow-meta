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
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.TYPE
)
@MustBeDocumented
annotation class Given

val given: Nothing
  get() = TODO("Should have been replaced by Arrow Proofs Compiler Plugin provided by [plugins { id 'io.arrow-kt.proofs' version 'x.x.x' }")

fun <A> given(evidence: @Given A = arrow.given): A = evidence

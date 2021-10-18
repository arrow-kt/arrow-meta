@file:Suppress("NOTHING_TO_INLINE")

package arrow.analysis

inline fun pre(predicate: Boolean, msg: () -> String): Unit =
  require(predicate) { msg() }

inline fun <A> A.post(predicate: (A) -> Boolean, msg: () -> String): A {
  require(predicate(this)) { msg() }
  return this
}

inline fun <A> A.invariant(predicate: (A) -> Boolean, msg: () -> String): A {
  require(predicate(this)) { msg() }
  return this
}

@Target(
  AnnotationTarget.FUNCTION
)
annotation class Pre(val messages: Array<String>, val formulae: Array<String>, val dependencies: Array<String>)

@Target(
  AnnotationTarget.FUNCTION
)
annotation class Post(val messages: Array<String>, val formulae: Array<String>, val dependencies: Array<String>)

/**
 * Annotation to flag ad-hoc refinements over third party functions
 */
@Target(
  AnnotationTarget.FUNCTION
)
annotation class Law

/**
 * Annotation to flag ad-hoc refinements over third party functions
 */
@Target(
  AnnotationTarget.FUNCTION
)
annotation class Subject(val fqName: String)

/**
 * This is used to mark an object as containing
 * only laws. This way you do not have to write
 * the annotation on every element, and you
 * can group several of them together.
 *
 * ```
 * @Laws
 * object IntLaws {
 *   fun Int.plusLaw { ... }
 * }
 * ```
 */
@Target(
  AnnotationTarget.CLASS
)
annotation class Laws

/**
 * This is used internally to speed up the search for laws
 * Each package with any law should include this annotation
 * in a submodule of 'arrow.analysis.hints'
 */
@Target(
  AnnotationTarget.FILE
)
annotation class DeclaresLawsIn(val packages: Array<String>)

/**
 * Indicates that the preconditions for a call
 * should not be checked.
 */
inline fun <A> unsafeCall(call: A): A = call

/**
 * Indicates that nothing in this block
 * should be checked.
 */
inline fun <A> unsafeBlock(block: () -> A) = block()

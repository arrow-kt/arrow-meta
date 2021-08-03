package arrow.refinement

import kotlin.reflect.KFunction

inline fun pre(msg: String, predicate: () -> Boolean): Unit =
  require(predicate()) { msg }

inline fun <A> A.post(msg: String, predicate: (A) -> Boolean): A {
  require(predicate(this)) { msg }
  return this
}

@Target(
  AnnotationTarget.FUNCTION
)
annotation class Pre(val formulae: Array<String>)

@Target(
  AnnotationTarget.FUNCTION
)
annotation class Post(val formulae: Array<String>)

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
package arrow.refinement

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

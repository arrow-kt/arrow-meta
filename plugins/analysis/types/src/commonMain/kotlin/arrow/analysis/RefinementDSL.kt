@file:Suppress("NOTHING_TO_INLINE")

package arrow.analysis

public fun interface Messager {
  public operator fun invoke(): String
}

public fun interface Predicate<A> {
  public operator fun invoke(value: A): Boolean
}

public inline fun pre(predicate: Boolean, msg: Messager): Unit = require(predicate) { msg() }

public inline fun doNotLookAtArgumentsWhen(predicate: Boolean, msg: Messager): Unit = Unit

public inline fun <A> A.post(predicate: Predicate<A>, msg: Messager): A {
  require(predicate(this)) { msg() }
  return this
}

public inline fun <A> post(predicate: Predicate<A>, msg: Messager): Unit {}

public inline fun <A> A.invariant(predicate: Predicate<A>, msg: Messager): A {
  require(predicate(this)) { msg() }
  return this
}

@Target(AnnotationTarget.FUNCTION)
public annotation class Pre(
  val messages: Array<String>,
  val formulae: Array<String>,
  val dependencies: Array<String>
)

@Target(AnnotationTarget.FUNCTION)
public annotation class Post(
  val messages: Array<String>,
  val formulae: Array<String>,
  val dependencies: Array<String>
)

@Target(AnnotationTarget.FUNCTION)
public annotation class DoNotLookAtArguments(
  val messages: Array<String>,
  val formulae: Array<String>,
  val dependencies: Array<String>
)

/** Annotation to flag ad-hoc refinements over third party functions */
@Target(AnnotationTarget.FUNCTION) public annotation class Law

/** Annotation to flag ad-hoc refinements over third party functions */
@Target(AnnotationTarget.FUNCTION) public annotation class Subject(val fqName: String)

/**
 * This is used to mark an object as containing only laws. This way you do not have to write the
 * annotation on every element, and you can group several of them together.
 *
 * ```
 * @Laws
 * object IntLaws {
 *   fun Int.plusLaw { ... }
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS) public annotation class Laws

/**
 * This is used internally to speed up the search for laws Each package with any law should include
 * this annotation in a class of module 'arrow.analysis.hints'
 */
@Target(AnnotationTarget.CLASS)
public annotation class PackagesWithLaws(val packages: Array<String>)

/** Indicates that the preconditions for a call should not be checked. */
public inline fun <A> unsafeCall(call: A): A = call

/** Indicates that nothing in this block should be checked. */
public inline fun <A> unsafeBlock(block: () -> A): A = block()

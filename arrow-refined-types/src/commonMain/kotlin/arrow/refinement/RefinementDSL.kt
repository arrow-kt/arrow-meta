package arrow.refinement

inline fun pre(msg: String, predicate: () -> Boolean): Unit =
  require(predicate()) { msg }

inline fun <A> A.post(msg: String, predicate: (A) -> Boolean): A {
  require(predicate(this)) { msg }
  return this
}
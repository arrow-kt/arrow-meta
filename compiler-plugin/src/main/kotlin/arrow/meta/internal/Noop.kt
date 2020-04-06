package arrow.meta.internal

object Noop {
  val effect0: () -> Unit = {}
  val effect1: (Any?) -> Unit = { _ -> Unit }
  val effect2: (Any?, Any?) -> Unit = { _, _ -> Unit }
  val effect3: (Any?, Any?, Any?) -> Unit = { _, _, _ -> Unit }
  val effect4: (Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _ -> Unit }
  val effect5: (Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _ -> Unit }
  val effect6: (Any?, Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _, _ -> Unit }
  fun <A> nullable1(): (Any?) -> A? = { null }
  fun <A> nullable2(): (Any?, Any?) -> A? = { _, _ -> null }
  fun <A> nullable3(): (Any?, Any?, Any?) -> A? = { _, _, _ -> null }
  fun <A> nullable4(): (Any?, Any?, Any?, Any?) -> A? = { _, _, _, _ -> null }
  fun <A> nullable5(): (Any?, Any?, Any?, Any?, Any?) -> A? = { _, _, _, _, _ -> null }
  fun <A> nullable6(): (Any?, Any?, Any?, Any?, Any?, Any?) -> A? = { _, _, _, _, _, _ -> null }
  fun <A> nullable7(): (Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> A? = { _, _, _, _, _, _, _ -> null }
  fun <A> emptyCollection1(): (Any?) -> Collection<A> = { emptyList() }
  fun <A> emptyList1(): (Any?) -> List<A> = { emptyList() }
  fun <A> emptyCollection2(): (Any?, Any?) -> Collection<A> = { _, _ -> emptyList() }
  fun <A> emptyList2(): (Any?, Any?) -> List<A> = { _, _ -> emptyList() }
  fun <A> emptyCollection3(): (Any?, Any?, Any?) -> Collection<A> = { _, _, _ -> emptyList() }
  fun <A> emptyCollection4(): (Any?, Any?, Any?, Any?) -> Collection<A> = { _, _, _, _ -> emptyList() }
  fun <A> emptyCollection5(): (Any?, Any?, Any?, Any?, Any?) -> Collection<A> = { _, _, _, _, _ -> emptyList() }
  val boolean1True: (Any?) -> Boolean = { _ -> true }
  val boolean1False: (Any?) -> Boolean = { _ -> false }
  val boolean2True: (Any?, Any?) -> Boolean = { _, _ -> true }
  val boolean2False: (Any?, Any?) -> Boolean = { _, _ -> false }
  val boolean3True: (Any?, Any?, Any?) -> Boolean = { _, _, _ -> true }
  fun <A> string1(): (A) -> String = { _ -> "" }
  fun <A, B> string2(): (A, B) -> String = { _, _ -> "" }
  fun <A, B, C> string3(): (A, B, C) -> String = { _, _, _ -> "" }
}

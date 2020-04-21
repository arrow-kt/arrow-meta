package arrow.meta.smt.util

fun <A> orNull(a: () -> A): A? =
  try {
    a()
  } catch (e: Exception) {
    null
  }
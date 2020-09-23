package arrow.meta.internal

// copied from stdlib without A : Any

fun <T> Iterable<T?>.filterNotNull(): List<T> =
  filterNotNullTo(ArrayList())

fun <C : MutableCollection<in T>, T> Iterable<T?>.filterNotNullTo(destination: C): C {
  for (element in this) if (element != null) destination.add(element)
  return destination
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(destination: C, transform: (T) -> R?): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <T, R> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.mapNotNullTo(destination: C, transform: (Map.Entry<K, V>) -> R?): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <K, V, R> Map<out K, V>.mapNotNull(transform: (Map.Entry<K, V>) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

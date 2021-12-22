// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.DoesNothingOnEmptyCollection
import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post
import arrow.analysis.pre

// based on core/builtins/native/kotlin/Collections.kt
//      and libraries/stdlib/src/kotlin/collections/*.kt
//      and libraries/stdlib/common/src/generated/_Collections.kt
// remember that laws are inherited by subclasses

@Laws
object CollectionLaws {
  @Law
  inline fun <E> Collection<E>.sizeLaw(): Int = size.post({ it >= 0 }) { "size is non-negative" }
  @Law
  inline fun <E> Collection<E>.countLaw(): Int =
    count().post({ it == this.size }) { "count is size" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.countLaw(predicate: (E) -> Boolean): Int =
    count(predicate).post({ it <= this.size }) { "count bounded by size" }

  @Law
  inline fun <E> Collection<E>.isEmptyLaw(): Boolean =
    isEmpty().post({ it == (size <= 0) }) { "empty when size is 0" }
  @Law
  inline fun <E> Collection<E>.noneLaw(): Boolean =
    none().post({ it == (size <= 0) }) { "none when size is 0" }
  @Law
  inline fun <E> Collection<E>.isNotEmptyLaw(): Boolean =
    isNotEmpty().post({ it == (size > 0) }) { "not empty when size is > 0" }
  @Law
  inline fun <E> Collection<E>?.isNullOrEmptyLaw(): Boolean =
    isNullOrEmpty().post({ it == ((this == null) || (this.size <= 0)) }) {
      "either null or size is 0"
    }
  @Law
  inline fun <E> Collection<E>?.orEmptyLaw(): Collection<E> =
    orEmpty().post({ if (this == null) (it.size == 0) else (it.size == this.size) }) {
      "returns empty when this is null"
    }

  @Law
  inline fun <E> Collection<E>.elementAtLaw(index: Int): E {
    pre(index >= 0 && index < size) { "index within bounds" }
    return elementAt(index)
  }
  @Law
  inline fun <E> Collection<E>.elementAtOrNullLaw(index: Int): E? =
    elementAtOrNull(index).post({ (it == null) == (index < 0 && index >= size) }) {
      "null iff out of bounds"
    }
  @Law
  inline fun <E> Collection<E>.firstLaw(): E {
    pre(size >= 1) { "not empty" }
    return first()
  }
  @Law
  inline fun <E> Collection<E>.firstLawWithPredicate(predicate: (x: E) -> Boolean): E {
    pre(size >= 1) { "not empty" }
    return first(predicate)
  }
  @Law
  inline fun <E> Collection<E>.firstOrNullLaw(): E? =
    firstOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }
  @Law
  inline fun <E> Collection<E>.lastLaw(): E {
    pre(size >= 1) { "not empty" }
    return last()
  }
  @Law
  inline fun <E> Collection<E>.lastLaw(predicate: (x: E) -> Boolean): E {
    pre(size >= 1) { "not empty" }
    return last(predicate)
  }
  @Law
  inline fun <E> Collection<E>.lastOrNullLaw(): E? =
    lastOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }

  @Law
  inline fun <E> Collection<E>.singleLaw(): E {
    pre(size == 1) { "size should be exactly 1" }
    return single()
  }
  @Law
  inline fun <E> Collection<E>.singleOrNullLaw(): E? =
    singleOrNull().post({ (it == null) == (this.size != 1) }) { "null iff size is not 1" }

  @Law
  inline fun <E> Collection<E>.indexOfLaw(element: E): Int =
    indexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOf"
    }
  @Law
  inline fun <E> Collection<E>.lastIndexOfLaw(element: E): Int =
    lastIndexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for lastIndexOf"
    }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.indexOfFirstLaw(predicate: (x: E) -> Boolean): Int =
    indexOfFirst(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfFirst"
    }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.indexOfLastLaw(predicate: (x: E) -> Boolean): Int =
    indexOfLast(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfLast"
    }

  @Law
  inline fun <T> Collection<T>.randomLaw(): T {
    pre(size > 0) { "not empty" }
    return random()
  }
  @Law
  inline fun <T> Collection<T>.randomOrNullLaw(): T? =
    randomOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }

  // zip and unzip
  @Law
  inline fun <T, R> Collection<Pair<T, R>>.unzipLaw(): Pair<List<T>, List<R>> =
    unzip().post({ it.first.size == this.size && it.second.size == this.size }) {
      "size remains after unzip"
    }
  @Law
  inline fun <T, R> Collection<T>.zipLaw(other: Collection<R>): List<Pair<T, R>> =
    zip(other).post({ it.size <= this.size && it.size <= other.size }) {
      "size bounded by the smallest"
    }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, R, V> Collection<T>.zipLaw(
    other: Collection<R>,
    transform: (a: T, b: R) -> V
  ): List<V> =
    zip(other, transform).post({ it.size <= this.size && it.size <= other.size }) {
      "size bounded by the smallest"
    }
  @Law
  inline fun <T> Collection<T>.zipWithNextLaw(): List<Pair<T, T>> =
    zipWithNext().post({ if (this.size == 0) (it.size == 0) else (it.size == this.size - 1) }) {
      "size is one less"
    }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, V> Collection<T>.zipWithNextLaw(transform: (a: T, b: T) -> V): List<V> =
    zipWithNext(transform).post({
      if (this.size == 0) (it.size == 0) else (it.size == this.size - 1)
    }) { "size bounded by the smallest" }

  // operations which remove things
  @Law
  inline fun <E> Collection<E>.dropLaw(n: Int): List<E> {
    pre(n >= 0) { "n must be non-negative" }
    return drop(n).post({ if (this.size <= n) (it.size == 0) else (it.size == this.size - n) }) {
      "bounds for drop"
    }
  }
  @Law
  inline fun <E> Collection<E>.takeLaw(n: Int): List<E> {
    pre(n >= 0) { "n must be non-negative" }
    return take(n).post({ it.size <= this.size && it.size <= n }) { "bounds for take" }
  }

  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.filterLaw(predicate: (E) -> Boolean): List<E> =
    filter(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.filterNotLaw(predicate: (E) -> Boolean): List<E> =
    filterNot(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E?>.filterNotNullLaw(): List<E> =
    filterNotNull().post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> Collection<E>.filterIndexedLaw(predicate: (Int, E) -> Boolean): List<E> =
    filterIndexed(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <reified R> Collection<*>.filterIsInstanceLaw(): List<R> =
    filterIsInstance<R>().post({ it.size <= this.size }) { "bounds after filter" }

  @Law
  inline fun <E> Collection<E>.distinctLaw(): List<E> =
    distinct().post({ it.size <= this.size }) { "size bounded by original " }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, K> Collection<T>.distinctByLaw(selector: (T) -> K): List<T> =
    distinctBy(selector).post({ it.size <= this.size }) { "size bounded by original " }

  @Law
  inline fun <E> Collection<E>.plusLaw(element: E): List<E> =
    plus(element).post({ it.size == this.size + 1 }) { "size increases by 1" }
  @Law
  inline fun <E> Collection<E>.plusLawWithElements(elements: Collection<E>): List<E> =
    plus(elements).post({ it.size == this.size + elements.size }) {
      "size increases by size of the collection"
    }

  @Law
  inline fun <E> Collection<E>.minusLaw(element: E): List<E> =
    minus(element).post({ it.size >= this.size - 1 && it.size <= this.size }) {
      "size may decrease by 1"
    }
  @Law
  inline fun <E> Collection<E>.minusLawWithElements(elements: Collection<E>): List<E> =
    minus(elements).post({ it.size >= this.size - elements.size && it.size <= this.size }) {
      "size may decrease by size of the removed elements"
    }

  // operations which keep the size
  @Law
  inline fun <E> Collection<E>.reversedLaw(): List<E> =
    reversed().post({ it.size == this.size }) { "size remains after reversal" }
  @Law
  inline fun <E : Comparable<E>> Collection<E>.sortedLaw(): List<E> =
    sorted().post({ it.size == this.size }) { "size remains after sorting" }
  @Law
  inline fun <E : Comparable<E>> Collection<E>.sortedDescendingLaw(): List<E> =
    sortedDescending().post({ it.size == this.size }) { "size remains after sorting" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, R : Comparable<R>> Collection<T>.sortedByLaw(
    crossinline selector: (T) -> R?
  ): List<T> = sortedBy(selector).post({ it.size == this.size }) { "size remains after sorting" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, R : Comparable<R>> Collection<T>.sortedByDescendingLaw(
    crossinline selector: (T) -> R?
  ): List<T> =
    sortedByDescending(selector).post({ it.size == this.size }) { "size remains after sorting" }
  @Law
  inline fun <T> Collection<T>.sortedWithLaw(comparator: Comparator<in T>): List<T> =
    sortedWith(comparator).post({ it.size == this.size }) { "size remains after sorting" }

  @Law
  inline fun <T> Collection<T>.shuffledLaw(): List<T> =
    shuffled().post({ it.size == this.size }) { "size remains after shuffle" }

  @Law
  @DoesNothingOnEmptyCollection
  inline fun <A, B> Collection<A>.mapLaw(transform: (A) -> B): List<B> =
    map(transform).post({ it.size == this.size }) { "size remains after map" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <A, B> Collection<A>.mapIndexedLaw(transform: (Int, A) -> B): List<B> =
    mapIndexed(transform).post({ it.size == this.size }) { "size remains after map" }

  @Law
  @DoesNothingOnEmptyCollection
  inline fun <A, B> Collection<A>.mapNotNullLaw(transform: (A) -> B?): List<B> =
    mapNotNull(transform).post({ it.size <= this.size }) { "size bounded by original" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <A, B> Collection<A>.mapIndexedNotNullLaw(transform: (Int, A) -> B?): List<B> =
    mapIndexedNotNull(transform).post({ it.size == this.size }) { "size bounded by original" }

  // operations between several collections
  @Law
  inline fun <T> Collection<T>.intersectLaw(other: Collection<T>): Set<T> =
    intersect(other).post({ it.size <= this.size && it.size <= other.size }) {
      "bounds for intersection"
    }
  @Law
  inline fun <T> Collection<T>.subtractLaw(other: Collection<T>): Set<T> =
    subtract(other).post({ it.size <= this.size }) { "bounds for subtraction" }
  @Law
  inline fun <T> Collection<T>.unionLaw(other: Collection<T>): Set<T> =
    union(other).post({ it.size >= this.size && it.size >= other.size }) {
      "bounds for subtraction"
    }
}

@Laws
object ListLaws {
  // size is inherited
  // isEmpty is inherited

  @Law
  inline fun <E> List<E>.getLaw(index: Int): E {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun <E> List<E>.elementAtLaw(index: Int): E {
    pre(index >= 0 && index < size) { "index within bounds" }
    return elementAt(index)
  }
  @Law
  inline fun <E> List<E>.getOrNullLaw(index: Int): E? =
    getOrNull(index).post({ (it == null) == (index < 0 && index >= size) }) {
      "null iff out of bounds"
    }
  @Law
  inline fun <E> List<E>.elementAtOrNullLaw(index: Int): E? =
    elementAtOrNull(index).post({ (it == null) == (index < 0 && index >= size) }) {
      "null iff out of bounds"
    }

  @Law
  inline fun <E> List<E>.firstLaw(): E {
    pre(size >= 1) { "not empty" }
    return first()
  }
  @Law
  inline fun <E> List<E>.firstOrNullLaw(): E? =
    firstOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }
  @Law
  inline fun <E> List<E>.lastLaw(): E {
    pre(size >= 1) { "not empty" }
    return last()
  }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> List<E>.lastLaw(predicate: (x: E) -> Boolean): E {
    pre(size >= 1) { "not empty" }
    return last(predicate)
  }
  @Law
  inline fun <E> List<E>.lastOrNullLaw(): E? =
    lastOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }

  @Law
  inline fun <E> List<E>.singleLaw(): E {
    pre(size == 1) { "size should be exactly 1" }
    return single()
  }
  @Law
  inline fun <E> List<E>.singleOrNullLaw(): E? =
    singleOrNull().post({ (it == null) == (this.size != 1) }) { "null iff size is not 1" }

  @Law
  inline fun <E> List<E>.indexOfLaw(element: E): Int =
    indexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOf"
    }
  @Law
  inline fun <E> List<E>.lastIndexOfLaw(element: E): Int =
    lastIndexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for lastIndexOf"
    }
  @Law
  inline fun <E> List<E>.lastIndexLaw(): Int =
    lastIndex.post({ it == size - 1 }) { "last index is size - 1" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> List<E>.indexOfFirstLaw(predicate: (x: E) -> Boolean): Int =
    indexOfFirst(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfFirst"
    }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <E> List<E>.indexOfLastLaw(predicate: (x: E) -> Boolean): Int =
    indexOfLast(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfLast"
    }

  @Law
  inline fun <E> emptyListLaw(): List<E> =
    emptyList<E>().post({ it.size == 0 }) { "empty list is empty" }
  @Law
  inline fun <E> emptyListOfLaw(): List<E> =
    listOf<E>().post({ it.size == 0 }) { "empty list is empty" }
  @Law
  inline fun <E> listOfNotNullLaw(element: E?): List<E> =
    listOfNotNull(element).post({ if (element == null) (it.size == 0) else (it.size == 1) }) {
      "empty iff element is null"
    }
  @Law
  inline fun <E> listOfLaw(vararg elements: E): List<E> =
    listOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun <E> listOfNotNullLaw(vararg elements: E?): List<E> =
    listOfNotNull(*elements).post({ it.size <= elements.size }) { "bounded by the literal" }

  @Law
  inline fun <E> emptyMutableListLaw(): List<E> =
    mutableListOf<E>().post({ it.size == 0 }) { "empty list is empty" }
  @Law
  inline fun <E> mutableListOfLaw(vararg elements: E): MutableList<E> =
    mutableListOf(*elements).post({ it.size == elements.size }) { "literal size" }

  @Law
  inline fun <E> List<E>?.orEmptyLaw(): List<E> =
    orEmpty().post({ if (this == null) (it.size == 0) else (it.size == this.size) }) {
      "returns empty when this is null"
    }

  @Law
  inline fun <E> List<E>.asReversedLaw(): List<E> =
    asReversed().post({ it.size == this.size }) { "size remains after reversal" }

  @Law
  inline fun <E> List<E>.component1Law(): E {
    pre(this.size >= 1) { "element #1 available" }
    return component1()
  }
  @Law
  inline fun <E> List<E>.component2Law(): E {
    pre(this.size >= 2) { "element #2 available" }
    return component2()
  }
  @Law
  inline fun <E> List<E>.component3Law(): E {
    pre(this.size >= 3) { "element #3 available" }
    return component3()
  }
  @Law
  inline fun <E> List<E>.component4Law(): E {
    pre(this.size >= 4) { "element #4 available" }
    return component4()
  }
  @Law
  inline fun <E> List<E>.component5Law(): E {
    pre(this.size >= 5) { "element #5 available" }
    return component5()
  }

  // operations
  @Law
  inline fun <E> List<E>.dropLastLaw(n: Int): List<E> {
    pre(n >= 0) { "n must be non-negative" }
    return dropLast(n).post({
      if (this.size <= n) (it.size == 0) else (it.size == this.size - n)
    }) { "bounds for drop" }
  }
  @Law
  inline fun <E> List<E>.takeLastLaw(n: Int): List<E> {
    pre(n >= 0) { "n must be non-negative" }
    return takeLast(n).post({ it.size <= this.size && it.size <= n }) { "bounds for take" }
  }
}

@Laws
object SetLaws {
  // size is inherited
  // isEmpty is inherited

  @Law
  inline fun <E> emptySetLaw(): Set<E> =
    emptySet<E>().post({ it.size == 0 }) { "empty set is empty" }
  @Law
  inline fun <E> emptySetOfLaw(): Set<E> =
    setOf<E>().post({ it.size == 0 }) { "empty set is empty" }
  @Law
  inline fun <E> setOfNotNullLaw(element: E?): Set<E> =
    setOfNotNull(element).post({ if (element == null) (it.size == 0) else (it.size == 1) }) {
      "empty iff element is null"
    }
  @Law
  inline fun <E> setOfLaw(vararg elements: E): Set<E> =
    setOf(*elements).post({ it.size <= elements.size }) { "bounded by the literal" }
  @Law
  inline fun <E> setOfNotNullLaw(vararg elements: E?): Set<E> =
    setOfNotNull(*elements).post({ it.size <= elements.size }) { "bounded by the literal" }

  @Law
  inline fun <E> emptyMutableSetOfLaw(): MutableSet<E> =
    mutableSetOf<E>().post({ it.size == 0 }) { "empty set is empty" }
  @Law
  inline fun <E> mutableSetOfLaw(vararg elements: E): MutableSet<E> =
    mutableSetOf(*elements).post({ it.size <= elements.size }) { "bounded by the literal" }

  @Law
  inline fun <E> Set<E>?.orEmptyLaw(): Set<E> =
    orEmpty().post({ if (this == null) (it.size == 0) else (it.size == this.size) }) {
      "returns empty when this is null"
    }
}

@Laws
object MapLaws {
  @Law
  inline fun <K, V> Map<K, V>.sizeLaw(): Int = size.post({ it >= 0 }) { "size is non-negative" }
  @Law
  inline fun <K, V> Map<K, V>.isEmptyLaw(): Boolean =
    isEmpty().post({ it == (size <= 0) }) { "empty when size is 0" }
  @Law
  inline fun <K, V> Map<K, V>.isNotEmptyLaw(): Boolean =
    isNotEmpty().post({ it == (size > 0) }) { "not empty when size is > 0" }
  @Law
  inline fun <K, V> Map<K, V>?.isNullOrEmptyLaw(): Boolean =
    isNullOrEmpty().post({ it == ((this == null) || (this.size <= 0)) }) {
      "either null or size is 0"
    }
  @Law
  inline fun <K, V> Map<K, V>?.orEmptyLaw(): Map<K, V> =
    orEmpty().post({ if (this == null) (it.size == 0) else (it.size == this.size) }) {
      "returns empty when this is null"
    }

  @Law
  inline fun <K, V> Map<K, V>.keysLaw(): Set<K> =
    keys.post({ it.size == this.size }) { "size of keys remains" }
  @Law
  inline fun <K, V> Map<K, V>.valuesLaw(): Collection<V> =
    values.post({ it.size == this.size }) { "size of values remains" }
  @Law
  inline fun <K, V> Map<K, V>.entriesLaw(): Set<Map.Entry<K, V>> =
    entries.post({ it.size == this.size }) { "size of entries remains" }

  @Law
  inline fun <K, V> emptyMapLaw(): Map<K, V> =
    emptyMap<K, V>().post({ it.size == 0 }) { "empty map is empty" }
  @Law
  inline fun <K, V> mapOfLaw(vararg elements: Pair<K, V>): Map<K, V> =
    mapOf(*elements).post({ it.size <= elements.size }) { "literal size" }

  @Law
  inline fun <K, V> emptyMutableMapLaw(): Map<K, V> =
    mutableMapOf<K, V>().post({ it.size == 0 }) { "empty map is empty" }
  @Law
  inline fun <K, V> mutableMapOfLaw(vararg elements: Pair<K, V>): MutableMap<K, V> =
    mutableMapOf(*elements).post({ it.size <= elements.size }) { "literal size" }

  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V, R> Map<out K, V>.mapValuesLaw(transform: (Map.Entry<K, V>) -> R): Map<K, R> =
    mapValues(transform).post({ it.size == this.size }) { "size remains after mapValues" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V, R> Map<out K, V>.mapKeysLaw(transform: (Map.Entry<K, V>) -> R): Map<R, V> =
    mapKeys(transform).post({ it.size <= this.size }) { "size bounded after mapKeys" }

  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V> Map<out K, V>.filterLaw(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> =
    filter(predicate).post({ it.size <= this.size }) { "size bounded after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V> Map<out K, V>.filterNotLaw(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> =
    filterNot(predicate).post({ it.size <= this.size }) { "size bounded after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V> Map<out K, V>.filterValuesLaw(predicate: (V) -> Boolean): Map<K, V> =
    filterValues(predicate).post({ it.size <= this.size }) { "size bounded after filter" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <K, V> Map<out K, V>.filterKeysLaw(predicate: (K) -> Boolean): Map<K, V> =
    filterKeys(predicate).post({ it.size <= this.size }) { "size bounded after filter" }

  @Law
  inline fun <K, V> Map<out K, V>.plusLaw(pair: Pair<K, V>): Map<K, V> =
    plus(pair).post({ it.size <= this.size + 1 }) { "size may increase by 1" }
  @Law
  inline fun <K, V> Map<out K, V>.plusLawWithElements(pairs: Collection<Pair<K, V>>): Map<K, V> =
    plus(pairs).post({ it.size <= this.size + pairs.size }) {
      "size may increase by size of the collection"
    }
  @Law
  inline fun <K, V> Map<out K, V>.plusLawWithMap(map: Map<K, V>): Map<K, V> =
    plus(map).post({ it.size <= this.size + map.size }) { "size may increase by size of the map" }

  @Law
  inline fun <K, V> Map<out K, V>.minusLaw(key: K): Map<K, V> =
    minus(key).post({ it.size <= this.size && it.size >= this.size - 1 }) {
      "size may decrease by 1"
    }
  @Law
  inline fun <K, V> Map<out K, V>.minusLawWithElements(keys: Collection<K>): Map<K, V> =
    minus(keys).post({ it.size <= this.size && it.size >= this.size - keys.size }) {
      "size may decrease by size of the collection"
    }
}

@Laws
object MapEntryLaws {
  @Law
  inline fun <K, V> Map.Entry<K, V>.component1Law(): K =
    component1().post({ it == this.key }) { "1st component is key" }
  @Law
  inline fun <K, V> Map.Entry<K, V>.component2Law(): V =
    component2().post({ it == this.value }) { "2nd component is value" }
}

@Laws
object CollectionConversionsLaws {
  @Law
  inline fun <K, V> Collection<Pair<K, V>>.toMapLaw(): Map<K, V> =
    toMap().post({ it.size <= this.size }) { "size bounded by collection" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, K, V> Collection<T>.associateLaw(transform: (T) -> Pair<K, V>): Map<K, V> =
    associate(transform).post({ it.size <= this.size }) { "size bounded by collection" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, K> Collection<T>.associateByLaw(keySelector: (T) -> K): Map<K, T> =
    associateBy(keySelector).post({ it.size <= this.size }) { "size bounded by collection" }
  @Law
  @DoesNothingOnEmptyCollection
  inline fun <T, K> Collection<K>.associateWithLaw(valueSelector: (K) -> T): Map<K, T> =
    associateWith(valueSelector).post({ it.size <= this.size }) { "size bounded by collection" }

  @Law
  inline fun <E> Collection<E>.toListLaw(): List<E> =
    toList().post({ it.size == this.size }) { "size remains after converstion to list" }
  @Law
  inline fun <E> Collection<E>.toSetLaw(): Set<E> =
    toSet().post({ it.size < this.size }) { "size bounded by original size" }
}

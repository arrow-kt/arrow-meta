// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post
import arrow.analysis.pre

object TestType {
  fun TestTypeResolution(n: () -> Int): Int {
    val javi = n()
    return javi
  }
}

@Laws
object ArrayLaws {
  @Law
  inline fun <reified E : Any> constructorLaw(size: Int, noinline init: (Int) -> E): Array<E> {
    return Array<E>(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun <reified E : Any> emptyArrayLaw(): Array<E> =
    emptyArray<E>().post({ it.size == 0 }) { "empty array" }

  @Law inline fun <E> Array<E>.sizeLaw(): Int = size.post({ it >= 0 }) { "size is non-negative" }
  @Law
  inline fun <E> Array<E>.countLaw(): Int = count().post({ it == this.size }) { "count is size" }
  @Law
  inline fun <E> Array<E>.countLaw(predicate: (E) -> Boolean): Int =
    count(predicate).post({ it <= this.size }) { "count bounded by size" }
  @Law
  inline fun <E> Array<E>.lastIndexLaw(): Int =
    lastIndex.post({ it == size - 1 }) { "last index is size - 1" }

  @Law
  inline fun <E> Array<E>.isEmptyLaw(): Boolean =
    isEmpty().post({ it == (size <= 0) }) { "empty when size is 0" }
  @Law
  inline fun <E> Array<E>.noneLaw(): Boolean =
    none().post({ it == (size <= 0) }) { "none when size is 0" }
  @Law
  inline fun <E> Array<E>.isNotEmptyLaw(): Boolean =
    isNotEmpty().post({ it == (size > 0) }) { "not empty when size is > 0" }
  @Law
  inline fun <E> Array<E>?.isNullOrEmptyLaw(): Boolean =
    isNullOrEmpty().post({ it == ((this == null) || (this.size <= 0)) }) {
      "either null or size is 0"
    }

  @Law
  inline fun <E> Array<E>.getLaw(index: Int): E {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun <E> Array<E>.setLaw(index: Int, value: E) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }

  @Law
  inline fun <E> Array<E>.elementAtLaw(index: Int): E {
    pre(index >= 0 && index < size) { "index within bounds" }
    return elementAt(index)
  }
  @Law
  inline fun <E> Array<E>.elementAtOrNullLaw(index: Int): E? =
    elementAtOrNull(index).post({ (it == null) == (index < 0 && index >= size) }) {
      "null iff out of bounds"
    }
  @Law
  inline fun <E> Collection<E>.firstLaw(): E {
    pre(size >= 1) { "not empty" }
    return first()
  }
  @Law
  inline fun <E> Array<E>.firstLawWithPredicate(predicate: (x: E) -> Boolean): E {
    pre(size >= 1) { "not empty" }
    return first(predicate)
  }
  @Law
  inline fun <E> Array<E>.firstOrNullLaw(): E? =
    firstOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }
  @Law
  inline fun <E> Array<E>.lastLaw(): E {
    pre(size >= 1) { "not empty" }
    return last()
  }
  @Law
  inline fun <E> Array<E>.lastLaw(predicate: (x: E) -> Boolean): E {
    pre(size >= 1) { "not empty" }
    return last(predicate)
  }
  @Law
  inline fun <E> Array<E>.lastOrNullLaw(): E? =
    lastOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }

  @Law
  inline fun <E> Array<E>.singleLaw(): E {
    pre(size == 1) { "size should be exactly 1" }
    return single()
  }
  @Law
  inline fun <E> Array<E>.singleOrNullLaw(): E? =
    singleOrNull().post({ (it == null) == (this.size != 1) }) { "null iff size is not 1" }

  @Law
  inline fun <E> Array<E>.indexOfLaw(element: E): Int =
    indexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOf"
    }
  @Law
  inline fun <E> Array<E>.lastIndexOfLaw(element: E): Int =
    lastIndexOf(element).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for lastIndexOf"
    }
  @Law
  inline fun <E> Array<E>.indexOfFirstLaw(predicate: (x: E) -> Boolean): Int =
    indexOfFirst(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfFirst"
    }
  @Law
  inline fun <E> Array<E>.indexOfLastLaw(predicate: (x: E) -> Boolean): Int =
    indexOfLast(predicate).post({ if (this.size <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfLast"
    }

  @Law
  inline fun <T> Array<T>.randomLaw(): T {
    pre(size > 0) { "not empty" }
    return random()
  }
  @Law
  inline fun <T> Array<T>.randomOrNullLaw(): T? =
    randomOrNull().post({ (it == null) == (this.size <= 0) }) { "null iff empty" }

  @Law
  inline fun <E> Array<E>.filterLaw(predicate: (E) -> Boolean): List<E> =
    filter(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  inline fun <E> Array<E>.filterNotLaw(predicate: (E) -> Boolean): List<E> =
    filterNot(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  inline fun <E> Array<E?>.filterNotNullLaw(): List<E> =
    filterNotNull().post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  inline fun <E> Array<E>.filterIndexedLaw(predicate: (Int, E) -> Boolean): List<E> =
    filterIndexed(predicate).post({ it.size <= this.size }) { "bounds after filter" }
  @Law
  inline fun <reified R> Array<*>.filterIsInstanceLaw(): List<R> =
    filterIsInstance<R>().post({ it.size <= this.size }) { "bounds after filter" }

  @Law
  inline fun <E> Array<E>.distinctLaw(): List<E> =
    distinct().post({ it.size <= this.size }) { "size bounded by original " }
  @Law
  inline fun <T, K> Array<T>.distinctByLaw(selector: (T) -> K): List<T> =
    distinctBy(selector).post({ it.size <= this.size }) { "size bounded by original " }

  @Law
  inline fun <A, B> Array<A>.mapLaw(transform: (A) -> B): List<B> =
    map(transform).post({ it.size == this.size }) { "size remains after map" }
  @Law
  inline fun <A, B> Array<A>.mapIndexedLaw(transform: (Int, A) -> B): List<B> =
    mapIndexed(transform).post({ it.size == this.size }) { "size remains after map" }

  @Law
  inline fun <A, B> Array<A>.mapNotNullLaw(transform: (A) -> B?): List<B> =
    mapNotNull(transform).post({ it.size <= this.size }) { "size bounded by original" }
  @Law
  inline fun <A, B> Array<A>.mapIndexedNotNullLaw(transform: (Int, A) -> B?): List<B> =
    mapIndexedNotNull(transform).post({ it.size == this.size }) { "size bounded by original" }

  @Law
  inline fun <T> Array<T>.intersectLaw(other: Collection<T>): Set<T> =
    intersect(other).post({ it.size <= this.size && it.size <= other.size }) {
      "bounds for intersection"
    }
  @Law
  inline fun <T> Array<T>.subtractLaw(other: Collection<T>): Set<T> =
    subtract(other).post({ it.size <= this.size }) { "bounds for subtraction" }
  @Law
  inline fun <T> Array<T>.unionLaw(other: Collection<T>): Set<T> =
    union(other).post({ it.size >= this.size && it.size >= other.size }) {
      "bounds for subtraction"
    }

  @Law
  inline fun <E> Array<E>.component1Law(): E {
    pre(this.size >= 1) { "element #1 available" }
    return component1()
  }
  @Law
  inline fun <E> Array<E>.component2Law(): E {
    pre(this.size >= 2) { "element #2 available" }
    return component2()
  }
  @Law
  inline fun <E> Array<E>.component3Law(): E {
    pre(this.size >= 3) { "element #3 available" }
    return component3()
  }
  @Law
  inline fun <E> Array<E>.component4Law(): E {
    pre(this.size >= 4) { "element #4 available" }
    return component4()
  }
  @Law
  inline fun <E> Array<E>.component5Law(): E {
    pre(this.size >= 5) { "element #5 available" }
    return component5()
  }
}

@Laws
object ArrayConversionsLaws {
  @Law
  inline fun <K, V> Array<Pair<K, V>>.toMapLaw(): Map<K, V> =
    toMap().post({ it.size <= this.size }) { "size bounded by array" }
  @Law
  inline fun <T, K, V> Array<T>.associateLaw(transform: (T) -> Pair<K, V>): Map<K, V> =
    associate(transform).post({ it.size <= this.size }) { "size bounded by array" }
  @Law
  inline fun <T, K> Array<T>.associateByLaw(keySelector: (T) -> K): Map<K, T> =
    associateBy(keySelector).post({ it.size <= this.size }) { "size bounded by array" }
  @Law
  inline fun <T, K> Array<K>.associateWithLaw(valueSelector: (K) -> T): Map<K, T> =
    associateWith(valueSelector).post({ it.size <= this.size }) { "size bounded by array" }

  @Law
  inline fun <E> Array<E>.toListLaw(): List<E> =
    toList().post({ it.size == this.size }) { "size remains after converstion to list" }
  @Law
  inline fun <E> Array<E>.toSetLaw(): Set<E> =
    toSet().post({ it.size < this.size }) { "size bounded by original size" }
}

@Laws
object CharArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Char): CharArray {
    return CharArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun charArrayOfLaw(vararg elements: Char): CharArray =
    charArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun CharArray.getLaw(index: Int): Char {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun CharArray.setLaw(index: Int, value: Char) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object BooleanArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Boolean): BooleanArray {
    return BooleanArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun booleanArrayOfLaw(vararg elements: Boolean): BooleanArray =
    booleanArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun BooleanArray.getLaw(index: Int): Boolean {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun BooleanArray.setLaw(index: Int, value: Boolean) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object ByteArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Byte): ByteArray {
    return ByteArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun byteArrayOfLaw(vararg elements: Byte): ByteArray =
    byteArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun ByteArray.getLaw(index: Int): Byte {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun ByteArray.setLaw(index: Int, value: Byte) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object ShortArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Short): ShortArray {
    return ShortArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun shortArrayOfLaw(vararg elements: Short): ShortArray =
    shortArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun ShortArray.getLaw(index: Int): Short {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun ShortArray.setLaw(index: Int, value: Short) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object IntArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Int): IntArray {
    return IntArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun intArrayOfLaw(vararg elements: Int): IntArray =
    intArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun IntArray.getLaw(index: Int): Int {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun IntArray.setLaw(index: Int, value: Int) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object LongArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Long): LongArray {
    return LongArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun longArrayOfLaw(vararg elements: Long): LongArray =
    longArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun LongArray.getLaw(index: Int): Long {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun LongArray.setLaw(index: Int, value: Long) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object FloatArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Float): FloatArray {
    return FloatArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun floatArrayOfLaw(vararg elements: Float): FloatArray =
    floatArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun FloatArray.getLaw(index: Int): Float {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun FloatArray.setLaw(index: Int, value: Float) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

@Laws
object DoubleArrayLaws {
  @Law
  inline fun constructorLaw(size: Int, noinline init: (Int) -> Double): DoubleArray {
    return DoubleArray(size, init).post({ it.size == size }) { "size is the given one" }
  }
  @Law
  inline fun doubleArrayOfLaw(vararg elements: Double): DoubleArray =
    doubleArrayOf(*elements).post({ it.size == elements.size }) { "literal size" }
  @Law
  inline fun DoubleArray.getLaw(index: Int): Double {
    pre(index >= 0 && index < size) { "index within bounds" }
    return get(index)
  }
  @Law
  inline fun DoubleArray.setLaw(index: Int, value: Double) {
    pre(index >= 0 && index < size) { "index within bounds" }
    return set(index, value)
  }
}

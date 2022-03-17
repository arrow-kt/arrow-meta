// ktlint-disable filename
@file:Suppress("NOTHING_TO_INLINE", "UNREACHABLE_CODE")

package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.Laws
import arrow.analysis.post
import arrow.analysis.pre

@Laws
object CharSequenceLaws {
  @Law
  inline fun CharSequence.lengthLaw(): Int = length.post({ it >= 0 }) { "length is non-negative" }
  @Law
  inline fun CharSequence.countLaw(): Int =
    count().post({ it == this.length }) { "count is length" }
  @Law
  inline fun CharSequence.countLaw(predicate: (Char) -> Boolean): Int =
    count(predicate).post({ it <= this.length }) { "count bounded by length" }
  @Law
  inline fun CharSequence.lastIndexLaw(): Int =
    lastIndex.post({ it == length - 1 }) { "last index is length - 1" }

  @Law
  inline fun CharSequence.isEmptyLaw(): Boolean =
    isEmpty().post({ it == (length <= 0) }) { "empty when length is 0" }
  @Law
  inline fun CharSequence.noneLaw(): Boolean =
    none().post({ it == (length <= 0) }) { "none when length is 0" }
  @Law
  inline fun CharSequence.isNotEmptyLaw(): Boolean =
    isNotEmpty().post({ it == (length > 0) }) { "not empty when length is > 0" }
  @Law
  inline fun CharSequence?.isNullOrEmptyLaw(): Boolean =
    isNullOrEmpty().post({ it == ((this == null) || (this.length <= 0)) }) {
      "either null or length is 0"
    }

  @Law
  inline fun CharSequence.getLaw(index: Int): Char {
    pre(index >= 0 && index < length) { "index within bounds" }
    return get(index)
  }

  @Law
  inline fun CharSequence.elementAtLaw(index: Int): Char {
    pre(index >= 0 && index < length) { "index within bounds" }
    return elementAt(index)
  }
  @Law
  inline fun CharSequence.elementAtOrNullLaw(index: Int): Char? =
    elementAtOrNull(index).post({ (it == null) == (index < 0 && index >= length) }) {
      "null iff out of bounds"
    }
  @Law
  inline fun CharSequence.firstLaw(): Char {
    pre(length >= 1) { "not empty" }
    return first()
  }
  @Law
  inline fun CharSequence.firstLawWithPredicate(predicate: (x: Char) -> Boolean): Char {
    pre(length >= 1) { "not empty" }
    return first(predicate)
  }
  @Law
  inline fun CharSequence.firstOrNullLaw(): Char? =
    firstOrNull().post({ (it == null) == (this.length <= 0) }) { "null iff empty" }
  @Law
  inline fun CharSequence.lastLaw(): Char {
    pre(length >= 1) { "not empty" }
    return last()
  }
  @Law
  inline fun CharSequence.lastLaw(predicate: (x: Char) -> Boolean): Char {
    pre(length >= 1) { "not empty" }
    return last(predicate)
  }
  @Law
  inline fun CharSequence.lastOrNullLaw(): Char? =
    lastOrNull().post({ (it == null) == (this.length <= 0) }) { "null iff empty" }

  @Law
  inline fun CharSequence.singleLaw(): Char {
    pre(length == 1) { "length should be exactly 1" }
    return single()
  }
  @Law
  inline fun CharSequence.singleOrNullLaw(): Char? =
    singleOrNull().post({ (it == null) == (this.length != 1) }) { "null iff length is not 1" }

  @Law
  inline fun CharSequence.indexOfLawChar(element: Char, startIndex: Int, ignoreCase: Boolean): Int =
    indexOf(element, startIndex, ignoreCase).post({
      if (this.length <= 0) (it == -1) else (it >= -1)
    }) { "bounds for indexOf" }
  @Law
  inline fun CharSequence.indexOfLawString(
    element: String,
    startIndex: Int,
    ignoreCase: Boolean
  ): Int =
    indexOf(element, startIndex, ignoreCase).post({
      if (this.length <= 0) (it == -1) else (it >= -1)
    }) { "bounds for indexOf" }
  @Law
  inline fun CharSequence.lastIndexOfLawChar(
    element: Char,
    startIndex: Int,
    ignoreCase: Boolean
  ): Int =
    lastIndexOf(element, startIndex, ignoreCase).post({
      if (this.length <= 0) (it == -1) else (it >= -1)
    }) { "bounds for lastIndexOf" }
  @Law
  inline fun CharSequence.lastIndexOfLawString(
    element: String,
    startIndex: Int,
    ignoreCase: Boolean
  ): Int =
    lastIndexOf(element, startIndex, ignoreCase).post({
      if (this.length <= 0) (it == -1) else (it >= -1)
    }) { "bounds for lastIndexOf" }
  @Law
  inline fun CharSequence.indexOfFirstLaw(predicate: (x: Char) -> Boolean): Int =
    indexOfFirst(predicate).post({ if (this.length <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfFirst"
    }
  @Law
  inline fun CharSequence.indexOfLastLaw(predicate: (x: Char) -> Boolean): Int =
    indexOfLast(predicate).post({ if (this.length <= 0) (it == -1) else (it >= -1) }) {
      "bounds for indexOfLast"
    }

  @Law
  inline fun CharSequence.randomLaw(): Char {
    pre(length > 0) { "not empty" }
    return random()
  }

  @Law
  inline fun CharSequence.filterLaw(predicate: (Char) -> Boolean): CharSequence =
    filter(predicate).post({ it.length <= this.length }) { "bounds after filter" }
  @Law
  inline fun CharSequence.filterNotLaw(predicate: (Char) -> Boolean): CharSequence =
    filterNot(predicate).post({ it.length <= this.length }) { "bounds after filter" }
  @Law
  inline fun CharSequence.filterIndexedLaw(predicate: (Int, Char) -> Boolean): CharSequence =
    filterIndexed(predicate).post({ it.length <= this.length }) { "bounds after filter" }

  @Law
  inline fun <B> CharSequence.mapLaw(transform: (Char) -> B): List<B> =
    map(transform).post({ it.size == this.length }) { "length remains after map" }
  @Law
  inline fun <B> CharSequence.mapIndexedLaw(transform: (Int, Char) -> B): List<B> =
    mapIndexed(transform).post({ it.size == this.length }) { "length remains after map" }

  @Law
  inline fun <B> CharSequence.mapNotNullLaw(transform: (Char) -> B?): List<B> =
    mapNotNull(transform).post({ it.size <= this.length }) { "length bounded by original" }
  @Law
  inline fun <B> CharSequence.mapIndexedNotNullLaw(transform: (Int, Char) -> B?): List<B> =
    mapIndexedNotNull(transform).post({ it.size == this.length }) { "length bounded by original" }

  @Law
  inline fun CharSequence.dropLaw(n: Int): CharSequence {
    pre(n >= 0) { "n must be non-negative" }
    return drop(n).post({
      if (this.length <= n) (it.length == 0) else (it.length == this.length - n)
    }) { "bounds for drop" }
  }
  @Law
  inline fun CharSequence.takeLaw(n: Int): CharSequence {
    pre(n >= 0) { "n must be non-negative" }
    return take(n).post({ it.length <= this.length && it.length <= n }) { "bounds for take" }
  }
  @Law
  inline fun CharSequence.dropLastLaw(n: Int): CharSequence {
    pre(n >= 0) { "n must be non-negative" }
    return dropLast(n).post({
      if (this.length <= n) (it.length == 0) else (it.length == this.length - n)
    }) { "bounds for drop" }
  }
  @Law
  inline fun CharSequence.takeLastLaw(n: Int): CharSequence {
    pre(n >= 0) { "n must be non-negative" }
    return takeLast(n).post({ it.length <= this.length && it.length <= n }) { "bounds for take" }
  }

  @Law
  inline fun CharSequence.trimLaw(): CharSequence =
    trim().post({ it.length <= this.length }) { "trim may reduce the size" }
  @Law
  inline fun CharSequence.trimLaw(vararg chars: Char): CharSequence =
    trim(*chars).post({ it.length <= this.length }) { "trim may reduce the size" }
  @Law
  inline fun CharSequence.trimEndLaw(): CharSequence =
    trimEnd().post({ it.length <= this.length }) { "trim may reduce the size" }
  @Law
  inline fun CharSequence.trimEndLaw(vararg chars: Char): CharSequence =
    trimEnd(*chars).post({ it.length <= this.length }) { "trim may reduce the size" }
  @Law
  inline fun CharSequence.trimStartLaw(): CharSequence =
    trimStart().post({ it.length <= this.length }) { "trim may reduce the size" }
  @Law
  inline fun CharSequence.trimStartLaw(vararg chars: Char): CharSequence =
    trimStart(*chars).post({ it.length <= this.length }) { "trim may reduce the size" }
}

@Laws
object CharSequenceConversionsLaws {
  @Law
  inline fun <K, V> CharSequence.associateLaw(transform: (Char) -> Pair<K, V>): Map<K, V> =
    associate(transform).post({ it.size <= this.length }) { "length bounded by array" }
  @Law
  inline fun <K> CharSequence.associateByLaw(keySelector: (Char) -> K): Map<K, Char> =
    associateBy(keySelector).post({ it.size <= this.length }) { "length bounded by array" }
  @Law
  inline fun <T> CharSequence.associateWithLaw(valueSelector: (Char) -> T): Map<Char, T> =
    associateWith(valueSelector).post({ it.size <= this.length }) { "length bounded by array" }

  @Law
  inline fun CharSequence.toListLaw(): List<Char> =
    toList().post({ it.size == this.length }) { "length remains after converstion to list" }
  @Law
  inline fun CharSequence.toMutableListLaw(): List<Char> =
    toMutableList().post({ it.size == this.length }) { "length remains after converstion to list" }

  @Law
  inline fun CharSequence.toSetLaw(): Set<Char> =
    toSet().post({ it.size < this.length }) { "length bounded by original length" }
  @Law
  inline fun CharSequence.toHashSetLaw(): HashSet<Char> =
    toHashSet().post({ it.size < this.length }) { "length bounded by original length" }
}

@Laws
object StringLaws {
  @Law
  inline fun String.plusLaw(other: CharSequence?): String =
    plus(other).post({
      when {
        other == null -> it.length == this.length
        else -> it.length == this.length + other.length
      }
    }) { "concatenation adds lengths" }
}

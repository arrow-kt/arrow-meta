package arrow.refinement.laws.kotlin.collections

import arrow.refinement.Law
import arrow.refinement.post
import arrow.refinement.pre

@Law
public inline fun ByteArray.toStringLaw(charset: java.nio.charset.Charset): String {
  pre(true) { "kotlin.collections.toString pre-conditions" }
  return toString(charset)
    .post({ true }, { "kotlin.collections.toString post-conditions" })
}

@Law
public fun <T> Array<out T>.binarySearchLaw(element: T, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}


@Law
public fun ByteArray.binarySearchLaw(element: Byte, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun CharArray.binarySearchLaw(element: Char, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun DoubleArray.binarySearchLaw(element: Double, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun FloatArray.binarySearchLaw(element: Float, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun IntArray.binarySearchLaw(element: Int, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun LongArray.binarySearchLaw(element: Long, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun ShortArray.binarySearchLaw(element: Short, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}


@Law
public fun <T> Array<out T>.binarySearchLaw(
  element: T,
  comparator: Comparator<in T> /* = java.util.Comparator<in T> */,
  fromIndex: Int,
  toIndex: Int
): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, comparator, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}


@Law
public fun <R> Array<*>.filterIsInstanceLaw(klass: java.lang.Class<R>): List<R> {
  pre(true) { "kotlin.collections.filterIsInstance pre-conditions" }
  return filterIsInstance(klass)
    .post({ true }, { "kotlin.collections.filterIsInstance post-conditions" })
}

@Law
public fun <C : MutableCollection<in R>, R> Array<*>.filterIsInstanceToLaw(
  destination: C,
  klass: java.lang.Class<R>
): C {
  pre(true) { "kotlin.collections.filterIsInstanceTo pre-conditions" }
  return filterIsInstanceTo(destination, klass)
    .post({ true }, { "kotlin.collections.filterIsInstanceTo post-conditions" })
}


@Law
public fun <T> Array<out T>.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun CharArray.sumOfLaw(selector: (Char) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun CharArray.sumOfLaw(selector: (Char) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun FloatArray.sumOfLaw(selector: (Float) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun FloatArray.sumOfLaw(selector: (Float) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun IntArray.sumOfLaw(selector: (Int) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun IntArray.sumOfLaw(selector: (Int) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun LongArray.sumOfLaw(selector: (Long) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun LongArray.sumOfLaw(selector: (Long) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ShortArray.sumOfLaw(selector: (Short) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ShortArray.sumOfLaw(selector: (Short) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.toSortedSetLaw(): java.util.SortedSet<T> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun <T> Array<out T>.toSortedSetLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): java.util.SortedSet<T> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet(comparator)
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun BooleanArray.toSortedSetLaw(): java.util.SortedSet<Boolean> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun ByteArray.toSortedSetLaw(): java.util.SortedSet<Byte> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun CharArray.toSortedSetLaw(): java.util.SortedSet<Char> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun DoubleArray.toSortedSetLaw(): java.util.SortedSet<Double> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun FloatArray.toSortedSetLaw(): java.util.SortedSet<Float> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun IntArray.toSortedSetLaw(): java.util.SortedSet<Int> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun LongArray.toSortedSetLaw(): java.util.SortedSet<Long> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun ShortArray.toSortedSetLaw(): java.util.SortedSet<Short> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}


@Law
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public fun <T> Array<out T>.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun <T> sortedSetOfLaw(
  comparator: Comparator<in T> /* = java.util.Comparator<in T> */,
  vararg elements: T
): java.util.TreeSet<T> {
  pre(true) { "kotlin.collections.sortedSetOf pre-conditions" }
  return sortedSetOf(comparator, *elements)
    .post({ true }, { "kotlin.collections.sortedSetOf post-conditions" })
}


@Law
public fun <T> Iterable<T>.shuffledLaw(random: java.util.Random): List<T> {
  pre(true) { "kotlin.collections.shuffled pre-conditions" }
  return shuffled(random)
    .post({ true }, { "kotlin.collections.shuffled post-conditions" })
}

@Law
public inline fun <T> java.util.Enumeration<T>.toListLaw(): List<T> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}


@Law
public fun <T> java.util.Enumeration<T>.iteratorLaw(): Iterator<T> {
  pre(true) { "kotlin.collections.iterator pre-conditions" }
  return iterator()
    .post({ true }, { "kotlin.collections.iterator post-conditions" })
}

@Law
public inline fun <T> MutableList<T>.shuffleLaw(random: java.util.Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun <R> Iterable<*>.filterIsInstanceLaw(klass: java.lang.Class<R>): List<R> {
  pre(true) { "kotlin.collections.filterIsInstance pre-conditions" }
  return filterIsInstance(klass)
    .post({ true }, { "kotlin.collections.filterIsInstance post-conditions" })
}

@Law
public fun <C : MutableCollection<in R>, R> Iterable<*>.filterIsInstanceToLaw(
  destination: C,
  klass: java.lang.Class<R>
): C {
  pre(true) { "kotlin.collections.filterIsInstanceTo pre-conditions" }
  return filterIsInstanceTo(destination, klass)
    .post({ true }, { "kotlin.collections.filterIsInstanceTo post-conditions" })
}


@Law
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> java.math.BigDecimal): java.math.BigDecimal {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> java.math.BigInteger): java.math.BigInteger {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public fun <T : Comparable<T>> Iterable<T>.toSortedSetLaw(): java.util.SortedSet<T> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet()
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}

@Law
public fun <T> Iterable<T>.toSortedSetLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): java.util.SortedSet<T> {
  pre(true) { "kotlin.collections.toSortedSet pre-conditions" }
  return toSortedSet(comparator)
    .post({ true }, { "kotlin.collections.toSortedSet post-conditions" })
}


@Law
public fun <K, V> sortedMapOfLaw(
  comparator: java.util.Comparator<in K>,
  vararg pairs: Pair<K, V>
): java.util.SortedMap<K, V> {
  pre(true) { "kotlin.collections.sortedMapOf pre-conditions" }
  return sortedMapOf(comparator, *pairs)
    .post({ true }, { "kotlin.collections.sortedMapOf post-conditions" })
}

@Law
public fun <K : Comparable<K>, V> sortedMapOfLaw(vararg pairs: Pair<K, V>): java.util.SortedMap<K, V> {
  pre(true) { "kotlin.collections.sortedMapOf pre-conditions" }
  return sortedMapOf(*pairs)
    .post({ true }, { "kotlin.collections.sortedMapOf post-conditions" })
}

@Law
public inline fun <K, V> java.util.concurrent.ConcurrentMap<K, V>.getOrPutLaw(key: K, defaultValue: () -> V): V {
  pre(true) { "kotlin.collections.getOrPut pre-conditions" }
  return getOrPut(key, defaultValue)
    .post({ true }, { "kotlin.collections.getOrPut post-conditions" })
}

@Law
public inline fun Map<String, String>.toPropertiesLaw(): java.util.Properties {
  pre(true) { "kotlin.collections.toProperties pre-conditions" }
  return toProperties()
    .post({ true }, { "kotlin.collections.toProperties post-conditions" })
}

@Law
public fun <K : Comparable<K>, V> Map<out K, V>.toSortedMapLaw(): java.util.SortedMap<K, V> {
  pre(true) { "kotlin.collections.toSortedMap pre-conditions" }
  return toSortedMap()
    .post({ true }, { "kotlin.collections.toSortedMap post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.toSortedMapLaw(comparator: java.util.Comparator<in K>): java.util.SortedMap<K, V> {
  pre(true) { "kotlin.collections.toSortedMap pre-conditions" }
  return toSortedMap(comparator)
    .post({ true }, { "kotlin.collections.toSortedMap post-conditions" })
}


@Law
public fun <T> sortedSetOfLaw(vararg elements: T): java.util.TreeSet<T> {
  pre(true) { "kotlin.collections.sortedSetOf pre-conditions" }
  return sortedSetOf(*elements)
    .post({ true }, { "kotlin.collections.sortedSetOf post-conditions" })
}

@Law
public fun UByteArray.binarySearchLaw(element: UByte, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun UIntArray.binarySearchLaw(element: UInt, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun ULongArray.binarySearchLaw(element: ULong, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun UShortArray.binarySearchLaw(element: UShort, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public inline fun <K, V> Map<in K, V>.getOrDefaultLaw(key: K, defaultValue: V): V {
  pre(true) { "kotlin.collections.getOrDefault pre-conditions" }
  return getOrDefault(key, defaultValue)
    .post({ true }, { "kotlin.collections.getOrDefault post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, V>.removeLaw(key: K, value: V): Boolean {
  pre(true) { "kotlin.collections.remove pre-conditions" }
  return remove(key, value)
    .post({ true }, { "kotlin.collections.remove post-conditions" })
}
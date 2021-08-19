package arrow.refinement.laws.kotlin.collections

import arrow.refinement.pre
import arrow.refinement.post
import arrow.refinement.Law
import kotlin.jvm.JvmName
import kotlin.random.Random
import kotlin.reflect.KProperty

@Law
public inline fun <reified T> Array<out T>?.orEmptyNullableLaw(): Array<out T> {
  pre(true) { "kotlin.collections.orEmpty pre-conditions" }
  return orEmpty()
    .post({ true }, { "kotlin.collections.orEmpty post-conditions" })
}

@Law
public inline fun <reified T> Collection<T>.toTypedArrayLaw(): Array<T> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun <T> Array<out Array<out T>>.flattenLaw(): List<T> {
  pre(true) { "kotlin.collections.flatten pre-conditions" }
  return flatten()
    .post({ true }, { "kotlin.collections.flatten post-conditions" })
}

// uses variance with special compiler treatment for the std library where C: Array and C : R
// @Law
// public inline fun <C: Array<*> , R> C.ifEmptyLaw(defaultValue: () -> R): R  {
//  pre(true) { "kotlin.collections.ifEmpty pre-conditions" }
//  return ifEmpty(defaultValue)
//    .post({ true }, { "kotlin.collections.ifEmpty post-conditions" })
// }

@Law
public inline fun Array<*>?.isNullOrEmptyNullableLaw(): Boolean {
  pre(true) { "kotlin.collections.isNullOrEmpty pre-conditions" }
  return isNullOrEmpty()
    .post({ true }, { "kotlin.collections.isNullOrEmpty post-conditions" })
}

@Law
public fun <T, R> Array<out Pair<T, R>>.unzipLaw(): Pair<List<T>, List<R>> {
  pre(true) { "kotlin.collections.unzip pre-conditions" }
  return unzip()
    .post({ true }, { "kotlin.collections.unzip post-conditions" })
}

@Law
public fun <T> Array<out T>.asListLaw(): List<T> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun BooleanArray.asListLaw(): List<Boolean> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun ByteArray.asListLaw(): List<Byte> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun CharArray.asListLaw(): List<Char> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun DoubleArray.asListLaw(): List<Double> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun FloatArray.asListLaw(): List<Float> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun IntArray.asListLaw(): List<Int> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun LongArray.asListLaw(): List<Long> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun ShortArray.asListLaw(): List<Short> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public inline infix fun <T> Array<out T>.contentDeepEqualsLaw(other: Array<out T>): Boolean {
  pre(true) { "kotlin.collections.contentDeepEquals pre-conditions" }
  return contentDeepEquals(other)
    .post({ true }, { "kotlin.collections.contentDeepEquals post-conditions" })
}

@Law
public inline infix fun <T> Array<out T>?.contentDeepEqualsNullableNullableLaw(other: Array<out T>?): Boolean {
  pre(true) { "kotlin.collections.contentDeepEquals pre-conditions" }
  return contentDeepEquals(other)
    .post({ true }, { "kotlin.collections.contentDeepEquals post-conditions" })
}

@Law
public inline fun <T> Array<out T>.contentDeepHashCodeLaw(): Int {
  pre(true) { "kotlin.collections.contentDeepHashCode pre-conditions" }
  return contentDeepHashCode()
    .post({ true }, { "kotlin.collections.contentDeepHashCode post-conditions" })
}

@Law
public inline fun <T> Array<out T>?.contentDeepHashCodeNullableNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentDeepHashCode pre-conditions" }
  return contentDeepHashCode()
    .post({ true }, { "kotlin.collections.contentDeepHashCode post-conditions" })
}

@Law
public inline fun <T> Array<out T>.contentDeepToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentDeepToString pre-conditions" }
  return contentDeepToString()
    .post({ true }, { "kotlin.collections.contentDeepToString post-conditions" })
}

@Law
public inline fun <T> Array<out T>?.contentDeepToStringNullableNullableLaw(): String {
  pre(true) { "kotlin.collections.contentDeepToString pre-conditions" }
  return contentDeepToString()
    .post({ true }, { "kotlin.collections.contentDeepToString post-conditions" })
}

@Law
public inline infix fun <T> Array<out T>?.contentEqualsNullableNullableLaw(other: Array<out T>?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun BooleanArray?.contentEqualsNullableNullableLaw(other: BooleanArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun ByteArray?.contentEqualsNullableLaw(other: ByteArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun CharArray?.contentEqualsNullableLaw(other: CharArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun DoubleArray?.contentEqualsNullableLaw(other: DoubleArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun FloatArray?.contentEqualsNullableLaw(other: FloatArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun IntArray?.contentEqualsNullableLaw(other: IntArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun LongArray?.contentEqualsNullableLaw(other: LongArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline infix fun ShortArray?.contentEqualsNullableLaw(other: ShortArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public inline fun <T> Array<out T>?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun BooleanArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun ByteArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun CharArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun DoubleArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun FloatArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun IntArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun LongArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun ShortArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public inline fun <T> Array<out T>?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun BooleanArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun ByteArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun CharArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun DoubleArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun FloatArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun IntArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun LongArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun ShortArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public fun <T> Array<out T>.copyIntoLaw(
  destination: Array<T>,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): Array<T> {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun BooleanArray.copyIntoLaw(
  destination: BooleanArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): BooleanArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun ByteArray.copyIntoLaw(
  destination: ByteArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): ByteArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun CharArray.copyIntoLaw(
  destination: CharArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): CharArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun DoubleArray.copyIntoLaw(
  destination: DoubleArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): DoubleArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun FloatArray.copyIntoLaw(
  destination: FloatArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): FloatArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun IntArray.copyIntoLaw(
  destination: IntArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): IntArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun LongArray.copyIntoLaw(
  destination: LongArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): LongArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public fun ShortArray.copyIntoLaw(
  destination: ShortArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): ShortArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public inline fun <T> Array<T>.copyOfLaw(): Array<T> {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun <T> Array<T>.copyOfLaw(newSize: Int): Array<T?> {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun BooleanArray.copyOfLaw(): BooleanArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun BooleanArray.copyOfLaw(newSize: Int): BooleanArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ByteArray.copyOfLaw(): ByteArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ByteArray.copyOfLaw(newSize: Int): ByteArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun CharArray.copyOfLaw(): CharArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun CharArray.copyOfLaw(newSize: Int): CharArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun DoubleArray.copyOfLaw(): DoubleArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun DoubleArray.copyOfLaw(newSize: Int): DoubleArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun FloatArray.copyOfLaw(): FloatArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun FloatArray.copyOfLaw(newSize: Int): FloatArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun IntArray.copyOfLaw(): IntArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun IntArray.copyOfLaw(newSize: Int): IntArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun LongArray.copyOfLaw(): LongArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun LongArray.copyOfLaw(newSize: Int): LongArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ShortArray.copyOfLaw(): ShortArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ShortArray.copyOfLaw(newSize: Int): ShortArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun <T> Array<T>.copyOfRangeLaw(fromIndex: Int, toIndex: Int): Array<T> {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun BooleanArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): BooleanArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun ByteArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): ByteArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun CharArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): CharArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun DoubleArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): DoubleArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun FloatArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): FloatArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun IntArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): IntArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun LongArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): LongArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun ShortArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): ShortArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun <T> Array<out T>.elementAtLaw(index: Int): T {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun BooleanArray.elementAtLaw(index: Int): Boolean {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun ByteArray.elementAtLaw(index: Int): Byte {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun CharArray.elementAtLaw(index: Int): Char {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun DoubleArray.elementAtLaw(index: Int): Double {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun FloatArray.elementAtLaw(index: Int): Float {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun IntArray.elementAtLaw(index: Int): Int {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun LongArray.elementAtLaw(index: Int): Long {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun ShortArray.elementAtLaw(index: Int): Short {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public fun <T> Array<T>.fillLaw(element: T, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun BooleanArray.fillLaw(element: Boolean, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun ByteArray.fillLaw(element: Byte, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun CharArray.fillLaw(element: Char, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun DoubleArray.fillLaw(element: Double, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun FloatArray.fillLaw(element: Float, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun IntArray.fillLaw(element: Int, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun LongArray.fillLaw(element: Long, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun ShortArray.fillLaw(element: Short, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun <T> Array<T>.plusLaw(element: T): Array<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Array<T>.plusLaw(elements: Array<out T>): Array<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Array<T>.plusLaw(elements: Collection<T>): Array<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun BooleanArray.plusLaw(element: Boolean): BooleanArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun BooleanArray.plusLaw(elements: BooleanArray): BooleanArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun BooleanArray.plusLaw(elements: Collection<Boolean>): BooleanArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ByteArray.plusLaw(element: Byte): ByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ByteArray.plusLaw(elements: ByteArray): ByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ByteArray.plusLaw(elements: Collection<Byte>): ByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun CharArray.plusLaw(element: Char): CharArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun CharArray.plusLaw(elements: CharArray): CharArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun CharArray.plusLaw(elements: Collection<Char>): CharArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun DoubleArray.plusLaw(element: Double): DoubleArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun DoubleArray.plusLaw(elements: DoubleArray): DoubleArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun DoubleArray.plusLaw(elements: Collection<Double>): DoubleArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun FloatArray.plusLaw(element: Float): FloatArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun FloatArray.plusLaw(elements: FloatArray): FloatArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun FloatArray.plusLaw(elements: Collection<Float>): FloatArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun IntArray.plusLaw(element: Int): IntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun IntArray.plusLaw(elements: IntArray): IntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun IntArray.plusLaw(elements: Collection<Int>): IntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun LongArray.plusLaw(element: Long): LongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun LongArray.plusLaw(elements: LongArray): LongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun LongArray.plusLaw(elements: Collection<Long>): LongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ShortArray.plusLaw(element: Short): ShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ShortArray.plusLaw(elements: ShortArray): ShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ShortArray.plusLaw(elements: Collection<Short>): ShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun <T> Array<T>.plusElementLaw(element: T): Array<T> {
  pre(true) { "kotlin.collections.plusElement pre-conditions" }
  return plusElement(element)
    .post({ true }, { "kotlin.collections.plusElement post-conditions" })
}

@Law
public inline fun <T : Comparable<T>> Array<out T>.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ByteArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ByteArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun CharArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun CharArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun DoubleArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun DoubleArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun FloatArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun FloatArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun IntArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun IntArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun LongArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun LongArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ShortArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ShortArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun <T> Array<out T>.sortWithLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): Unit {
  pre(true) { "kotlin.collections.sortWith pre-conditions" }
  return sortWith(comparator)
    .post({ true }, { "kotlin.collections.sortWith post-conditions" })
}

@Law
public fun <T> Array<out T>.sortWithLaw(
  comparator: Comparator<in T> /* = java.util.Comparator<in T> */,
  fromIndex: Int,
  toIndex: Int
): Unit {
  pre(true) { "kotlin.collections.sortWith pre-conditions" }
  return sortWith(comparator, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortWith post-conditions" })
}

@Law
public fun BooleanArray.toTypedArrayLaw(): Array<Boolean> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun ByteArray.toTypedArrayLaw(): Array<Byte> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun CharArray.toTypedArrayLaw(): Array<Char> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun DoubleArray.toTypedArrayLaw(): Array<Double> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun FloatArray.toTypedArrayLaw(): Array<Float> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun IntArray.toTypedArrayLaw(): Array<Int> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun LongArray.toTypedArrayLaw(): Array<Long> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun ShortArray.toTypedArrayLaw(): Array<Short> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public inline fun <T> Array<out T>.allLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun BooleanArray.allLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun ByteArray.allLaw(predicate: (Byte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun CharArray.allLaw(predicate: (Char) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun DoubleArray.allLaw(predicate: (Double) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun FloatArray.allLaw(predicate: (Float) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun IntArray.allLaw(predicate: (Int) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun LongArray.allLaw(predicate: (Long) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun ShortArray.allLaw(predicate: (Short) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public fun <T> Array<out T>.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun <T> Array<out T>.anyLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun BooleanArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun BooleanArray.anyLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun ByteArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun ByteArray.anyLaw(predicate: (Byte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun CharArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun CharArray.anyLaw(predicate: (Char) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun DoubleArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun DoubleArray.anyLaw(predicate: (Double) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun FloatArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun FloatArray.anyLaw(predicate: (Float) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun IntArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun IntArray.anyLaw(predicate: (Int) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun LongArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun LongArray.anyLaw(predicate: (Long) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun ShortArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun ShortArray.anyLaw(predicate: (Short) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public fun <T> Array<out T>.asIterableLaw(): Iterable<T> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun BooleanArray.asIterableLaw(): Iterable<Boolean> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun ByteArray.asIterableLaw(): Iterable<Byte> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun CharArray.asIterableLaw(): Iterable<Char> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun DoubleArray.asIterableLaw(): Iterable<Double> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun FloatArray.asIterableLaw(): Iterable<Float> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun IntArray.asIterableLaw(): Iterable<Int> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun LongArray.asIterableLaw(): Iterable<Long> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun ShortArray.asIterableLaw(): Iterable<Short> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun <T> Array<out T>.asSequenceLaw(): Sequence<T> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun BooleanArray.asSequenceLaw(): Sequence<Boolean> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun ByteArray.asSequenceLaw(): Sequence<Byte> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun CharArray.asSequenceLaw(): Sequence<Char> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun DoubleArray.asSequenceLaw(): Sequence<Double> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun FloatArray.asSequenceLaw(): Sequence<Float> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun IntArray.asSequenceLaw(): Sequence<Int> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun LongArray.asSequenceLaw(): Sequence<Long> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public fun ShortArray.asSequenceLaw(): Sequence<Short> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public inline fun <T, K, V> Array<out T>.associateLaw(transform: (T) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> BooleanArray.associateLaw(transform: (Boolean) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> ByteArray.associateLaw(transform: (Byte) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> CharArray.associateLaw(transform: (Char) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> DoubleArray.associateLaw(transform: (Double) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> FloatArray.associateLaw(transform: (Float) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> IntArray.associateLaw(transform: (Int) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> LongArray.associateLaw(transform: (Long) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <K, V> ShortArray.associateLaw(transform: (Short) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <T, K> Array<out T>.associateByLaw(keySelector: (T) -> K): Map<K, T> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <T, K, V> Array<out T>.associateByLaw(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> BooleanArray.associateByLaw(keySelector: (Boolean) -> K): Map<K, Boolean> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> BooleanArray.associateByLaw(
  keySelector: (Boolean) -> K,
  valueTransform: (Boolean) -> V
): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> ByteArray.associateByLaw(keySelector: (Byte) -> K): Map<K, Byte> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> ByteArray.associateByLaw(keySelector: (Byte) -> K, valueTransform: (Byte) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> CharArray.associateByLaw(keySelector: (Char) -> K): Map<K, Char> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> CharArray.associateByLaw(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> DoubleArray.associateByLaw(keySelector: (Double) -> K): Map<K, Double> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> DoubleArray.associateByLaw(
  keySelector: (Double) -> K,
  valueTransform: (Double) -> V
): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> FloatArray.associateByLaw(keySelector: (Float) -> K): Map<K, Float> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> FloatArray.associateByLaw(keySelector: (Float) -> K, valueTransform: (Float) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> IntArray.associateByLaw(keySelector: (Int) -> K): Map<K, Int> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> IntArray.associateByLaw(keySelector: (Int) -> K, valueTransform: (Int) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> LongArray.associateByLaw(keySelector: (Long) -> K): Map<K, Long> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> LongArray.associateByLaw(keySelector: (Long) -> K, valueTransform: (Long) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K> ShortArray.associateByLaw(keySelector: (Short) -> K): Map<K, Short> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <K, V> ShortArray.associateByLaw(keySelector: (Short) -> K, valueTransform: (Short) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <T, K, M : MutableMap<in K, in T>> Array<out T>.associateByToLaw(
  destination: M,
  keySelector: (T) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, in V>> Array<out T>.associateByToLaw(
  destination: M,
  keySelector: (T) -> K,
  valueTransform: (T) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Boolean>> BooleanArray.associateByToLaw(
  destination: M,
  keySelector: (Boolean) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> BooleanArray.associateByToLaw(
  destination: M,
  keySelector: (Boolean) -> K,
  valueTransform: (Boolean) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Byte>> ByteArray.associateByToLaw(
  destination: M,
  keySelector: (Byte) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> ByteArray.associateByToLaw(
  destination: M,
  keySelector: (Byte) -> K,
  valueTransform: (Byte) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Char>> CharArray.associateByToLaw(
  destination: M,
  keySelector: (Char) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> CharArray.associateByToLaw(
  destination: M,
  keySelector: (Char) -> K,
  valueTransform: (Char) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Double>> DoubleArray.associateByToLaw(
  destination: M,
  keySelector: (Double) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> DoubleArray.associateByToLaw(
  destination: M,
  keySelector: (Double) -> K,
  valueTransform: (Double) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Float>> FloatArray.associateByToLaw(
  destination: M,
  keySelector: (Float) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> FloatArray.associateByToLaw(
  destination: M,
  keySelector: (Float) -> K,
  valueTransform: (Float) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Int>> IntArray.associateByToLaw(
  destination: M,
  keySelector: (Int) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> IntArray.associateByToLaw(
  destination: M,
  keySelector: (Int) -> K,
  valueTransform: (Int) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Long>> LongArray.associateByToLaw(
  destination: M,
  keySelector: (Long) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> LongArray.associateByToLaw(
  destination: M,
  keySelector: (Long) -> K,
  valueTransform: (Long) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, in Short>> ShortArray.associateByToLaw(
  destination: M,
  keySelector: (Short) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> ShortArray.associateByToLaw(
  destination: M,
  keySelector: (Short) -> K,
  valueTransform: (Short) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, in V>> Array<out T>.associateToLaw(
  destination: M,
  transform: (T) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> BooleanArray.associateToLaw(
  destination: M,
  transform: (Boolean) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> ByteArray.associateToLaw(
  destination: M,
  transform: (Byte) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> CharArray.associateToLaw(
  destination: M,
  transform: (Char) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> DoubleArray.associateToLaw(
  destination: M,
  transform: (Double) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> FloatArray.associateToLaw(
  destination: M,
  transform: (Float) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> IntArray.associateToLaw(
  destination: M,
  transform: (Int) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> LongArray.associateToLaw(
  destination: M,
  transform: (Long) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> ShortArray.associateToLaw(
  destination: M,
  transform: (Short) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V> Array<out K>.associateWithLaw(valueSelector: (K) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> BooleanArray.associateWithLaw(valueSelector: (Boolean) -> V): Map<Boolean, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> ByteArray.associateWithLaw(valueSelector: (Byte) -> V): Map<Byte, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> CharArray.associateWithLaw(valueSelector: (Char) -> V): Map<Char, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> DoubleArray.associateWithLaw(valueSelector: (Double) -> V): Map<Double, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> FloatArray.associateWithLaw(valueSelector: (Float) -> V): Map<Float, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> IntArray.associateWithLaw(valueSelector: (Int) -> V): Map<Int, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> LongArray.associateWithLaw(valueSelector: (Long) -> V): Map<Long, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> ShortArray.associateWithLaw(valueSelector: (Short) -> V): Map<Short, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> Array<out K>.associateWithToLaw(
  destination: M,
  valueSelector: (K) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Boolean, in V>> BooleanArray.associateWithToLaw(
  destination: M,
  valueSelector: (Boolean) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Byte, in V>> ByteArray.associateWithToLaw(
  destination: M,
  valueSelector: (Byte) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Char, in V>> CharArray.associateWithToLaw(
  destination: M,
  valueSelector: (Char) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Double, in V>> DoubleArray.associateWithToLaw(
  destination: M,
  valueSelector: (Double) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Float, in V>> FloatArray.associateWithToLaw(
  destination: M,
  valueSelector: (Float) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Int, in V>> IntArray.associateWithToLaw(
  destination: M,
  valueSelector: (Int) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Long, in V>> LongArray.associateWithToLaw(
  destination: M,
  valueSelector: (Long) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in Short, in V>> ShortArray.associateWithToLaw(
  destination: M,
  valueSelector: (Short) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public fun Array<out Byte>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun Array<out Double>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun Array<out Float>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun Array<out Int>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun Array<out Long>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun Array<out Short>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun ByteArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun DoubleArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun FloatArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun IntArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun LongArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun ShortArray.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public inline fun <T> Array<out T>.component1Law(): T {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun BooleanArray.component1Law(): Boolean {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun ByteArray.component1Law(): Byte {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun CharArray.component1Law(): Char {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun DoubleArray.component1Law(): Double {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun FloatArray.component1Law(): Float {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun IntArray.component1Law(): Int {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun LongArray.component1Law(): Long {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun ShortArray.component1Law(): Short {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun <T> Array<out T>.component2Law(): T {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun BooleanArray.component2Law(): Boolean {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun ByteArray.component2Law(): Byte {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun CharArray.component2Law(): Char {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun DoubleArray.component2Law(): Double {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun FloatArray.component2Law(): Float {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun IntArray.component2Law(): Int {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun LongArray.component2Law(): Long {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun ShortArray.component2Law(): Short {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun <T> Array<out T>.component3Law(): T {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun BooleanArray.component3Law(): Boolean {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun ByteArray.component3Law(): Byte {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun CharArray.component3Law(): Char {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun DoubleArray.component3Law(): Double {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun FloatArray.component3Law(): Float {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun IntArray.component3Law(): Int {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun LongArray.component3Law(): Long {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun ShortArray.component3Law(): Short {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun <T> Array<out T>.component4Law(): T {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun BooleanArray.component4Law(): Boolean {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun ByteArray.component4Law(): Byte {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun CharArray.component4Law(): Char {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun DoubleArray.component4Law(): Double {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun FloatArray.component4Law(): Float {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun IntArray.component4Law(): Int {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun LongArray.component4Law(): Long {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun ShortArray.component4Law(): Short {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun <T> Array<out T>.component5Law(): T {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun BooleanArray.component5Law(): Boolean {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun ByteArray.component5Law(): Byte {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun CharArray.component5Law(): Char {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun DoubleArray.component5Law(): Double {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun FloatArray.component5Law(): Float {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun IntArray.component5Law(): Int {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun LongArray.component5Law(): Long {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun ShortArray.component5Law(): Short {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public fun <T> Array<out T>.containsLaw(element: T): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun BooleanArray.containsLaw(element: Boolean): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun ByteArray.containsLaw(element: Byte): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun CharArray.containsLaw(element: Char): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun IntArray.containsLaw(element: Int): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun LongArray.containsLaw(element: Long): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public fun ShortArray.containsLaw(element: Short): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public inline fun <T> Array<out T>.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun <T> Array<out T>.countLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun BooleanArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun BooleanArray.countLaw(predicate: (Boolean) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun ByteArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun ByteArray.countLaw(predicate: (Byte) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun CharArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun CharArray.countLaw(predicate: (Char) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun DoubleArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun DoubleArray.countLaw(predicate: (Double) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun FloatArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun FloatArray.countLaw(predicate: (Float) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun IntArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun IntArray.countLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun LongArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun LongArray.countLaw(predicate: (Long) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun ShortArray.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun ShortArray.countLaw(predicate: (Short) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public fun <T> Array<out T>.distinctLaw(): List<T> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun BooleanArray.distinctLaw(): List<Boolean> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun ByteArray.distinctLaw(): List<Byte> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun CharArray.distinctLaw(): List<Char> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun DoubleArray.distinctLaw(): List<Double> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun FloatArray.distinctLaw(): List<Float> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun IntArray.distinctLaw(): List<Int> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun LongArray.distinctLaw(): List<Long> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public fun ShortArray.distinctLaw(): List<Short> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public inline fun <T, K> Array<out T>.distinctByLaw(selector: (T) -> K): List<T> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> BooleanArray.distinctByLaw(selector: (Boolean) -> K): List<Boolean> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> ByteArray.distinctByLaw(selector: (Byte) -> K): List<Byte> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> CharArray.distinctByLaw(selector: (Char) -> K): List<Char> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> DoubleArray.distinctByLaw(selector: (Double) -> K): List<Double> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> FloatArray.distinctByLaw(selector: (Float) -> K): List<Float> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> IntArray.distinctByLaw(selector: (Int) -> K): List<Int> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> LongArray.distinctByLaw(selector: (Long) -> K): List<Long> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public inline fun <K> ShortArray.distinctByLaw(selector: (Short) -> K): List<Short> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public fun <T> Array<out T>.dropLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun BooleanArray.dropLaw(n: Int): List<Boolean> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun ByteArray.dropLaw(n: Int): List<Byte> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun CharArray.dropLaw(n: Int): List<Char> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun DoubleArray.dropLaw(n: Int): List<Double> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun FloatArray.dropLaw(n: Int): List<Float> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun IntArray.dropLaw(n: Int): List<Int> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun LongArray.dropLaw(n: Int): List<Long> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun ShortArray.dropLaw(n: Int): List<Short> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun <T> Array<out T>.dropLastLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun BooleanArray.dropLastLaw(n: Int): List<Boolean> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun ByteArray.dropLastLaw(n: Int): List<Byte> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun CharArray.dropLastLaw(n: Int): List<Char> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun DoubleArray.dropLastLaw(n: Int): List<Double> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun FloatArray.dropLastLaw(n: Int): List<Float> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun IntArray.dropLastLaw(n: Int): List<Int> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun LongArray.dropLastLaw(n: Int): List<Long> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun ShortArray.dropLastLaw(n: Int): List<Short> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public inline fun <T> Array<out T>.dropLastWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun BooleanArray.dropLastWhileLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun ByteArray.dropLastWhileLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun CharArray.dropLastWhileLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun DoubleArray.dropLastWhileLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun FloatArray.dropLastWhileLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun IntArray.dropLastWhileLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun LongArray.dropLastWhileLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun ShortArray.dropLastWhileLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun <T> Array<out T>.dropWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun BooleanArray.dropWhileLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun ByteArray.dropWhileLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun CharArray.dropWhileLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun DoubleArray.dropWhileLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun FloatArray.dropWhileLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun IntArray.dropWhileLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun LongArray.dropWhileLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun ShortArray.dropWhileLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun <T> Array<out T>.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> T): T {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun BooleanArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun ByteArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Byte): Byte {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun CharArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Char): Char {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun DoubleArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Double): Double {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun FloatArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Float): Float {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun IntArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Int): Int {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun LongArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Long): Long {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun ShortArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> Short): Short {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun <T> Array<out T>.elementAtOrNullLaw(index: Int): T? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.elementAtOrNullLaw(index: Int): Boolean? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun ByteArray.elementAtOrNullLaw(index: Int): Byte? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun CharArray.elementAtOrNullLaw(index: Int): Char? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.elementAtOrNullLaw(index: Int): Double? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun FloatArray.elementAtOrNullLaw(index: Int): Float? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun IntArray.elementAtOrNullLaw(index: Int): Int? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun LongArray.elementAtOrNullLaw(index: Int): Long? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun ShortArray.elementAtOrNullLaw(index: Int): Short? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.filterLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun BooleanArray.filterLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun ByteArray.filterLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun CharArray.filterLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun DoubleArray.filterLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun FloatArray.filterLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun IntArray.filterLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun LongArray.filterLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun ShortArray.filterLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun <T> Array<out T>.filterIndexedLaw(predicate: (index: Int, T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.filterIndexedLaw(predicate: (index: Int, Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun ByteArray.filterIndexedLaw(predicate: (index: Int, Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun CharArray.filterIndexedLaw(predicate: (index: Int, Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.filterIndexedLaw(predicate: (index: Int, Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun FloatArray.filterIndexedLaw(predicate: (index: Int, Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun IntArray.filterIndexedLaw(predicate: (index: Int, Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun LongArray.filterIndexedLaw(predicate: (index: Int, Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun ShortArray.filterIndexedLaw(predicate: (index: Int, Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Boolean) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Byte>> ByteArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Byte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Char>> CharArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Char) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Double>> DoubleArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Double) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Float>> FloatArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Float) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Int>> IntArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Int) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Long>> LongArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Long) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Short>> ShortArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, Short) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <reified R> Array<*>.filterIsInstanceLaw(): List<R> {
  pre(true) { "kotlin.collections.filterIsInstance pre-conditions" }
  return filterIsInstance<R>()
    .post({ true }, { "kotlin.collections.filterIsInstance post-conditions" })
}

@Law
public inline fun <reified R, C : MutableCollection<in R>> Array<*>.filterIsInstanceToLaw(destination: C): C {
  pre(true) { "kotlin.collections.filterIsInstanceTo pre-conditions" }
  return filterIsInstanceTo(destination)
    .post({ true }, { "kotlin.collections.filterIsInstanceTo post-conditions" })
}

@Law
public inline fun <T> Array<out T>.filterNotLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun BooleanArray.filterNotLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun ByteArray.filterNotLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun CharArray.filterNotLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun DoubleArray.filterNotLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun FloatArray.filterNotLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun IntArray.filterNotLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun LongArray.filterNotLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun ShortArray.filterNotLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public fun <T : Any> Array<out T?>.filterNotNullNullableLaw(): List<T> {
  pre(true) { "kotlin.collections.filterNotNull pre-conditions" }
  return filterNotNull()
    .post({ true }, { "kotlin.collections.filterNotNull post-conditions" })
}

@Law
public fun <C : MutableCollection<in T>, T : Any> Array<out T?>.filterNotNullToNullableLaw(destination: C): C {
  pre(true) { "kotlin.collections.filterNotNullTo pre-conditions" }
  return filterNotNullTo(destination)
    .post({ true }, { "kotlin.collections.filterNotNullTo post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterNotToLaw(
  destination: C,
  predicate: (T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterNotToLaw(
  destination: C,
  predicate: (Boolean) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Byte>> ByteArray.filterNotToLaw(
  destination: C,
  predicate: (Byte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Char>> CharArray.filterNotToLaw(
  destination: C,
  predicate: (Char) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Double>> DoubleArray.filterNotToLaw(
  destination: C,
  predicate: (Double) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Float>> FloatArray.filterNotToLaw(
  destination: C,
  predicate: (Float) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Int>> IntArray.filterNotToLaw(
  destination: C,
  predicate: (Int) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Long>> LongArray.filterNotToLaw(
  destination: C,
  predicate: (Long) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Short>> ShortArray.filterNotToLaw(
  destination: C,
  predicate: (Short) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterToLaw(
  destination: C,
  predicate: (T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterToLaw(
  destination: C,
  predicate: (Boolean) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Byte>> ByteArray.filterToLaw(
  destination: C,
  predicate: (Byte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Char>> CharArray.filterToLaw(
  destination: C,
  predicate: (Char) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Double>> DoubleArray.filterToLaw(
  destination: C,
  predicate: (Double) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Float>> FloatArray.filterToLaw(
  destination: C,
  predicate: (Float) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Int>> IntArray.filterToLaw(destination: C, predicate: (Int) -> Boolean): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Long>> LongArray.filterToLaw(
  destination: C,
  predicate: (Long) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in Short>> ShortArray.filterToLaw(
  destination: C,
  predicate: (Short) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <T> Array<out T>.findLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun BooleanArray.findLaw(predicate: (Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun ByteArray.findLaw(predicate: (Byte) -> Boolean): Byte? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun CharArray.findLaw(predicate: (Char) -> Boolean): Char? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun DoubleArray.findLaw(predicate: (Double) -> Boolean): Double? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun FloatArray.findLaw(predicate: (Float) -> Boolean): Float? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun IntArray.findLaw(predicate: (Int) -> Boolean): Int? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun LongArray.findLaw(predicate: (Long) -> Boolean): Long? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun ShortArray.findLaw(predicate: (Short) -> Boolean): Short? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun <T> Array<out T>.findLastLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun BooleanArray.findLastLaw(predicate: (Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun ByteArray.findLastLaw(predicate: (Byte) -> Boolean): Byte? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun CharArray.findLastLaw(predicate: (Char) -> Boolean): Char? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun DoubleArray.findLastLaw(predicate: (Double) -> Boolean): Double? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun FloatArray.findLastLaw(predicate: (Float) -> Boolean): Float? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun IntArray.findLastLaw(predicate: (Int) -> Boolean): Int? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun LongArray.findLastLaw(predicate: (Long) -> Boolean): Long? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun ShortArray.findLastLaw(predicate: (Short) -> Boolean): Short? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public fun <T> Array<out T>.firstLaw(): T {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun <T> Array<out T>.firstLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun BooleanArray.firstLaw(): Boolean {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun BooleanArray.firstLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun ByteArray.firstLaw(): Byte {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun ByteArray.firstLaw(predicate: (Byte) -> Boolean): Byte {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun CharArray.firstLaw(): Char {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun CharArray.firstLaw(predicate: (Char) -> Boolean): Char {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun DoubleArray.firstLaw(): Double {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun DoubleArray.firstLaw(predicate: (Double) -> Boolean): Double {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun FloatArray.firstLaw(): Float {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun FloatArray.firstLaw(predicate: (Float) -> Boolean): Float {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun IntArray.firstLaw(): Int {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun IntArray.firstLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun LongArray.firstLaw(): Long {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun LongArray.firstLaw(predicate: (Long) -> Boolean): Long {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun ShortArray.firstLaw(): Short {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun ShortArray.firstLaw(predicate: (Short) -> Boolean): Short {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun <T, R : Any> Array<out T>.firstNotNullOfLaw(transform: (T) -> R?): R {
  pre(true) { "kotlin.collections.firstNotNullOf pre-conditions" }
  return firstNotNullOf(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOf post-conditions" })
}

@Law
public inline fun <T, R : Any> Array<out T>.firstNotNullOfOrNullLaw(transform: (T) -> R?): R? {
  pre(true) { "kotlin.collections.firstNotNullOfOrNull pre-conditions" }
  return firstNotNullOfOrNull(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOfOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.firstOrNullLaw(): T? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.firstOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun BooleanArray.firstOrNullLaw(): Boolean? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.firstOrNullLaw(predicate: (Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun ByteArray.firstOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun ByteArray.firstOrNullLaw(predicate: (Byte) -> Boolean): Byte? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun CharArray.firstOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun CharArray.firstOrNullLaw(predicate: (Char) -> Boolean): Char? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun DoubleArray.firstOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.firstOrNullLaw(predicate: (Double) -> Boolean): Double? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun FloatArray.firstOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun FloatArray.firstOrNullLaw(predicate: (Float) -> Boolean): Float? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun IntArray.firstOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun IntArray.firstOrNullLaw(predicate: (Int) -> Boolean): Int? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun LongArray.firstOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun LongArray.firstOrNullLaw(predicate: (Long) -> Boolean): Long? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun ShortArray.firstOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun ShortArray.firstOrNullLaw(predicate: (Short) -> Boolean): Short? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
@JvmName("flatMapLawArrayIterable")
public inline fun <T, R> Array<out T>.flatMapLaw(transform: (T) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapLawArraySequence")
public inline fun <T, R> Array<out T>.flatMapLaw(transform: (T) -> Sequence<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapLawBooleanArrayIterable")
public inline fun <R> BooleanArray.flatMapLaw(transform: (Boolean) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> ByteArray.flatMapLaw(transform: (Byte) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> CharArray.flatMapLaw(transform: (Char) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> DoubleArray.flatMapLaw(transform: (Double) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> FloatArray.flatMapLaw(transform: (Float) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> IntArray.flatMapLaw(transform: (Int) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> LongArray.flatMapLaw(transform: (Long) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> ShortArray.flatMapLaw(transform: (Short) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapIndexedLawArrayIterable")
public inline fun <T, R> Array<out T>.flatMapIndexedLaw(transform: (index: Int, T) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
@JvmName("flatMapIndexedLawArraySequence")
public inline fun <T, R> Array<out T>.flatMapIndexedLaw(transform: (index: Int, T) -> Sequence<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.flatMapIndexedLaw(transform: (index: Int, Boolean) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.flatMapIndexedLaw(transform: (index: Int, Byte) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.flatMapIndexedLaw(transform: (index: Int, Char) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.flatMapIndexedLaw(transform: (index: Int, Double) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.flatMapIndexedLaw(transform: (index: Int, Float) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.flatMapIndexedLaw(transform: (index: Int, Int) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.flatMapIndexedLaw(transform: (index: Int, Long) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.flatMapIndexedLaw(transform: (index: Int, Short) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
@JvmName("flatMapIndexedToLawArrayIterable")
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
@JvmName("flatMapIndexedToLawArraySequence")
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> Sequence<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> BooleanArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Boolean) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ByteArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Byte) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> CharArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Char) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> DoubleArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Double) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> FloatArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Float) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> IntArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Int) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> LongArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Long) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ShortArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, Short) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
@JvmName("flatMapToLawArrayIterable")
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapToLaw(
  destination: C,
  transform: (T) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
@JvmName("flatMapToLawArraySequence")
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapToLaw(
  destination: C,
  transform: (T) -> Sequence<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> BooleanArray.flatMapToLaw(
  destination: C,
  transform: (Boolean) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ByteArray.flatMapToLaw(
  destination: C,
  transform: (Byte) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> CharArray.flatMapToLaw(
  destination: C,
  transform: (Char) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> DoubleArray.flatMapToLaw(
  destination: C,
  transform: (Double) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> FloatArray.flatMapToLaw(
  destination: C,
  transform: (Float) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> IntArray.flatMapToLaw(
  destination: C,
  transform: (Int) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> LongArray.flatMapToLaw(
  destination: C,
  transform: (Long) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ShortArray.flatMapToLaw(
  destination: C,
  transform: (Short) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.foldLaw(initial: R, operation: (acc: R, T) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> BooleanArray.foldLaw(initial: R, operation: (acc: R, Boolean) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> ByteArray.foldLaw(initial: R, operation: (acc: R, Byte) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> CharArray.foldLaw(initial: R, operation: (acc: R, Char) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> DoubleArray.foldLaw(initial: R, operation: (acc: R, Double) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> FloatArray.foldLaw(initial: R, operation: (acc: R, Float) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> IntArray.foldLaw(initial: R, operation: (acc: R, Int) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> LongArray.foldLaw(initial: R, operation: (acc: R, Long) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> ShortArray.foldLaw(initial: R, operation: (acc: R, Short) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, T) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Boolean) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Byte) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Char) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Double) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Float) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Int) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Long) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, Short) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.foldRightLaw(initial: R, operation: (T, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> BooleanArray.foldRightLaw(initial: R, operation: (Boolean, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> ByteArray.foldRightLaw(initial: R, operation: (Byte, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> CharArray.foldRightLaw(initial: R, operation: (Char, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> DoubleArray.foldRightLaw(initial: R, operation: (Double, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> FloatArray.foldRightLaw(initial: R, operation: (Float, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> IntArray.foldRightLaw(initial: R, operation: (Int, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> LongArray.foldRightLaw(initial: R, operation: (Long, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> ShortArray.foldRightLaw(initial: R, operation: (Short, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.foldRightIndexedLaw(initial: R, operation: (index: Int, T, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Boolean, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Byte, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Char, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Double, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Float, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Int, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Long, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.foldRightIndexedLaw(initial: R, operation: (index: Int, Short, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <T> Array<out T>.forEachLaw(action: (T) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun BooleanArray.forEachLaw(action: (Boolean) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun ByteArray.forEachLaw(action: (Byte) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun CharArray.forEachLaw(action: (Char) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun DoubleArray.forEachLaw(action: (Double) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun FloatArray.forEachLaw(action: (Float) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun IntArray.forEachLaw(action: (Int) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun LongArray.forEachLaw(action: (Long) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun ShortArray.forEachLaw(action: (Short) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun <T> Array<out T>.forEachIndexedLaw(action: (index: Int, T) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.forEachIndexedLaw(action: (index: Int, Boolean) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun ByteArray.forEachIndexedLaw(action: (index: Int, Byte) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun CharArray.forEachIndexedLaw(action: (index: Int, Char) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.forEachIndexedLaw(action: (index: Int, Double) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun FloatArray.forEachIndexedLaw(action: (index: Int, Float) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun IntArray.forEachIndexedLaw(action: (index: Int, Int) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun LongArray.forEachIndexedLaw(action: (index: Int, Long) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun ShortArray.forEachIndexedLaw(action: (index: Int, Short) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun <T> Array<out T>.getOrElseLaw(index: Int, defaultValue: (Int) -> T): T {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun BooleanArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun ByteArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Byte): Byte {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun CharArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Char): Char {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun DoubleArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Double): Double {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun FloatArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Float): Float {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun IntArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Int): Int {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun LongArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Long): Long {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun ShortArray.getOrElseLaw(index: Int, defaultValue: (Int) -> Short): Short {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public fun <T> Array<out T>.getOrNullLaw(index: Int): T? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun BooleanArray.getOrNullLaw(index: Int): Boolean? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun ByteArray.getOrNullLaw(index: Int): Byte? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun CharArray.getOrNullLaw(index: Int): Char? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun DoubleArray.getOrNullLaw(index: Int): Double? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun FloatArray.getOrNullLaw(index: Int): Float? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun IntArray.getOrNullLaw(index: Int): Int? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun LongArray.getOrNullLaw(index: Int): Long? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun ShortArray.getOrNullLaw(index: Int): Short? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public inline fun <T, K> Array<out T>.groupByLaw(keySelector: (T) -> K): Map<K, List<T>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <T, K, V> Array<out T>.groupByLaw(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> BooleanArray.groupByLaw(keySelector: (Boolean) -> K): Map<K, List<Boolean>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> BooleanArray.groupByLaw(
  keySelector: (Boolean) -> K,
  valueTransform: (Boolean) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> ByteArray.groupByLaw(keySelector: (Byte) -> K): Map<K, List<Byte>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> ByteArray.groupByLaw(keySelector: (Byte) -> K, valueTransform: (Byte) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> CharArray.groupByLaw(keySelector: (Char) -> K): Map<K, List<Char>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> CharArray.groupByLaw(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> DoubleArray.groupByLaw(keySelector: (Double) -> K): Map<K, List<Double>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> DoubleArray.groupByLaw(
  keySelector: (Double) -> K,
  valueTransform: (Double) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> FloatArray.groupByLaw(keySelector: (Float) -> K): Map<K, List<Float>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> FloatArray.groupByLaw(
  keySelector: (Float) -> K,
  valueTransform: (Float) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> IntArray.groupByLaw(keySelector: (Int) -> K): Map<K, List<Int>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> IntArray.groupByLaw(keySelector: (Int) -> K, valueTransform: (Int) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> LongArray.groupByLaw(keySelector: (Long) -> K): Map<K, List<Long>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> LongArray.groupByLaw(keySelector: (Long) -> K, valueTransform: (Long) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> ShortArray.groupByLaw(keySelector: (Short) -> K): Map<K, List<Short>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> ShortArray.groupByLaw(
  keySelector: (Short) -> K,
  valueTransform: (Short) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Array<out T>.groupByToLaw(
  destination: M,
  keySelector: (T) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Array<out T>.groupByToLaw(
  destination: M,
  keySelector: (T) -> K,
  valueTransform: (T) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Boolean>>> BooleanArray.groupByToLaw(
  destination: M,
  keySelector: (Boolean) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> BooleanArray.groupByToLaw(
  destination: M,
  keySelector: (Boolean) -> K,
  valueTransform: (Boolean) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Byte>>> ByteArray.groupByToLaw(
  destination: M,
  keySelector: (Byte) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ByteArray.groupByToLaw(
  destination: M,
  keySelector: (Byte) -> K,
  valueTransform: (Byte) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Char>>> CharArray.groupByToLaw(
  destination: M,
  keySelector: (Char) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> CharArray.groupByToLaw(
  destination: M,
  keySelector: (Char) -> K,
  valueTransform: (Char) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Double>>> DoubleArray.groupByToLaw(
  destination: M,
  keySelector: (Double) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> DoubleArray.groupByToLaw(
  destination: M,
  keySelector: (Double) -> K,
  valueTransform: (Double) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Float>>> FloatArray.groupByToLaw(
  destination: M,
  keySelector: (Float) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> FloatArray.groupByToLaw(
  destination: M,
  keySelector: (Float) -> K,
  valueTransform: (Float) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Int>>> IntArray.groupByToLaw(
  destination: M,
  keySelector: (Int) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> IntArray.groupByToLaw(
  destination: M,
  keySelector: (Int) -> K,
  valueTransform: (Int) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Long>>> LongArray.groupByToLaw(
  destination: M,
  keySelector: (Long) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> LongArray.groupByToLaw(
  destination: M,
  keySelector: (Long) -> K,
  valueTransform: (Long) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<Short>>> ShortArray.groupByToLaw(
  destination: M,
  keySelector: (Short) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ShortArray.groupByToLaw(
  destination: M,
  keySelector: (Short) -> K,
  valueTransform: (Short) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <T, K> Array<out T>.groupingByLaw(crossinline keySelector: (T) -> K): Grouping<T, K> {
  pre(true) { "kotlin.collections.groupingBy pre-conditions" }
  return groupingBy(keySelector)
    .post({ true }, { "kotlin.collections.groupingBy post-conditions" })
}

@Law
public fun <T> Array<out T>.indexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun BooleanArray.indexOfLaw(element: Boolean): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun ByteArray.indexOfLaw(element: Byte): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun CharArray.indexOfLaw(element: Char): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun IntArray.indexOfLaw(element: Int): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun LongArray.indexOfLaw(element: Long): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun ShortArray.indexOfLaw(element: Short): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.indexOfFirstLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun BooleanArray.indexOfFirstLaw(predicate: (Boolean) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun ByteArray.indexOfFirstLaw(predicate: (Byte) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun CharArray.indexOfFirstLaw(predicate: (Char) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun DoubleArray.indexOfFirstLaw(predicate: (Double) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun FloatArray.indexOfFirstLaw(predicate: (Float) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun IntArray.indexOfFirstLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun LongArray.indexOfFirstLaw(predicate: (Long) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun ShortArray.indexOfFirstLaw(predicate: (Short) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun <T> Array<out T>.indexOfLastLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun BooleanArray.indexOfLastLaw(predicate: (Boolean) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun ByteArray.indexOfLastLaw(predicate: (Byte) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun CharArray.indexOfLastLaw(predicate: (Char) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun DoubleArray.indexOfLastLaw(predicate: (Double) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun FloatArray.indexOfLastLaw(predicate: (Float) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun IntArray.indexOfLastLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun LongArray.indexOfLastLaw(predicate: (Long) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun ShortArray.indexOfLastLaw(predicate: (Short) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public infix fun <T> Array<out T>.intersectLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun BooleanArray.intersectLaw(other: Iterable<Boolean>): Set<Boolean> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun ByteArray.intersectLaw(other: Iterable<Byte>): Set<Byte> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun CharArray.intersectLaw(other: Iterable<Char>): Set<Char> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun DoubleArray.intersectLaw(other: Iterable<Double>): Set<Double> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun FloatArray.intersectLaw(other: Iterable<Float>): Set<Float> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun IntArray.intersectLaw(other: Iterable<Int>): Set<Int> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun LongArray.intersectLaw(other: Iterable<Long>): Set<Long> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public infix fun ShortArray.intersectLaw(other: Iterable<Short>): Set<Short> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public inline fun <T> Array<out T>.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun BooleanArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun ByteArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun CharArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun DoubleArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun FloatArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun IntArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun LongArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun ShortArray.isEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isEmpty pre-conditions" }
  return isEmpty()
    .post({ true }, { "kotlin.collections.isEmpty post-conditions" })
}

@Law
public inline fun <T> Array<out T>.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun BooleanArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun ByteArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun CharArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun DoubleArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun FloatArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun IntArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun LongArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun ShortArray.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public fun <T, A : Appendable /* = java.lang.Appendable */> Array<out T>.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((T) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> BooleanArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Boolean) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> ByteArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Byte) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> CharArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Char) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> DoubleArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Double) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> FloatArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Float) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> IntArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Int) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> LongArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Long) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <A : Appendable /* = java.lang.Appendable */> ShortArray.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Short) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <T> Array<out T>.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((T) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun BooleanArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Boolean) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun ByteArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Byte) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun CharArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Char) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun DoubleArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Double) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun FloatArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Float) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun IntArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Int) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun LongArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Long) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun ShortArray.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((Short) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun <T> Array<out T>.lastLaw(): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun <T> Array<out T>.lastLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun BooleanArray.lastLaw(): Boolean {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun BooleanArray.lastLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun ByteArray.lastLaw(): Byte {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun ByteArray.lastLaw(predicate: (Byte) -> Boolean): Byte {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun CharArray.lastLaw(): Char {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun CharArray.lastLaw(predicate: (Char) -> Boolean): Char {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun DoubleArray.lastLaw(): Double {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun DoubleArray.lastLaw(predicate: (Double) -> Boolean): Double {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun FloatArray.lastLaw(): Float {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun FloatArray.lastLaw(predicate: (Float) -> Boolean): Float {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun IntArray.lastLaw(): Int {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun IntArray.lastLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun LongArray.lastLaw(): Long {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun LongArray.lastLaw(predicate: (Long) -> Boolean): Long {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun ShortArray.lastLaw(): Short {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun ShortArray.lastLaw(predicate: (Short) -> Boolean): Short {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun <T> Array<out T>.lastIndexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun BooleanArray.lastIndexOfLaw(element: Boolean): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun ByteArray.lastIndexOfLaw(element: Byte): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun CharArray.lastIndexOfLaw(element: Char): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun IntArray.lastIndexOfLaw(element: Int): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun LongArray.lastIndexOfLaw(element: Long): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun ShortArray.lastIndexOfLaw(element: Short): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun <T> Array<out T>.lastOrNullLaw(): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.lastOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun BooleanArray.lastOrNullLaw(): Boolean? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.lastOrNullLaw(predicate: (Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun ByteArray.lastOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun ByteArray.lastOrNullLaw(predicate: (Byte) -> Boolean): Byte? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun CharArray.lastOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun CharArray.lastOrNullLaw(predicate: (Char) -> Boolean): Char? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun DoubleArray.lastOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.lastOrNullLaw(predicate: (Double) -> Boolean): Double? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun FloatArray.lastOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun FloatArray.lastOrNullLaw(predicate: (Float) -> Boolean): Float? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun IntArray.lastOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun IntArray.lastOrNullLaw(predicate: (Int) -> Boolean): Int? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun LongArray.lastOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun LongArray.lastOrNullLaw(predicate: (Long) -> Boolean): Long? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun ShortArray.lastOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun ShortArray.lastOrNullLaw(predicate: (Short) -> Boolean): Short? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.mapLaw(transform: (T) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> BooleanArray.mapLaw(transform: (Boolean) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> ByteArray.mapLaw(transform: (Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> CharArray.mapLaw(transform: (Char) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> DoubleArray.mapLaw(transform: (Double) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> FloatArray.mapLaw(transform: (Float) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> IntArray.mapLaw(transform: (Int) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> LongArray.mapLaw(transform: (Long) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> ShortArray.mapLaw(transform: (Short) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.mapIndexedLaw(transform: (index: Int, T) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.mapIndexedLaw(transform: (index: Int, Boolean) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.mapIndexedLaw(transform: (index: Int, Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.mapIndexedLaw(transform: (index: Int, Char) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.mapIndexedLaw(transform: (index: Int, Double) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.mapIndexedLaw(transform: (index: Int, Float) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.mapIndexedLaw(transform: (index: Int, Int) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.mapIndexedLaw(transform: (index: Int, Long) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.mapIndexedLaw(transform: (index: Int, Short) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <T, R : Any> Array<out T>.mapIndexedNotNullLaw(transform: (index: Int, T) -> R?): List<R> {
  pre(true) { "kotlin.collections.mapIndexedNotNull pre-conditions" }
  return mapIndexedNotNull(transform)
    .post({ true }, { "kotlin.collections.mapIndexedNotNull post-conditions" })
}

@Law
public inline fun <T, R : Any, C : MutableCollection<in R>> Array<out T>.mapIndexedNotNullToLaw(
  destination: C,
  transform: (index: Int, T) -> R?
): C {
  pre(true) { "kotlin.collections.mapIndexedNotNullTo pre-conditions" }
  return mapIndexedNotNullTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedNotNullTo post-conditions" })
}

@Law
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> BooleanArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Boolean) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ByteArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Byte) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> CharArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Char) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> DoubleArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Double) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> FloatArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Float) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> IntArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Int) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> LongArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Long) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ShortArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, Short) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <T, R : Any> Array<out T>.mapNotNullLaw(transform: (T) -> R?): List<R> {
  pre(true) { "kotlin.collections.mapNotNull pre-conditions" }
  return mapNotNull(transform)
    .post({ true }, { "kotlin.collections.mapNotNull post-conditions" })
}

@Law
public inline fun <T, R : Any, C : MutableCollection<in R>> Array<out T>.mapNotNullToLaw(
  destination: C,
  transform: (T) -> R?
): C {
  pre(true) { "kotlin.collections.mapNotNullTo pre-conditions" }
  return mapNotNullTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapNotNullTo post-conditions" })
}

@Law
public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.mapToLaw(destination: C, transform: (T) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> BooleanArray.mapToLaw(destination: C, transform: (Boolean) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ByteArray.mapToLaw(destination: C, transform: (Byte) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> CharArray.mapToLaw(destination: C, transform: (Char) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> DoubleArray.mapToLaw(destination: C, transform: (Double) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> FloatArray.mapToLaw(destination: C, transform: (Float) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> IntArray.mapToLaw(destination: C, transform: (Int) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> LongArray.mapToLaw(destination: C, transform: (Long) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ShortArray.mapToLaw(destination: C, transform: (Short) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.maxByOrNullLaw(selector: (T) -> R): T? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.maxByOrNullLaw(selector: (Boolean) -> R): Boolean? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.maxByOrNullLaw(selector: (Byte) -> R): Byte? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.maxByOrNullLaw(selector: (Char) -> R): Char? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.maxByOrNullLaw(selector: (Double) -> R): Double? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.maxByOrNullLaw(selector: (Float) -> R): Float? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.maxByOrNullLaw(selector: (Int) -> R): Int? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.maxByOrNullLaw(selector: (Long) -> R): Long? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.maxByOrNullLaw(selector: (Short) -> R): Short? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.maxOfLaw(selector: (T) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.maxOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.maxOfLaw(selector: (T) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.maxOfLaw(selector: (Boolean) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun BooleanArray.maxOfLaw(selector: (Boolean) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun BooleanArray.maxOfLaw(selector: (Boolean) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.maxOfLaw(selector: (Byte) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ByteArray.maxOfLaw(selector: (Byte) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ByteArray.maxOfLaw(selector: (Byte) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.maxOfLaw(selector: (Char) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun CharArray.maxOfLaw(selector: (Char) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun CharArray.maxOfLaw(selector: (Char) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.maxOfLaw(selector: (Double) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun DoubleArray.maxOfLaw(selector: (Double) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun DoubleArray.maxOfLaw(selector: (Double) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.maxOfLaw(selector: (Float) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun FloatArray.maxOfLaw(selector: (Float) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun FloatArray.maxOfLaw(selector: (Float) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.maxOfLaw(selector: (Int) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun IntArray.maxOfLaw(selector: (Int) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun IntArray.maxOfLaw(selector: (Int) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.maxOfLaw(selector: (Long) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun LongArray.maxOfLaw(selector: (Long) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun LongArray.maxOfLaw(selector: (Long) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.maxOfLaw(selector: (Short) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ShortArray.maxOfLaw(selector: (Short) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ShortArray.maxOfLaw(selector: (Short) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.maxOfOrNullLaw(selector: (T) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.maxOfOrNullLaw(selector: (T) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.maxOfOrNullLaw(selector: (T) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.maxOfOrNullLaw(selector: (Boolean) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.maxOfOrNullLaw(selector: (Boolean) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.maxOfOrNullLaw(selector: (Boolean) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.maxOfOrNullLaw(selector: (Byte) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ByteArray.maxOfOrNullLaw(selector: (Byte) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ByteArray.maxOfOrNullLaw(selector: (Byte) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.maxOfOrNullLaw(selector: (Char) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun CharArray.maxOfOrNullLaw(selector: (Char) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun CharArray.maxOfOrNullLaw(selector: (Char) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.maxOfOrNullLaw(selector: (Double) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.maxOfOrNullLaw(selector: (Double) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.maxOfOrNullLaw(selector: (Double) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.maxOfOrNullLaw(selector: (Float) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun FloatArray.maxOfOrNullLaw(selector: (Float) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun FloatArray.maxOfOrNullLaw(selector: (Float) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.maxOfOrNullLaw(selector: (Int) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun IntArray.maxOfOrNullLaw(selector: (Int) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun IntArray.maxOfOrNullLaw(selector: (Int) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.maxOfOrNullLaw(selector: (Long) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun LongArray.maxOfOrNullLaw(selector: (Long) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun LongArray.maxOfOrNullLaw(selector: (Long) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.maxOfOrNullLaw(selector: (Short) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ShortArray.maxOfOrNullLaw(selector: (Short) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ShortArray.maxOfOrNullLaw(selector: (Short) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> BooleanArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Boolean) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> ByteArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Byte) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> CharArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Char) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> DoubleArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Double) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> FloatArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Float) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> IntArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Int) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> LongArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Long) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> ShortArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Short) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> BooleanArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Boolean) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ByteArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Byte) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> CharArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Char) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> DoubleArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Double) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> FloatArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Float) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> IntArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Int) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> LongArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Long) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ShortArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Short) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.maxOrNullLaw(): T? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun Array<out Double>.maxOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun Array<out Float>.maxOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun ByteArray.maxOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun CharArray.maxOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun DoubleArray.maxOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun FloatArray.maxOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun IntArray.maxOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun LongArray.maxOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun ShortArray.maxOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.maxWithOrNullLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): T? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun BooleanArray.maxWithOrNullLaw(comparator: Comparator<in Boolean> /* = java.util.Comparator<in Boolean> */): Boolean? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun ByteArray.maxWithOrNullLaw(comparator: Comparator<in Byte> /* = java.util.Comparator<in Byte> */): Byte? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun CharArray.maxWithOrNullLaw(comparator: Comparator<in Char> /* = java.util.Comparator<in Char> */): Char? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun DoubleArray.maxWithOrNullLaw(comparator: Comparator<in Double> /* = java.util.Comparator<in Double> */): Double? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun FloatArray.maxWithOrNullLaw(comparator: Comparator<in Float> /* = java.util.Comparator<in Float> */): Float? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun IntArray.maxWithOrNullLaw(comparator: Comparator<in Int> /* = java.util.Comparator<in Int> */): Int? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun LongArray.maxWithOrNullLaw(comparator: Comparator<in Long> /* = java.util.Comparator<in Long> */): Long? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun ShortArray.maxWithOrNullLaw(comparator: Comparator<in Short> /* = java.util.Comparator<in Short> */): Short? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.minByOrNullLaw(selector: (T) -> R): T? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.minByOrNullLaw(selector: (Boolean) -> R): Boolean? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.minByOrNullLaw(selector: (Byte) -> R): Byte? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.minByOrNullLaw(selector: (Char) -> R): Char? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.minByOrNullLaw(selector: (Double) -> R): Double? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.minByOrNullLaw(selector: (Float) -> R): Float? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.minByOrNullLaw(selector: (Int) -> R): Int? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.minByOrNullLaw(selector: (Long) -> R): Long? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.minByOrNullLaw(selector: (Short) -> R): Short? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.minOfLaw(selector: (T) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.minOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.minOfLaw(selector: (T) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.minOfLaw(selector: (Boolean) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun BooleanArray.minOfLaw(selector: (Boolean) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun BooleanArray.minOfLaw(selector: (Boolean) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.minOfLaw(selector: (Byte) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ByteArray.minOfLaw(selector: (Byte) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ByteArray.minOfLaw(selector: (Byte) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.minOfLaw(selector: (Char) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun CharArray.minOfLaw(selector: (Char) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun CharArray.minOfLaw(selector: (Char) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.minOfLaw(selector: (Double) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun DoubleArray.minOfLaw(selector: (Double) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun DoubleArray.minOfLaw(selector: (Double) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.minOfLaw(selector: (Float) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun FloatArray.minOfLaw(selector: (Float) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun FloatArray.minOfLaw(selector: (Float) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.minOfLaw(selector: (Int) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun IntArray.minOfLaw(selector: (Int) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun IntArray.minOfLaw(selector: (Int) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.minOfLaw(selector: (Long) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun LongArray.minOfLaw(selector: (Long) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun LongArray.minOfLaw(selector: (Long) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.minOfLaw(selector: (Short) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ShortArray.minOfLaw(selector: (Short) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ShortArray.minOfLaw(selector: (Short) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.minOfOrNullLaw(selector: (T) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.minOfOrNullLaw(selector: (T) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.minOfOrNullLaw(selector: (T) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.minOfOrNullLaw(selector: (Boolean) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.minOfOrNullLaw(selector: (Boolean) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.minOfOrNullLaw(selector: (Boolean) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.minOfOrNullLaw(selector: (Byte) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ByteArray.minOfOrNullLaw(selector: (Byte) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ByteArray.minOfOrNullLaw(selector: (Byte) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.minOfOrNullLaw(selector: (Char) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun CharArray.minOfOrNullLaw(selector: (Char) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun CharArray.minOfOrNullLaw(selector: (Char) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.minOfOrNullLaw(selector: (Double) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.minOfOrNullLaw(selector: (Double) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.minOfOrNullLaw(selector: (Double) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.minOfOrNullLaw(selector: (Float) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun FloatArray.minOfOrNullLaw(selector: (Float) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun FloatArray.minOfOrNullLaw(selector: (Float) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.minOfOrNullLaw(selector: (Int) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun IntArray.minOfOrNullLaw(selector: (Int) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun IntArray.minOfOrNullLaw(selector: (Int) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.minOfOrNullLaw(selector: (Long) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun LongArray.minOfOrNullLaw(selector: (Long) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun LongArray.minOfOrNullLaw(selector: (Long) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.minOfOrNullLaw(selector: (Short) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ShortArray.minOfOrNullLaw(selector: (Short) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ShortArray.minOfOrNullLaw(selector: (Short) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> BooleanArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Boolean) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> ByteArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Byte) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> CharArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Char) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> DoubleArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Double) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> FloatArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Float) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> IntArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Int) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> LongArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Long) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> ShortArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Short) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> BooleanArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Boolean) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ByteArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Byte) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> CharArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Char) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> DoubleArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Double) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> FloatArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Float) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> IntArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Int) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> LongArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Long) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ShortArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Short) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.minOrNullLaw(): T? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun Array<out Double>.minOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun Array<out Float>.minOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun ByteArray.minOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun CharArray.minOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun DoubleArray.minOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun FloatArray.minOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun IntArray.minOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun LongArray.minOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun ShortArray.minOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.minWithOrNullLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): T? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun BooleanArray.minWithOrNullLaw(comparator: Comparator<in Boolean> /* = java.util.Comparator<in Boolean> */): Boolean? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun ByteArray.minWithOrNullLaw(comparator: Comparator<in Byte> /* = java.util.Comparator<in Byte> */): Byte? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun CharArray.minWithOrNullLaw(comparator: Comparator<in Char> /* = java.util.Comparator<in Char> */): Char? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun DoubleArray.minWithOrNullLaw(comparator: Comparator<in Double> /* = java.util.Comparator<in Double> */): Double? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun FloatArray.minWithOrNullLaw(comparator: Comparator<in Float> /* = java.util.Comparator<in Float> */): Float? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun IntArray.minWithOrNullLaw(comparator: Comparator<in Int> /* = java.util.Comparator<in Int> */): Int? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun LongArray.minWithOrNullLaw(comparator: Comparator<in Long> /* = java.util.Comparator<in Long> */): Long? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun ShortArray.minWithOrNullLaw(comparator: Comparator<in Short> /* = java.util.Comparator<in Short> */): Short? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <T> Array<out T>.noneLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun BooleanArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun BooleanArray.noneLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun ByteArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun ByteArray.noneLaw(predicate: (Byte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun CharArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun CharArray.noneLaw(predicate: (Char) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun DoubleArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun DoubleArray.noneLaw(predicate: (Double) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun FloatArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun FloatArray.noneLaw(predicate: (Float) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun IntArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun IntArray.noneLaw(predicate: (Int) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun LongArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun LongArray.noneLaw(predicate: (Long) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public fun ShortArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun ShortArray.noneLaw(predicate: (Short) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <T> Array<out T>.onEachLaw(action: (T) -> Unit): Array<out T> {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun BooleanArray.onEachLaw(action: (Boolean) -> Unit): BooleanArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun ByteArray.onEachLaw(action: (Byte) -> Unit): ByteArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun CharArray.onEachLaw(action: (Char) -> Unit): CharArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun DoubleArray.onEachLaw(action: (Double) -> Unit): DoubleArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun FloatArray.onEachLaw(action: (Float) -> Unit): FloatArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun IntArray.onEachLaw(action: (Int) -> Unit): IntArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun LongArray.onEachLaw(action: (Long) -> Unit): LongArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun ShortArray.onEachLaw(action: (Short) -> Unit): ShortArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun <T> Array<out T>.onEachIndexedLaw(action: (index: Int, T) -> Unit): Array<out T> {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.onEachIndexedLaw(action: (index: Int, Boolean) -> Unit): BooleanArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun ByteArray.onEachIndexedLaw(action: (index: Int, Byte) -> Unit): ByteArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun CharArray.onEachIndexedLaw(action: (index: Int, Char) -> Unit): CharArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.onEachIndexedLaw(action: (index: Int, Double) -> Unit): DoubleArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun FloatArray.onEachIndexedLaw(action: (index: Int, Float) -> Unit): FloatArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun IntArray.onEachIndexedLaw(action: (index: Int, Int) -> Unit): IntArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun LongArray.onEachIndexedLaw(action: (index: Int, Long) -> Unit): LongArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun ShortArray.onEachIndexedLaw(action: (index: Int, Short) -> Unit): ShortArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun <T> Array<out T>.partitionLaw(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun BooleanArray.partitionLaw(predicate: (Boolean) -> Boolean): Pair<List<Boolean>, List<Boolean>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun ByteArray.partitionLaw(predicate: (Byte) -> Boolean): Pair<List<Byte>, List<Byte>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun CharArray.partitionLaw(predicate: (Char) -> Boolean): Pair<List<Char>, List<Char>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun DoubleArray.partitionLaw(predicate: (Double) -> Boolean): Pair<List<Double>, List<Double>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun FloatArray.partitionLaw(predicate: (Float) -> Boolean): Pair<List<Float>, List<Float>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun IntArray.partitionLaw(predicate: (Int) -> Boolean): Pair<List<Int>, List<Int>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun LongArray.partitionLaw(predicate: (Long) -> Boolean): Pair<List<Long>, List<Long>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun ShortArray.partitionLaw(predicate: (Short) -> Boolean): Pair<List<Short>, List<Short>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public inline fun <T> Array<out T>.randomLaw(): T {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun <T> Array<out T>.randomLaw(random: Random): T {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun BooleanArray.randomLaw(): Boolean {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun BooleanArray.randomLaw(random: Random): Boolean {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun ByteArray.randomLaw(): Byte {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun ByteArray.randomLaw(random: Random): Byte {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun CharArray.randomLaw(): Char {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun CharArray.randomLaw(random: Random): Char {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun DoubleArray.randomLaw(): Double {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun DoubleArray.randomLaw(random: Random): Double {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun FloatArray.randomLaw(): Float {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun FloatArray.randomLaw(random: Random): Float {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun IntArray.randomLaw(): Int {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun IntArray.randomLaw(random: Random): Int {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun LongArray.randomLaw(): Long {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun LongArray.randomLaw(random: Random): Long {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun ShortArray.randomLaw(): Short {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun ShortArray.randomLaw(random: Random): Short {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun <T> Array<out T>.randomOrNullLaw(): T? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.randomOrNullLaw(random: Random): T? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.randomOrNullLaw(): Boolean? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun BooleanArray.randomOrNullLaw(random: Random): Boolean? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun ByteArray.randomOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun ByteArray.randomOrNullLaw(random: Random): Byte? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun CharArray.randomOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun CharArray.randomOrNullLaw(random: Random): Char? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.randomOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun DoubleArray.randomOrNullLaw(random: Random): Double? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun FloatArray.randomOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun FloatArray.randomOrNullLaw(random: Random): Float? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun IntArray.randomOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun IntArray.randomOrNullLaw(random: Random): Int? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun LongArray.randomOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun LongArray.randomOrNullLaw(random: Random): Long? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun ShortArray.randomOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun ShortArray.randomOrNullLaw(random: Random): Short? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceLaw(operation: (acc: S, T) -> S): S {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun BooleanArray.reduceLaw(operation: (acc: Boolean, Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun ByteArray.reduceLaw(operation: (acc: Byte, Byte) -> Byte): Byte {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun CharArray.reduceLaw(operation: (acc: Char, Char) -> Char): Char {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun DoubleArray.reduceLaw(operation: (acc: Double, Double) -> Double): Double {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun FloatArray.reduceLaw(operation: (acc: Float, Float) -> Float): Float {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun IntArray.reduceLaw(operation: (acc: Int, Int) -> Int): Int {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun LongArray.reduceLaw(operation: (acc: Long, Long) -> Long): Long {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun ShortArray.reduceLaw(operation: (acc: Short, Short) -> Short): Short {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceIndexedLaw(operation: (index: Int, acc: S, T) -> S): S {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.reduceIndexedLaw(operation: (index: Int, acc: Boolean, Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun ByteArray.reduceIndexedLaw(operation: (index: Int, acc: Byte, Byte) -> Byte): Byte {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun CharArray.reduceIndexedLaw(operation: (index: Int, acc: Char, Char) -> Char): Char {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.reduceIndexedLaw(operation: (index: Int, acc: Double, Double) -> Double): Double {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun FloatArray.reduceIndexedLaw(operation: (index: Int, acc: Float, Float) -> Float): Float {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun IntArray.reduceIndexedLaw(operation: (index: Int, acc: Int, Int) -> Int): Int {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun LongArray.reduceIndexedLaw(operation: (index: Int, acc: Long, Long) -> Long): Long {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun ShortArray.reduceIndexedLaw(operation: (index: Int, acc: Short, Short) -> Short): Short {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceIndexedOrNullLaw(operation: (index: Int, acc: S, T) -> S): S? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Boolean, Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun ByteArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Byte, Byte) -> Byte): Byte? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun CharArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Char, Char) -> Char): Char? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Double, Double) -> Double): Double? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun FloatArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Float, Float) -> Float): Float? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun IntArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Int, Int) -> Int): Int? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun LongArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Long, Long) -> Long): Long? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun ShortArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: Short, Short) -> Short): Short? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceOrNullLaw(operation: (acc: S, T) -> S): S? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.reduceOrNullLaw(operation: (acc: Boolean, Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun ByteArray.reduceOrNullLaw(operation: (acc: Byte, Byte) -> Byte): Byte? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun CharArray.reduceOrNullLaw(operation: (acc: Char, Char) -> Char): Char? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.reduceOrNullLaw(operation: (acc: Double, Double) -> Double): Double? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun FloatArray.reduceOrNullLaw(operation: (acc: Float, Float) -> Float): Float? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun IntArray.reduceOrNullLaw(operation: (acc: Int, Int) -> Int): Int? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun LongArray.reduceOrNullLaw(operation: (acc: Long, Long) -> Long): Long? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun ShortArray.reduceOrNullLaw(operation: (acc: Short, Short) -> Short): Short? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceRightLaw(operation: (T, acc: S) -> S): S {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun BooleanArray.reduceRightLaw(operation: (Boolean, acc: Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun ByteArray.reduceRightLaw(operation: (Byte, acc: Byte) -> Byte): Byte {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun CharArray.reduceRightLaw(operation: (Char, acc: Char) -> Char): Char {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun DoubleArray.reduceRightLaw(operation: (Double, acc: Double) -> Double): Double {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun FloatArray.reduceRightLaw(operation: (Float, acc: Float) -> Float): Float {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun IntArray.reduceRightLaw(operation: (Int, acc: Int) -> Int): Int {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun LongArray.reduceRightLaw(operation: (Long, acc: Long) -> Long): Long {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun ShortArray.reduceRightLaw(operation: (Short, acc: Short) -> Short): Short {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceRightIndexedLaw(operation: (index: Int, T, acc: S) -> S): S {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.reduceRightIndexedLaw(operation: (index: Int, Boolean, acc: Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun ByteArray.reduceRightIndexedLaw(operation: (index: Int, Byte, acc: Byte) -> Byte): Byte {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun CharArray.reduceRightIndexedLaw(operation: (index: Int, Char, acc: Char) -> Char): Char {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.reduceRightIndexedLaw(operation: (index: Int, Double, acc: Double) -> Double): Double {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun FloatArray.reduceRightIndexedLaw(operation: (index: Int, Float, acc: Float) -> Float): Float {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun IntArray.reduceRightIndexedLaw(operation: (index: Int, Int, acc: Int) -> Int): Int {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun LongArray.reduceRightIndexedLaw(operation: (index: Int, Long, acc: Long) -> Long): Long {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun ShortArray.reduceRightIndexedLaw(operation: (index: Int, Short, acc: Short) -> Short): Short {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceRightIndexedOrNullLaw(operation: (index: Int, T, acc: S) -> S): S? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Boolean, acc: Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun ByteArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Byte, acc: Byte) -> Byte): Byte? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun CharArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Char, acc: Char) -> Char): Char? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Double, acc: Double) -> Double): Double? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun FloatArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Float, acc: Float) -> Float): Float? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun IntArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Int, acc: Int) -> Int): Int? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun LongArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Long, acc: Long) -> Long): Long? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun ShortArray.reduceRightIndexedOrNullLaw(operation: (index: Int, Short, acc: Short) -> Short): Short? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.reduceRightOrNullLaw(operation: (T, acc: S) -> S): S? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.reduceRightOrNullLaw(operation: (Boolean, acc: Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun ByteArray.reduceRightOrNullLaw(operation: (Byte, acc: Byte) -> Byte): Byte? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun CharArray.reduceRightOrNullLaw(operation: (Char, acc: Char) -> Char): Char? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.reduceRightOrNullLaw(operation: (Double, acc: Double) -> Double): Double? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun FloatArray.reduceRightOrNullLaw(operation: (Float, acc: Float) -> Float): Float? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun IntArray.reduceRightOrNullLaw(operation: (Int, acc: Int) -> Int): Int? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun LongArray.reduceRightOrNullLaw(operation: (Long, acc: Long) -> Long): Long? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun ShortArray.reduceRightOrNullLaw(operation: (Short, acc: Short) -> Short): Short? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public fun <T : Any> Array<T?>.requireNoNullsNullableLaw(): Array<T> {
  pre(true) { "kotlin.collections.requireNoNulls pre-conditions" }
  return requireNoNulls()
    .post({ true }, { "kotlin.collections.requireNoNulls post-conditions" })
}

@Law
public fun <T> Array<T>.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun <T> Array<T>.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun BooleanArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun BooleanArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun ByteArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun ByteArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun CharArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun CharArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun DoubleArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun DoubleArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun FloatArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun FloatArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun IntArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun IntArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun LongArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun LongArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun ShortArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun ShortArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun <T> Array<out T>.reversedLaw(): List<T> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun BooleanArray.reversedLaw(): List<Boolean> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun ByteArray.reversedLaw(): List<Byte> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun CharArray.reversedLaw(): List<Char> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun DoubleArray.reversedLaw(): List<Double> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun FloatArray.reversedLaw(): List<Float> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun IntArray.reversedLaw(): List<Int> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun LongArray.reversedLaw(): List<Long> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun ShortArray.reversedLaw(): List<Short> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun <T> Array<T>.reversedArrayLaw(): Array<T> {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun BooleanArray.reversedArrayLaw(): BooleanArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun ByteArray.reversedArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun CharArray.reversedArrayLaw(): CharArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun DoubleArray.reversedArrayLaw(): DoubleArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun FloatArray.reversedArrayLaw(): FloatArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun IntArray.reversedArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun LongArray.reversedArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public fun ShortArray.reversedArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.runningFoldLaw(initial: R, operation: (acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> BooleanArray.runningFoldLaw(initial: R, operation: (acc: R, Boolean) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> ByteArray.runningFoldLaw(initial: R, operation: (acc: R, Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> CharArray.runningFoldLaw(initial: R, operation: (acc: R, Char) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> DoubleArray.runningFoldLaw(initial: R, operation: (acc: R, Double) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> FloatArray.runningFoldLaw(initial: R, operation: (acc: R, Float) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> IntArray.runningFoldLaw(initial: R, operation: (acc: R, Int) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> LongArray.runningFoldLaw(initial: R, operation: (acc: R, Long) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> ShortArray.runningFoldLaw(initial: R, operation: (acc: R, Short) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, T) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, Boolean) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.runningFoldIndexedLaw(initial: R, operation: (index: Int, acc: R, Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.runningFoldIndexedLaw(initial: R, operation: (index: Int, acc: R, Char) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, Double) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, Float) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.runningFoldIndexedLaw(initial: R, operation: (index: Int, acc: R, Int) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.runningFoldIndexedLaw(initial: R, operation: (index: Int, acc: R, Long) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, Short) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.runningReduceLaw(operation: (acc: S, T) -> S): List<S> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun BooleanArray.runningReduceLaw(operation: (acc: Boolean, Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun ByteArray.runningReduceLaw(operation: (acc: Byte, Byte) -> Byte): List<Byte> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun CharArray.runningReduceLaw(operation: (acc: Char, Char) -> Char): List<Char> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun DoubleArray.runningReduceLaw(operation: (acc: Double, Double) -> Double): List<Double> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun FloatArray.runningReduceLaw(operation: (acc: Float, Float) -> Float): List<Float> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun IntArray.runningReduceLaw(operation: (acc: Int, Int) -> Int): List<Int> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun LongArray.runningReduceLaw(operation: (acc: Long, Long) -> Long): List<Long> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun ShortArray.runningReduceLaw(operation: (acc: Short, Short) -> Short): List<Short> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun <S, T : S> Array<out T>.runningReduceIndexedLaw(operation: (index: Int, acc: S, T) -> S): List<S> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun BooleanArray.runningReduceIndexedLaw(operation: (index: Int, acc: Boolean, Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun ByteArray.runningReduceIndexedLaw(operation: (index: Int, acc: Byte, Byte) -> Byte): List<Byte> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun CharArray.runningReduceIndexedLaw(operation: (index: Int, acc: Char, Char) -> Char): List<Char> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun DoubleArray.runningReduceIndexedLaw(operation: (index: Int, acc: Double, Double) -> Double): List<Double> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun FloatArray.runningReduceIndexedLaw(operation: (index: Int, acc: Float, Float) -> Float): List<Float> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun IntArray.runningReduceIndexedLaw(operation: (index: Int, acc: Int, Int) -> Int): List<Int> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun LongArray.runningReduceIndexedLaw(operation: (index: Int, acc: Long, Long) -> Long): List<Long> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun ShortArray.runningReduceIndexedLaw(operation: (index: Int, acc: Short, Short) -> Short): List<Short> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.scanLaw(initial: R, operation: (acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> BooleanArray.scanLaw(initial: R, operation: (acc: R, Boolean) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> ByteArray.scanLaw(initial: R, operation: (acc: R, Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> CharArray.scanLaw(initial: R, operation: (acc: R, Char) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> DoubleArray.scanLaw(initial: R, operation: (acc: R, Double) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> FloatArray.scanLaw(initial: R, operation: (acc: R, Float) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> IntArray.scanLaw(initial: R, operation: (acc: R, Int) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> LongArray.scanLaw(initial: R, operation: (acc: R, Long) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> ShortArray.scanLaw(initial: R, operation: (acc: R, Short) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <T, R> Array<out T>.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> BooleanArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Boolean) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> ByteArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Byte) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> CharArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Char) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> DoubleArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Double) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> FloatArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Float) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> IntArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Int) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> LongArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Long) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> ShortArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, Short) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public fun <T> Array<T>.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun <T> Array<T>.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun BooleanArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun BooleanArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ByteArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ByteArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun CharArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun CharArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun DoubleArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun DoubleArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun FloatArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun FloatArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun IntArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun IntArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun LongArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun LongArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ShortArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ShortArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun <T> Array<out T>.singleLaw(): T {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun <T> Array<out T>.singleLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun BooleanArray.singleLaw(): Boolean {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun BooleanArray.singleLaw(predicate: (Boolean) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun ByteArray.singleLaw(): Byte {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun ByteArray.singleLaw(predicate: (Byte) -> Boolean): Byte {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun CharArray.singleLaw(): Char {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun CharArray.singleLaw(predicate: (Char) -> Boolean): Char {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun DoubleArray.singleLaw(): Double {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun DoubleArray.singleLaw(predicate: (Double) -> Boolean): Double {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun FloatArray.singleLaw(): Float {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun FloatArray.singleLaw(predicate: (Float) -> Boolean): Float {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun IntArray.singleLaw(): Int {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun IntArray.singleLaw(predicate: (Int) -> Boolean): Int {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun LongArray.singleLaw(): Long {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun LongArray.singleLaw(predicate: (Long) -> Boolean): Long {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun ShortArray.singleLaw(): Short {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun ShortArray.singleLaw(predicate: (Short) -> Boolean): Short {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun <T> Array<out T>.singleOrNullLaw(): T? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun <T> Array<out T>.singleOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun BooleanArray.singleOrNullLaw(): Boolean? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun BooleanArray.singleOrNullLaw(predicate: (Boolean) -> Boolean): Boolean? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun ByteArray.singleOrNullLaw(): Byte? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun ByteArray.singleOrNullLaw(predicate: (Byte) -> Boolean): Byte? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun CharArray.singleOrNullLaw(): Char? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun CharArray.singleOrNullLaw(predicate: (Char) -> Boolean): Char? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun DoubleArray.singleOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun DoubleArray.singleOrNullLaw(predicate: (Double) -> Boolean): Double? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun FloatArray.singleOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun FloatArray.singleOrNullLaw(predicate: (Float) -> Boolean): Float? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun IntArray.singleOrNullLaw(): Int? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun IntArray.singleOrNullLaw(predicate: (Int) -> Boolean): Int? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun LongArray.singleOrNullLaw(): Long? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun LongArray.singleOrNullLaw(predicate: (Long) -> Boolean): Long? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun ShortArray.singleOrNullLaw(): Short? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun ShortArray.singleOrNullLaw(predicate: (Short) -> Boolean): Short? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun <T> Array<out T>.sliceLaw(indices: Iterable<Int>): List<T> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun <T> Array<out T>.sliceLaw(indices: IntRange): List<T> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun BooleanArray.sliceLaw(indices: Iterable<Int>): List<Boolean> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun BooleanArray.sliceLaw(indices: IntRange): List<Boolean> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ByteArray.sliceLaw(indices: Iterable<Int>): List<Byte> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ByteArray.sliceLaw(indices: IntRange): List<Byte> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun CharArray.sliceLaw(indices: Iterable<Int>): List<Char> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun CharArray.sliceLaw(indices: IntRange): List<Char> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun DoubleArray.sliceLaw(indices: Iterable<Int>): List<Double> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun DoubleArray.sliceLaw(indices: IntRange): List<Double> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun FloatArray.sliceLaw(indices: Iterable<Int>): List<Float> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun FloatArray.sliceLaw(indices: IntRange): List<Float> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun IntArray.sliceLaw(indices: Iterable<Int>): List<Int> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun IntArray.sliceLaw(indices: IntRange): List<Int> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun LongArray.sliceLaw(indices: Iterable<Int>): List<Long> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun LongArray.sliceLaw(indices: IntRange): List<Long> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ShortArray.sliceLaw(indices: Iterable<Int>): List<Short> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ShortArray.sliceLaw(indices: IntRange): List<Short> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun <T> Array<T>.sliceArrayLaw(indices: Collection<Int>): Array<T> {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun <T> Array<T>.sliceArrayLaw(indices: IntRange): Array<T> {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun BooleanArray.sliceArrayLaw(indices: Collection<Int>): BooleanArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun BooleanArray.sliceArrayLaw(indices: IntRange): BooleanArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ByteArray.sliceArrayLaw(indices: Collection<Int>): ByteArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ByteArray.sliceArrayLaw(indices: IntRange): ByteArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun CharArray.sliceArrayLaw(indices: Collection<Int>): CharArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun CharArray.sliceArrayLaw(indices: IntRange): CharArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun DoubleArray.sliceArrayLaw(indices: Collection<Int>): DoubleArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun DoubleArray.sliceArrayLaw(indices: IntRange): DoubleArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun FloatArray.sliceArrayLaw(indices: Collection<Int>): FloatArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun FloatArray.sliceArrayLaw(indices: IntRange): FloatArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun IntArray.sliceArrayLaw(indices: Collection<Int>): IntArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun IntArray.sliceArrayLaw(indices: IntRange): IntArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun LongArray.sliceArrayLaw(indices: Collection<Int>): LongArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun LongArray.sliceArrayLaw(indices: IntRange): LongArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ShortArray.sliceArrayLaw(indices: Collection<Int>): ShortArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ShortArray.sliceArrayLaw(indices: IntRange): ShortArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.sortByLaw(crossinline selector: (T) -> R?): Unit {
  pre(true) { "kotlin.collections.sortBy pre-conditions" }
  return sortBy(selector)
    .post({ true }, { "kotlin.collections.sortBy post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.sortByDescendingLaw(crossinline selector: (T) -> R?): Unit {
  pre(true) { "kotlin.collections.sortByDescending pre-conditions" }
  return sortByDescending(selector)
    .post({ true }, { "kotlin.collections.sortByDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ByteArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ByteArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun CharArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun CharArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun DoubleArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun DoubleArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun FloatArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun FloatArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun IntArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun IntArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun LongArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun LongArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ShortArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ShortArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.sortedLaw(): List<T> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun ByteArray.sortedLaw(): List<Byte> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun CharArray.sortedLaw(): List<Char> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun DoubleArray.sortedLaw(): List<Double> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun FloatArray.sortedLaw(): List<Float> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun IntArray.sortedLaw(): List<Int> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun LongArray.sortedLaw(): List<Long> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun ShortArray.sortedLaw(): List<Short> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<T>.sortedArrayLaw(): Array<T> {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun ByteArray.sortedArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun CharArray.sortedArrayLaw(): CharArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun DoubleArray.sortedArrayLaw(): DoubleArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun FloatArray.sortedArrayLaw(): FloatArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun IntArray.sortedArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun LongArray.sortedArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun ShortArray.sortedArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<T>.sortedArrayDescendingLaw(): Array<T> {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun ByteArray.sortedArrayDescendingLaw(): ByteArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun CharArray.sortedArrayDescendingLaw(): CharArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun DoubleArray.sortedArrayDescendingLaw(): DoubleArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun FloatArray.sortedArrayDescendingLaw(): FloatArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun IntArray.sortedArrayDescendingLaw(): IntArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun LongArray.sortedArrayDescendingLaw(): LongArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun ShortArray.sortedArrayDescendingLaw(): ShortArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun <T> Array<out T>.sortedArrayWithLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): Array<out T> {
  pre(true) { "kotlin.collections.sortedArrayWith pre-conditions" }
  return sortedArrayWith(comparator)
    .post({ true }, { "kotlin.collections.sortedArrayWith post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.sortedByLaw(crossinline selector: (T) -> R?): List<T> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.sortedByLaw(crossinline selector: (Boolean) -> R?): List<Boolean> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.sortedByLaw(crossinline selector: (Byte) -> R?): List<Byte> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.sortedByLaw(crossinline selector: (Char) -> R?): List<Char> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.sortedByLaw(crossinline selector: (Double) -> R?): List<Double> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.sortedByLaw(crossinline selector: (Float) -> R?): List<Float> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.sortedByLaw(crossinline selector: (Int) -> R?): List<Int> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.sortedByLaw(crossinline selector: (Long) -> R?): List<Long> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.sortedByLaw(crossinline selector: (Short) -> R?): List<Short> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Array<out T>.sortedByDescendingLaw(crossinline selector: (T) -> R?): List<T> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> BooleanArray.sortedByDescendingLaw(crossinline selector: (Boolean) -> R?): List<Boolean> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ByteArray.sortedByDescendingLaw(crossinline selector: (Byte) -> R?): List<Byte> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> CharArray.sortedByDescendingLaw(crossinline selector: (Char) -> R?): List<Char> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> DoubleArray.sortedByDescendingLaw(crossinline selector: (Double) -> R?): List<Double> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> FloatArray.sortedByDescendingLaw(crossinline selector: (Float) -> R?): List<Float> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> IntArray.sortedByDescendingLaw(crossinline selector: (Int) -> R?): List<Int> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> LongArray.sortedByDescendingLaw(crossinline selector: (Long) -> R?): List<Long> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ShortArray.sortedByDescendingLaw(crossinline selector: (Short) -> R?): List<Short> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Array<out T>.sortedDescendingLaw(): List<T> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun ByteArray.sortedDescendingLaw(): List<Byte> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun CharArray.sortedDescendingLaw(): List<Char> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun DoubleArray.sortedDescendingLaw(): List<Double> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun FloatArray.sortedDescendingLaw(): List<Float> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun IntArray.sortedDescendingLaw(): List<Int> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun LongArray.sortedDescendingLaw(): List<Long> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun ShortArray.sortedDescendingLaw(): List<Short> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun <T> Array<out T>.sortedWithLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): List<T> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun BooleanArray.sortedWithLaw(comparator: Comparator<in Boolean> /* = java.util.Comparator<in Boolean> */): List<Boolean> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun ByteArray.sortedWithLaw(comparator: Comparator<in Byte> /* = java.util.Comparator<in Byte> */): List<Byte> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun CharArray.sortedWithLaw(comparator: Comparator<in Char> /* = java.util.Comparator<in Char> */): List<Char> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun DoubleArray.sortedWithLaw(comparator: Comparator<in Double> /* = java.util.Comparator<in Double> */): List<Double> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun FloatArray.sortedWithLaw(comparator: Comparator<in Float> /* = java.util.Comparator<in Float> */): List<Float> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun IntArray.sortedWithLaw(comparator: Comparator<in Int> /* = java.util.Comparator<in Int> */): List<Int> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun LongArray.sortedWithLaw(comparator: Comparator<in Long> /* = java.util.Comparator<in Long> */): List<Long> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public fun ShortArray.sortedWithLaw(comparator: Comparator<in Short> /* = java.util.Comparator<in Short> */): List<Short> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public infix fun <T> Array<out T>.subtractLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun BooleanArray.subtractLaw(other: Iterable<Boolean>): Set<Boolean> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun ByteArray.subtractLaw(other: Iterable<Byte>): Set<Byte> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun CharArray.subtractLaw(other: Iterable<Char>): Set<Char> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun DoubleArray.subtractLaw(other: Iterable<Double>): Set<Double> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun FloatArray.subtractLaw(other: Iterable<Float>): Set<Float> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun IntArray.subtractLaw(other: Iterable<Int>): Set<Int> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun LongArray.subtractLaw(other: Iterable<Long>): Set<Long> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public infix fun ShortArray.subtractLaw(other: Iterable<Short>): Set<Short> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
public fun Array<out Byte>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out Double>.sumLaw(): Double {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out Float>.sumLaw(): Float {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out Int>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out Long>.sumLaw(): Long {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out Short>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun ByteArray.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun DoubleArray.sumLaw(): Double {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun FloatArray.sumLaw(): Float {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun IntArray.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun LongArray.sumLaw(): Long {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun ShortArray.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawInt")
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLong")
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun <T> Array<out T>.sumOfLaw(selector: (T) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawBooleanArrayInt")
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawBooleanArrayLong")
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawBooleanArrayUInt")
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawBooleanArrayULong")
public inline fun BooleanArray.sumOfLaw(selector: (Boolean) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawBooleanArrayDouble")
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawByteArrayInt")
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawByteArrayLong")
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawByteArrayUInt")
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawByteArrayULong")
public inline fun ByteArray.sumOfLaw(selector: (Byte) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawCharArrayDouble")
public inline fun CharArray.sumOfLaw(selector: (Char) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawCharArrayInt")
public inline fun CharArray.sumOfLaw(selector: (Char) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawCharArrayLong")
public inline fun CharArray.sumOfLaw(selector: (Char) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawCharArrayUInt")
public inline fun CharArray.sumOfLaw(selector: (Char) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawCharArrayULong")
public inline fun CharArray.sumOfLaw(selector: (Char) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawDoubleArrayDouble")
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawDoubleArrayInt")
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawDoubleArrayLong")
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawDoubleArrayUInt")
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawDoubleArrayULong")
public inline fun DoubleArray.sumOfLaw(selector: (Double) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawFloatArrayDouble")
public inline fun FloatArray.sumOfLaw(selector: (Float) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawFloatArrayInt")
public inline fun FloatArray.sumOfLaw(selector: (Float) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawFloatArrayLong")
public inline fun FloatArray.sumOfLaw(selector: (Float) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawFloatArrayUInt")
public inline fun FloatArray.sumOfLaw(selector: (Float) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawFloatArrayULong")
public inline fun FloatArray.sumOfLaw(selector: (Float) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIntArrayDouble")
public inline fun IntArray.sumOfLaw(selector: (Int) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIntArrayInt")
public inline fun IntArray.sumOfLaw(selector: (Int) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIntArrayLong")
public inline fun IntArray.sumOfLaw(selector: (Int) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIntArrayUInt")
public inline fun IntArray.sumOfLaw(selector: (Int) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIntArrayULong")
public inline fun IntArray.sumOfLaw(selector: (Int) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLongArrayDouble")
public inline fun LongArray.sumOfLaw(selector: (Long) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLongArrayInt")
public inline fun LongArray.sumOfLaw(selector: (Long) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLongArrayLong")
public inline fun LongArray.sumOfLaw(selector: (Long) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLongArrayUInt")
public inline fun LongArray.sumOfLaw(selector: (Long) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawLongArrayDouble")
public inline fun LongArray.sumOfLaw(selector: (Long) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawShortArrayDouble")
public inline fun ShortArray.sumOfLaw(selector: (Short) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawShortArrayInt")
public inline fun ShortArray.sumOfLaw(selector: (Short) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawShortArrayLong")
public inline fun ShortArray.sumOfLaw(selector: (Short) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawShortArrayUInt")
public inline fun ShortArray.sumOfLaw(selector: (Short) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawShortArrayULong")
public inline fun ShortArray.sumOfLaw(selector: (Short) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public fun <T> Array<out T>.takeLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun BooleanArray.takeLaw(n: Int): List<Boolean> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun ByteArray.takeLaw(n: Int): List<Byte> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun CharArray.takeLaw(n: Int): List<Char> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun DoubleArray.takeLaw(n: Int): List<Double> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun FloatArray.takeLaw(n: Int): List<Float> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun IntArray.takeLaw(n: Int): List<Int> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun LongArray.takeLaw(n: Int): List<Long> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun ShortArray.takeLaw(n: Int): List<Short> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun <T> Array<out T>.takeLastLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun BooleanArray.takeLastLaw(n: Int): List<Boolean> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun ByteArray.takeLastLaw(n: Int): List<Byte> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun CharArray.takeLastLaw(n: Int): List<Char> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun DoubleArray.takeLastLaw(n: Int): List<Double> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun FloatArray.takeLastLaw(n: Int): List<Float> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun IntArray.takeLastLaw(n: Int): List<Int> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun LongArray.takeLastLaw(n: Int): List<Long> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun ShortArray.takeLastLaw(n: Int): List<Short> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public inline fun <T> Array<out T>.takeLastWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun BooleanArray.takeLastWhileLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun ByteArray.takeLastWhileLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun CharArray.takeLastWhileLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun DoubleArray.takeLastWhileLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun FloatArray.takeLastWhileLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun IntArray.takeLastWhileLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun LongArray.takeLastWhileLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun ShortArray.takeLastWhileLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun <T> Array<out T>.takeWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun BooleanArray.takeWhileLaw(predicate: (Boolean) -> Boolean): List<Boolean> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun ByteArray.takeWhileLaw(predicate: (Byte) -> Boolean): List<Byte> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun CharArray.takeWhileLaw(predicate: (Char) -> Boolean): List<Char> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun DoubleArray.takeWhileLaw(predicate: (Double) -> Boolean): List<Double> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun FloatArray.takeWhileLaw(predicate: (Float) -> Boolean): List<Float> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun IntArray.takeWhileLaw(predicate: (Int) -> Boolean): List<Int> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun LongArray.takeWhileLaw(predicate: (Long) -> Boolean): List<Long> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun ShortArray.takeWhileLaw(predicate: (Short) -> Boolean): List<Short> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public fun Array<out Boolean>.toBooleanArrayLaw(): BooleanArray {
  pre(true) { "kotlin.collections.toBooleanArray pre-conditions" }
  return toBooleanArray()
    .post({ true }, { "kotlin.collections.toBooleanArray post-conditions" })
}

@Law
public fun Array<out Byte>.toByteArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.toByteArray pre-conditions" }
  return toByteArray()
    .post({ true }, { "kotlin.collections.toByteArray post-conditions" })
}

@Law
public fun Array<out Char>.toCharArrayLaw(): CharArray {
  pre(true) { "kotlin.collections.toCharArray pre-conditions" }
  return toCharArray()
    .post({ true }, { "kotlin.collections.toCharArray post-conditions" })
}

@Law
public fun <T, C : MutableCollection<in T>> Array<out T>.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Boolean>> BooleanArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Byte>> ByteArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Char>> CharArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Double>> DoubleArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Float>> FloatArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Int>> IntArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Long>> LongArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun <C : MutableCollection<in Short>> ShortArray.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun Array<out Double>.toDoubleArrayLaw(): DoubleArray {
  pre(true) { "kotlin.collections.toDoubleArray pre-conditions" }
  return toDoubleArray()
    .post({ true }, { "kotlin.collections.toDoubleArray post-conditions" })
}

@Law
public fun Array<out Float>.toFloatArrayLaw(): FloatArray {
  pre(true) { "kotlin.collections.toFloatArray pre-conditions" }
  return toFloatArray()
    .post({ true }, { "kotlin.collections.toFloatArray post-conditions" })
}

@Law
public fun <T> Array<out T>.toHashSetLaw(): HashSet<T> /* = java.util.HashSet<T> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun BooleanArray.toHashSetLaw(): HashSet<Boolean> /* = java.util.HashSet<Boolean> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun ByteArray.toHashSetLaw(): HashSet<Byte> /* = java.util.HashSet<Byte> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun CharArray.toHashSetLaw(): HashSet<Char> /* = java.util.HashSet<Char> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun DoubleArray.toHashSetLaw(): HashSet<Double> /* = java.util.HashSet<Double> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun FloatArray.toHashSetLaw(): HashSet<Float> /* = java.util.HashSet<Float> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun IntArray.toHashSetLaw(): HashSet<Int> /* = java.util.HashSet<Int> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun LongArray.toHashSetLaw(): HashSet<Long> /* = java.util.HashSet<Long> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun ShortArray.toHashSetLaw(): HashSet<Short> /* = java.util.HashSet<Short> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun Array<out Int>.toIntArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.toIntArray pre-conditions" }
  return toIntArray()
    .post({ true }, { "kotlin.collections.toIntArray post-conditions" })
}

@Law
public fun <T> Array<out T>.toListLaw(): List<T> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun BooleanArray.toListLaw(): List<Boolean> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun ByteArray.toListLaw(): List<Byte> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun CharArray.toListLaw(): List<Char> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun DoubleArray.toListLaw(): List<Double> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun FloatArray.toListLaw(): List<Float> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun IntArray.toListLaw(): List<Int> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun LongArray.toListLaw(): List<Long> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun ShortArray.toListLaw(): List<Short> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun Array<out Long>.toLongArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.toLongArray pre-conditions" }
  return toLongArray()
    .post({ true }, { "kotlin.collections.toLongArray post-conditions" })
}

@Law
public fun <T> Array<out T>.toMutableListLaw(): MutableList<T> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun BooleanArray.toMutableListLaw(): MutableList<Boolean> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun ByteArray.toMutableListLaw(): MutableList<Byte> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun CharArray.toMutableListLaw(): MutableList<Char> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun DoubleArray.toMutableListLaw(): MutableList<Double> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun FloatArray.toMutableListLaw(): MutableList<Float> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun IntArray.toMutableListLaw(): MutableList<Int> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun LongArray.toMutableListLaw(): MutableList<Long> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun ShortArray.toMutableListLaw(): MutableList<Short> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun <T> Array<out T>.toMutableSetLaw(): MutableSet<T> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun BooleanArray.toMutableSetLaw(): MutableSet<Boolean> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun ByteArray.toMutableSetLaw(): MutableSet<Byte> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun CharArray.toMutableSetLaw(): MutableSet<Char> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun DoubleArray.toMutableSetLaw(): MutableSet<Double> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun FloatArray.toMutableSetLaw(): MutableSet<Float> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun IntArray.toMutableSetLaw(): MutableSet<Int> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun LongArray.toMutableSetLaw(): MutableSet<Long> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun ShortArray.toMutableSetLaw(): MutableSet<Short> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun <T> Array<out T>.toSetLaw(): Set<T> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun BooleanArray.toSetLaw(): Set<Boolean> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun ByteArray.toSetLaw(): Set<Byte> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun CharArray.toSetLaw(): Set<Char> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun DoubleArray.toSetLaw(): Set<Double> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun FloatArray.toSetLaw(): Set<Float> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun IntArray.toSetLaw(): Set<Int> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun LongArray.toSetLaw(): Set<Long> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun ShortArray.toSetLaw(): Set<Short> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun Array<out Short>.toShortArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.toShortArray pre-conditions" }
  return toShortArray()
    .post({ true }, { "kotlin.collections.toShortArray post-conditions" })
}

@Law
public infix fun <T> Array<out T>.unionLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun BooleanArray.unionLaw(other: Iterable<Boolean>): Set<Boolean> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun ByteArray.unionLaw(other: Iterable<Byte>): Set<Byte> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun CharArray.unionLaw(other: Iterable<Char>): Set<Char> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun DoubleArray.unionLaw(other: Iterable<Double>): Set<Double> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun FloatArray.unionLaw(other: Iterable<Float>): Set<Float> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun IntArray.unionLaw(other: Iterable<Int>): Set<Int> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun LongArray.unionLaw(other: Iterable<Long>): Set<Long> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public infix fun ShortArray.unionLaw(other: Iterable<Short>): Set<Short> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public fun <T> Array<out T>.withIndexLaw(): Iterable<IndexedValue<T>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun BooleanArray.withIndexLaw(): Iterable<IndexedValue<Boolean>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun ByteArray.withIndexLaw(): Iterable<IndexedValue<Byte>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun CharArray.withIndexLaw(): Iterable<IndexedValue<Char>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun DoubleArray.withIndexLaw(): Iterable<IndexedValue<Double>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun FloatArray.withIndexLaw(): Iterable<IndexedValue<Float>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun IntArray.withIndexLaw(): Iterable<IndexedValue<Int>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun LongArray.withIndexLaw(): Iterable<IndexedValue<Long>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun ShortArray.withIndexLaw(): Iterable<IndexedValue<Short>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public infix fun <T, R> Array<out T>.zipLaw(other: Array<out R>): List<Pair<T, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <T, R, V> Array<out T>.zipLaw(other: Array<out R>, transform: (a: T, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <T, R> Array<out T>.zipLaw(other: Iterable<R>): List<Pair<T, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <T, R, V> Array<out T>.zipLaw(other: Iterable<R>, transform: (a: T, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> BooleanArray.zipLaw(other: Array<out R>): List<Pair<Boolean, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> BooleanArray.zipLaw(other: Array<out R>, transform: (a: Boolean, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun BooleanArray.zipLaw(other: BooleanArray): List<Pair<Boolean, Boolean>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> BooleanArray.zipLaw(other: BooleanArray, transform: (a: Boolean, b: Boolean) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> BooleanArray.zipLaw(other: Iterable<R>): List<Pair<Boolean, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> BooleanArray.zipLaw(other: Iterable<R>, transform: (a: Boolean, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ByteArray.zipLaw(other: Array<out R>): List<Pair<Byte, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ByteArray.zipLaw(other: Array<out R>, transform: (a: Byte, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun ByteArray.zipLaw(other: ByteArray): List<Pair<Byte, Byte>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> ByteArray.zipLaw(other: ByteArray, transform: (a: Byte, b: Byte) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ByteArray.zipLaw(other: Iterable<R>): List<Pair<Byte, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ByteArray.zipLaw(other: Iterable<R>, transform: (a: Byte, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> CharArray.zipLaw(other: Array<out R>): List<Pair<Char, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> CharArray.zipLaw(other: Array<out R>, transform: (a: Char, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun CharArray.zipLaw(other: CharArray): List<Pair<Char, Char>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> CharArray.zipLaw(other: CharArray, transform: (a: Char, b: Char) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> CharArray.zipLaw(other: Iterable<R>): List<Pair<Char, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> CharArray.zipLaw(other: Iterable<R>, transform: (a: Char, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> DoubleArray.zipLaw(other: Array<out R>): List<Pair<Double, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> DoubleArray.zipLaw(other: Array<out R>, transform: (a: Double, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun DoubleArray.zipLaw(other: DoubleArray): List<Pair<Double, Double>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> DoubleArray.zipLaw(other: DoubleArray, transform: (a: Double, b: Double) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> DoubleArray.zipLaw(other: Iterable<R>): List<Pair<Double, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> DoubleArray.zipLaw(other: Iterable<R>, transform: (a: Double, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> FloatArray.zipLaw(other: Array<out R>): List<Pair<Float, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> FloatArray.zipLaw(other: Array<out R>, transform: (a: Float, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun FloatArray.zipLaw(other: FloatArray): List<Pair<Float, Float>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> FloatArray.zipLaw(other: FloatArray, transform: (a: Float, b: Float) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> FloatArray.zipLaw(other: Iterable<R>): List<Pair<Float, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> FloatArray.zipLaw(other: Iterable<R>, transform: (a: Float, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> IntArray.zipLaw(other: Array<out R>): List<Pair<Int, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> IntArray.zipLaw(other: Array<out R>, transform: (a: Int, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun IntArray.zipLaw(other: IntArray): List<Pair<Int, Int>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> IntArray.zipLaw(other: IntArray, transform: (a: Int, b: Int) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> IntArray.zipLaw(other: Iterable<R>): List<Pair<Int, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> IntArray.zipLaw(other: Iterable<R>, transform: (a: Int, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> LongArray.zipLaw(other: Array<out R>): List<Pair<Long, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> LongArray.zipLaw(other: Array<out R>, transform: (a: Long, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun LongArray.zipLaw(other: LongArray): List<Pair<Long, Long>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> LongArray.zipLaw(other: LongArray, transform: (a: Long, b: Long) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> LongArray.zipLaw(other: Iterable<R>): List<Pair<Long, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> LongArray.zipLaw(other: Iterable<R>, transform: (a: Long, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ShortArray.zipLaw(other: Array<out R>): List<Pair<Short, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ShortArray.zipLaw(other: Array<out R>, transform: (a: Short, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun ShortArray.zipLaw(other: ShortArray): List<Pair<Short, Short>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> ShortArray.zipLaw(other: ShortArray, transform: (a: Short, b: Short) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ShortArray.zipLaw(other: Iterable<R>): List<Pair<Short, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ShortArray.zipLaw(other: Iterable<R>, transform: (a: Short, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public fun <T> listOfLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.listOf pre-conditions" }
  return listOf(element)
    .post({ true }, { "kotlin.collections.listOf post-conditions" })
}

@Law
public fun <T> Iterable<T>.shuffledLaw(): List<T> {
  pre(true) { "kotlin.collections.shuffled pre-conditions" }
  return shuffled()
    .post({ true }, { "kotlin.collections.shuffled post-conditions" })
}

@Law
public inline fun <T> ListLaw(size: Int, init: (index: Int) -> T): List<T> {
  pre(true) { "kotlin.collections.List pre-conditions" }
  return List(size, init)
    .post({ true }, { "kotlin.collections.List post-conditions" })
}

@Law
public inline fun <T> MutableListLaw(size: Int, init: (index: Int) -> T): MutableList<T> {
  pre(true) { "kotlin.collections.MutableList pre-conditions" }
  return MutableList(size, init)
    .post({ true }, { "kotlin.collections.MutableList post-conditions" })
}

@Law
public inline fun <T> arrayListOfLaw(): ArrayList<T> /* = java.util.ArrayList<T> */ {
  pre(true) { "kotlin.collections.arrayListOf pre-conditions" }
  return arrayListOf<T>()
    .post({ true }, { "kotlin.collections.arrayListOf post-conditions" })
}

@Law
public fun <T> arrayListOfLaw(vararg elements: T): ArrayList<T> /* = java.util.ArrayList<T> */ {
  pre(true) { "kotlin.collections.arrayListOf pre-conditions" }
  return arrayListOf(*elements)
    .post({ true }, { "kotlin.collections.arrayListOf post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <E> buildListLaw(capacity: Int, builderAction: MutableList<E>.() -> Unit): List<E> {
  pre(true) { "kotlin.collections.buildList pre-conditions" }
  return buildList(capacity, builderAction)
    .post({ true }, { "kotlin.collections.buildList post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <E> buildListLaw(builderAction: MutableList<E>.() -> Unit): List<E> {
  pre(true) { "kotlin.collections.buildList pre-conditions" }
  return buildList(builderAction)
    .post({ true }, { "kotlin.collections.buildList post-conditions" })
}

@Law
public fun <T> emptyListLaw(): List<T> {
  pre(true) { "kotlin.collections.emptyList pre-conditions" }
  return emptyList<T>()
    .post({ true }, { "kotlin.collections.emptyList post-conditions" })
}

@Law
public inline fun <T> listOfLaw(): List<T> {
  pre(true) { "kotlin.collections.listOf pre-conditions" }
  return listOf<T>()
    .post({ true }, { "kotlin.collections.listOf post-conditions" })
}

@Law
public fun <T> listOfLaw(vararg elements: T): List<T> {
  pre(true) { "kotlin.collections.listOf pre-conditions" }
  return listOf(*elements)
    .post({ true }, { "kotlin.collections.listOf post-conditions" })
}

@Law
public fun <T : Any> listOfNotNullLaw(element: T?): List<T> {
  pre(true) { "kotlin.collections.listOfNotNull pre-conditions" }
  return listOfNotNull(element)
    .post({ true }, { "kotlin.collections.listOfNotNull post-conditions" })
}

@Law
public fun <T : Any> listOfNotNullLaw(vararg elements: T?): List<T> {
  pre(true) { "kotlin.collections.listOfNotNull pre-conditions" }
  return listOfNotNull(*elements)
    .post({ true }, { "kotlin.collections.listOfNotNull post-conditions" })
}

@Law
public inline fun <T> mutableListOfLaw(): MutableList<T> {
  pre(true) { "kotlin.collections.mutableListOf pre-conditions" }
  return mutableListOf<T>()
    .post({ true }, { "kotlin.collections.mutableListOf post-conditions" })
}

@Law
public fun <T> mutableListOfLaw(vararg elements: T): MutableList<T> {
  pre(true) { "kotlin.collections.mutableListOf pre-conditions" }
  return mutableListOf(*elements)
    .post({ true }, { "kotlin.collections.mutableListOf post-conditions" })
}

@Law
public fun <T> List<T>.binarySearchLaw(
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
public fun <T> List<T>.binarySearchLaw(fromIndex: Int, toIndex: Int, comparison: (T) -> Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(fromIndex, toIndex, comparison)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public fun <T : Comparable<T>> List<T?>.binarySearchNullableLaw(element: T?, fromIndex: Int, toIndex: Int): Int {
  pre(true) { "kotlin.collections.binarySearch pre-conditions" }
  return binarySearch(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.binarySearch post-conditions" })
}

@Law
public inline fun <T, K : Comparable<K>> List<T>.binarySearchByLaw(
  key: K?,
  fromIndex: Int,
  toIndex: Int,
  crossinline selector: (T) -> K?
): Int {
  pre(true) { "kotlin.collections.binarySearchBy pre-conditions" }
  return binarySearchBy(key, fromIndex, toIndex, selector)
    .post({ true }, { "kotlin.collections.binarySearchBy post-conditions" })
}

@Law
public inline fun <T> Collection<T>.containsAllLaw(elements: Collection<T>): Boolean {
  pre(true) { "kotlin.collections.containsAll pre-conditions" }
  return containsAll(elements)
    .post({ true }, { "kotlin.collections.containsAll post-conditions" })
}

// @Law
// public inline fun <C : Collection<*>, R> C.ifEmptyLaw(defaultValue: () -> R): R where C : R  {
//  pre(true) { "kotlin.collections.ifEmpty pre-conditions" }
//  return ifEmpty(defaultValue)
//    .post({ true }, { "kotlin.collections.ifEmpty post-conditions" })
// }

@Law
public inline fun <T> Collection<T>.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun <T> Collection<T>?.isNullOrEmptyNullableLaw(): Boolean {
  pre(true) { "kotlin.collections.isNullOrEmpty pre-conditions" }
  return isNullOrEmpty()
    .post({ true }, { "kotlin.collections.isNullOrEmpty post-conditions" })
}

@Law
public inline fun <T> Collection<T>?.orEmptyNullableLaw(): Collection<T> {
  pre(true) { "kotlin.collections.orEmpty pre-conditions" }
  return orEmpty()
    .post({ true }, { "kotlin.collections.orEmpty post-conditions" })
}

@Law
public inline fun <T> List<T>?.orEmptyNullableLaw(): List<T> {
  pre(true) { "kotlin.collections.orEmpty pre-conditions" }
  return orEmpty()
    .post({ true }, { "kotlin.collections.orEmpty post-conditions" })
}

@Law
public fun <T> Iterable<T>.shuffledLaw(random: Random): List<T> {
  pre(true) { "kotlin.collections.shuffled pre-conditions" }
  return shuffled(random)
    .post({ true }, { "kotlin.collections.shuffled post-conditions" })
}

@Law
public inline fun <T> IterableLaw(crossinline iterator: () -> Iterator<T>): Iterable<T> {
  pre(true) { "kotlin.collections.Iterable pre-conditions" }
  return Iterable(iterator)
    .post({ true }, { "kotlin.collections.Iterable post-conditions" })
}

@Law
public fun <T> Iterable<Iterable<T>>.flattenLaw(): List<T> {
  pre(true) { "kotlin.collections.flatten pre-conditions" }
  return flatten()
    .post({ true }, { "kotlin.collections.flatten post-conditions" })
}

@Law
public fun <T, R> Iterable<Pair<T, R>>.unzipLaw(): Pair<List<T>, List<R>> {
  pre(true) { "kotlin.collections.unzip pre-conditions" }
  return unzip()
    .post({ true }, { "kotlin.collections.unzip post-conditions" })
}

@Law
public inline fun <T> Iterator<T>.forEachLaw(operation: (T) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(operation)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun <T> Iterator<T>.iteratorLaw(): Iterator<T> {
  pre(true) { "kotlin.collections.iterator pre-conditions" }
  return iterator()
    .post({ true }, { "kotlin.collections.iterator post-conditions" })
}

@Law
public fun <T> Iterator<T>.withIndexLaw(): Iterator<IndexedValue<T>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public inline fun <T> MutableList<T>.fillLaw(value: T): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(value)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public inline fun <T> MutableList<T>.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun <T : Comparable<T>> MutableList<T>.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun <T> MutableList<T>.sortWithLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): Unit {
  pre(true) { "kotlin.collections.sortWith pre-conditions" }
  return sortWith(comparator)
    .post({ true }, { "kotlin.collections.sortWith post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.addAllLaw(elements: Array<out T>): Boolean {
  pre(true) { "kotlin.collections.addAll pre-conditions" }
  return addAll(elements)
    .post({ true }, { "kotlin.collections.addAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.addAllLaw(elements: Iterable<T>): Boolean {
  pre(true) { "kotlin.collections.addAll pre-conditions" }
  return addAll(elements)
    .post({ true }, { "kotlin.collections.addAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.addAllLaw(elements: Sequence<T>): Boolean {
  pre(true) { "kotlin.collections.addAll pre-conditions" }
  return addAll(elements)
    .post({ true }, { "kotlin.collections.addAll post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.minusAssignLaw(element: T): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(element)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.minusAssignLaw(elements: Array<T>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(elements)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.minusAssignLaw(elements: Iterable<T>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(elements)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.minusAssignLaw(elements: Sequence<T>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(elements)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.plusAssignLaw(element: T): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(element)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.plusAssignLaw(elements: Array<T>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(elements)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.plusAssignLaw(elements: Iterable<T>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(elements)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<in T>.plusAssignLaw(elements: Sequence<T>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(elements)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <T> MutableCollection<out T>.removeLaw(element: T): Boolean {
  pre(true) { "kotlin.collections.remove pre-conditions" }
  return remove(element)
    .post({ true }, { "kotlin.collections.remove post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.removeAllLaw(elements: Array<out T>): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(elements)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.removeAllLaw(elements: Iterable<T>): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(elements)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.removeAllLaw(elements: Sequence<T>): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(elements)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public inline fun <T> MutableCollection<out T>.removeAllLaw(elements: Collection<T>): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(elements)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public fun <T> MutableIterable<T>.removeAllLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(predicate)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public fun <T> MutableList<T>.removeAllLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.removeAll pre-conditions" }
  return removeAll(predicate)
    .post({ true }, { "kotlin.collections.removeAll post-conditions" })
}

@Law
public fun <T> MutableList<T>.removeFirstLaw(): T {
  pre(true) { "kotlin.collections.removeFirst pre-conditions" }
  return removeFirst()
    .post({ true }, { "kotlin.collections.removeFirst post-conditions" })
}

@Law
public fun <T> MutableList<T>.removeFirstOrNullLaw(): T? {
  pre(true) { "kotlin.collections.removeFirstOrNull pre-conditions" }
  return removeFirstOrNull()
    .post({ true }, { "kotlin.collections.removeFirstOrNull post-conditions" })
}

@Law
public fun <T> MutableList<T>.removeLastLaw(): T {
  pre(true) { "kotlin.collections.removeLast pre-conditions" }
  return removeLast()
    .post({ true }, { "kotlin.collections.removeLast post-conditions" })
}

@Law
public fun <T> MutableList<T>.removeLastOrNullLaw(): T? {
  pre(true) { "kotlin.collections.removeLastOrNull pre-conditions" }
  return removeLastOrNull()
    .post({ true }, { "kotlin.collections.removeLastOrNull post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.retainAllLaw(elements: Array<out T>): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(elements)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.retainAllLaw(elements: Iterable<T>): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(elements)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
public fun <T> MutableCollection<in T>.retainAllLaw(elements: Sequence<T>): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(elements)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
public inline fun <T> MutableCollection<out T>.retainAllLaw(elements: Collection<T>): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(elements)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
public fun <T> MutableIterable<T>.retainAllLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(predicate)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
public fun <T> MutableList<T>.retainAllLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.retainAll pre-conditions" }
  return retainAll(predicate)
    .post({ true }, { "kotlin.collections.retainAll post-conditions" })
}

@Law
@JvmName("asReversedLawList")
public fun <T> List<T>.asReversedLaw(): List<T> {
  pre(true) { "kotlin.collections.asReversed pre-conditions" }
  return asReversed()
    .post({ true }, { "kotlin.collections.asReversed post-conditions" })
}

@Law
@JvmName("asReversedLawMutableList")
public fun <T> MutableList<T>.asReversedLaw(): MutableList<T> {
  pre(true) { "kotlin.collections.asReversed pre-conditions" }
  return asReversed()
    .post({ true }, { "kotlin.collections.asReversed post-conditions" })
}

@Law
public fun <T> MutableList<T>.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.allLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public fun <T> Iterable<T>.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.anyLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.asIterableLaw(): Iterable<T> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun <T> Iterable<T>.asSequenceLaw(): Sequence<T> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public inline fun <T, K, V> Iterable<T>.associateLaw(transform: (T) -> Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.associate pre-conditions" }
  return associate(transform)
    .post({ true }, { "kotlin.collections.associate post-conditions" })
}

@Law
public inline fun <T, K> Iterable<T>.associateByLaw(keySelector: (T) -> K): Map<K, T> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <T, K, V> Iterable<T>.associateByLaw(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateBy pre-conditions" }
  return associateBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateBy post-conditions" })
}

@Law
public inline fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.associateByToLaw(
  destination: M,
  keySelector: (T) -> K
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateByToLaw(
  destination: M,
  keySelector: (T) -> K,
  valueTransform: (T) -> V
): M {
  pre(true) { "kotlin.collections.associateByTo pre-conditions" }
  return associateByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.associateByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateToLaw(
  destination: M,
  transform: (T) -> Pair<K, V>
): M {
  pre(true) { "kotlin.collections.associateTo pre-conditions" }
  return associateTo(destination, transform)
    .post({ true }, { "kotlin.collections.associateTo post-conditions" })
}

@Law
public inline fun <K, V> Iterable<K>.associateWithLaw(valueSelector: (K) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.associateWithToLaw(
  destination: M,
  valueSelector: (K) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
@JvmName("averageLawIterableByteDouble")
public fun Iterable<Byte>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
@JvmName("averageLawIterableDoubleDouble")
public fun Iterable<Double>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
@JvmName("averageLawIterableFloatDouble")
public fun Iterable<Float>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
@JvmName("averageLawIterableIntDouble")
public fun Iterable<Int>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
@JvmName("averageLawIterableLongDouble")
public fun Iterable<Long>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
@JvmName("averageLawIterableShortDouble")
public fun Iterable<Short>.averageLaw(): Double {
  pre(true) { "kotlin.collections.average pre-conditions" }
  return average()
    .post({ true }, { "kotlin.collections.average post-conditions" })
}

@Law
public fun <T> Iterable<T>.chunkedLaw(size: Int): List<List<T>> {
  pre(true) { "kotlin.collections.chunked pre-conditions" }
  return chunked(size)
    .post({ true }, { "kotlin.collections.chunked post-conditions" })
}

@Law
public fun <T, R> Iterable<T>.chunkedLaw(size: Int, transform: (List<T>) -> R): List<R> {
  pre(true) { "kotlin.collections.chunked pre-conditions" }
  return chunked(size, transform)
    .post({ true }, { "kotlin.collections.chunked post-conditions" })
}

@Law
public inline fun <T> List<T>.component1Law(): T {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun <T> List<T>.component2Law(): T {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun <T> List<T>.component3Law(): T {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun <T> List<T>.component4Law(): T {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun <T> List<T>.component5Law(): T {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public fun <T> Iterable<T>.containsLaw(element: T): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(element)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public inline fun <T> Collection<T>.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public fun <T> Iterable<T>.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.countLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public fun <T> Iterable<T>.distinctLaw(): List<T> {
  pre(true) { "kotlin.collections.distinct pre-conditions" }
  return distinct()
    .post({ true }, { "kotlin.collections.distinct post-conditions" })
}

@Law
public inline fun <T, K> Iterable<T>.distinctByLaw(selector: (T) -> K): List<T> {
  pre(true) { "kotlin.collections.distinctBy pre-conditions" }
  return distinctBy(selector)
    .post({ true }, { "kotlin.collections.distinctBy post-conditions" })
}

@Law
public fun <T> Iterable<T>.dropLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun <T> List<T>.dropLastLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public inline fun <T> List<T>.dropLastWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.dropWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public fun <T> Iterable<T>.elementAtLaw(index: Int): T {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun <T> List<T>.elementAtLaw(index: Int): T {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public fun <T> Iterable<T>.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> T): T {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun <T> List<T>.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> T): T {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public fun <T> Iterable<T>.elementAtOrNullLaw(index: Int): T? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun <T> List<T>.elementAtOrNullLaw(index: Int): T? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.filterLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.filterIndexedLaw(predicate: (index: Int, T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <reified R> Iterable<*>.filterIsInstanceLaw(): List<R> {
  pre(true) { "kotlin.collections.filterIsInstance pre-conditions" }
  return filterIsInstance<R>()
    .post({ true }, { "kotlin.collections.filterIsInstance post-conditions" })
}

@Law
public inline fun <reified R, C : MutableCollection<in R>> Iterable<*>.filterIsInstanceToLaw(destination: C): C {
  pre(true) { "kotlin.collections.filterIsInstanceTo pre-conditions" }
  return filterIsInstanceTo(destination)
    .post({ true }, { "kotlin.collections.filterIsInstanceTo post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.filterNotLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public fun <T : Any> Iterable<T?>.filterNotNullNullableLaw(): List<T> {
  pre(true) { "kotlin.collections.filterNotNull pre-conditions" }
  return filterNotNull()
    .post({ true }, { "kotlin.collections.filterNotNull post-conditions" })
}

@Law
public fun <C : MutableCollection<in T>, T : Any> Iterable<T?>.filterNotNullToNullableLaw(destination: C): C {
  pre(true) { "kotlin.collections.filterNotNullTo pre-conditions" }
  return filterNotNullTo(destination)
    .post({ true }, { "kotlin.collections.filterNotNullTo post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterNotToLaw(
  destination: C,
  predicate: (T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterToLaw(
  destination: C,
  predicate: (T) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.findLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.findLastLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun <T> List<T>.findLastLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public fun <T> Iterable<T>.firstLaw(): T {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.firstLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun <T> List<T>.firstLaw(): T {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun <T, R : Any> Iterable<T>.firstNotNullOfLaw(transform: (T) -> R?): R {
  pre(true) { "kotlin.collections.firstNotNullOf pre-conditions" }
  return firstNotNullOf(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOf post-conditions" })
}

@Law
public inline fun <T, R : Any> Iterable<T>.firstNotNullOfOrNullLaw(transform: (T) -> R?): R? {
  pre(true) { "kotlin.collections.firstNotNullOfOrNull pre-conditions" }
  return firstNotNullOfOrNull(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOfOrNull post-conditions" })
}

@Law
public fun <T> Iterable<T>.firstOrNullLaw(): T? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.firstOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun <T> List<T>.firstOrNullLaw(): T? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
@JvmName("flatMapLawIterable")
public inline fun <T, R> Iterable<T>.flatMapLaw(transform: (T) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapLawSequence")
public inline fun <T, R> Iterable<T>.flatMapLaw(transform: (T) -> Sequence<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapIndexedLawIterable")
public inline fun <T, R> Iterable<T>.flatMapIndexedLaw(transform: (index: Int, T) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
@JvmName("flatMapIndexedLawSequence")
public inline fun <T, R> Iterable<T>.flatMapIndexedLaw(transform: (index: Int, T) -> Sequence<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
@JvmName("flatMapIndexedToLawIterable")
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
@JvmName("flatMapIndexedToLawSequence")
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> Sequence<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
@JvmName("flatMapToLawIterable")
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapToLaw(
  destination: C,
  transform: (T) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
@JvmName("flatMapToLawSequence")
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapToLaw(
  destination: C,
  transform: (T) -> Sequence<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.foldLaw(initial: R, operation: (acc: R, T) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, T) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <T, R> List<T>.foldRightLaw(initial: R, operation: (T, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <T, R> List<T>.foldRightIndexedLaw(initial: R, operation: (index: Int, T, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.forEachLaw(action: (T) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.forEachIndexedLaw(action: (index: Int, T) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun <T> List<T>.getOrElseLaw(index: Int, defaultValue: (Int) -> T): T {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public fun <T> List<T>.getOrNullLaw(index: Int): T? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public inline fun <T, K> Iterable<T>.groupByLaw(keySelector: (T) -> K): Map<K, List<T>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <T, K, V> Iterable<T>.groupByLaw(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByToLaw(
  destination: M,
  keySelector: (T) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByToLaw(
  destination: M,
  keySelector: (T) -> K,
  valueTransform: (T) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <T, K> Iterable<T>.groupingByLaw(crossinline keySelector: (T) -> K): Grouping<T, K> {
  pre(true) { "kotlin.collections.groupingBy pre-conditions" }
  return groupingBy(keySelector)
    .post({ true }, { "kotlin.collections.groupingBy post-conditions" })
}

@Law
public fun <T> Iterable<T>.indexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public fun <T> List<T>.indexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.indexOfFirstLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun <T> List<T>.indexOfFirstLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.indexOfLastLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun <T> List<T>.indexOfLastLaw(predicate: (T) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public infix fun <T> Iterable<T>.intersectLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.intersect pre-conditions" }
  return intersect(other)
    .post({ true }, { "kotlin.collections.intersect post-conditions" })
}

@Law
public fun <T, A : Appendable /* = java.lang.Appendable */> Iterable<T>.joinToLaw(
  buffer: A,
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((T) -> CharSequence)?
): A {
  pre(true) { "kotlin.collections.joinTo pre-conditions" }
  return joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinTo post-conditions" })
}

@Law
public fun <T> Iterable<T>.joinToStringLaw(
  separator: CharSequence,
  prefix: CharSequence,
  postfix: CharSequence,
  limit: Int,
  truncated: CharSequence,
  transform: ((T) -> CharSequence)?
): String {
  pre(true) { "kotlin.collections.joinToString pre-conditions" }
  return joinToString(separator, prefix, postfix, limit, truncated, transform)
    .post({ true }, { "kotlin.collections.joinToString post-conditions" })
}

@Law
public fun <T> Iterable<T>.lastLaw(): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.lastLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun <T> List<T>.lastLaw(): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun <T> List<T>.lastLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public fun <T> Iterable<T>.lastIndexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun <T> List<T>.lastIndexOfLaw(element: T): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun <T> Iterable<T>.lastOrNullLaw(): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.lastOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun <T> List<T>.lastOrNullLaw(): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <T> List<T>.lastOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.mapLaw(transform: (T) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.mapIndexedLaw(transform: (index: Int, T) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <T, R : Any> Iterable<T>.mapIndexedNotNullLaw(transform: (index: Int, T) -> R?): List<R> {
  pre(true) { "kotlin.collections.mapIndexedNotNull pre-conditions" }
  return mapIndexedNotNull(transform)
    .post({ true }, { "kotlin.collections.mapIndexedNotNull post-conditions" })
}

@Law
public inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapIndexedNotNullToLaw(
  destination: C,
  transform: (index: Int, T) -> R?
): C {
  pre(true) { "kotlin.collections.mapIndexedNotNullTo pre-conditions" }
  return mapIndexedNotNullTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedNotNullTo post-conditions" })
}

@Law
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, T) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <T, R : Any> Iterable<T>.mapNotNullLaw(transform: (T) -> R?): List<R> {
  pre(true) { "kotlin.collections.mapNotNull pre-conditions" }
  return mapNotNull(transform)
    .post({ true }, { "kotlin.collections.mapNotNull post-conditions" })
}

@Law
public inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapNotNullToLaw(
  destination: C,
  transform: (T) -> R?
): C {
  pre(true) { "kotlin.collections.mapNotNullTo pre-conditions" }
  return mapNotNullTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapNotNullTo post-conditions" })
}

@Law
public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapToLaw(destination: C, transform: (T) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.maxByOrNullLaw(selector: (T) -> R): T? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.maxOfLaw(selector: (T) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.maxOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.maxOfLaw(selector: (T) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.maxOfOrNullLaw(selector: (T) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.maxOfOrNullLaw(selector: (T) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.maxOfOrNullLaw(selector: (T) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public fun <T : Comparable<T>> Iterable<T>.maxOrNullLaw(): T? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun Iterable<Double>.maxOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun Iterable<Float>.maxOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun <T> Iterable<T>.maxWithOrNullLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): T? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.minByOrNullLaw(selector: (T) -> R): T? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.minOfLaw(selector: (T) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.minOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.minOfLaw(selector: (T) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.minOfOrNullLaw(selector: (T) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.minOfOrNullLaw(selector: (T) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.minOfOrNullLaw(selector: (T) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (T) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public fun <T : Comparable<T>> Iterable<T>.minOrNullLaw(): T? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun Iterable<Double>.minOrNullLaw(): Double? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun Iterable<Float>.minOrNullLaw(): Float? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun <T> Iterable<T>.minWithOrNullLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): T? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun <T> Iterable<T>.minusLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(element)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Iterable<T>.minusLaw(elements: Array<out T>): List<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Iterable<T>.minusLaw(elements: Iterable<T>): List<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Iterable<T>.minusLaw(elements: Sequence<T>): List<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.minusElementLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.minusElement pre-conditions" }
  return minusElement(element)
    .post({ true }, { "kotlin.collections.minusElement post-conditions" })
}

@Law
public fun <T> Iterable<T>.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.noneLaw(predicate: (T) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <T, C : Iterable<T>> C.onEachLaw(action: (T) -> Unit): C {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun <T, C : Iterable<T>> C.onEachIndexedLaw(action: (index: Int, T) -> Unit): C {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.partitionLaw(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
  pre(true) { "kotlin.collections.partition pre-conditions" }
  return partition(predicate)
    .post({ true }, { "kotlin.collections.partition post-conditions" })
}

@Law
public fun <T> Collection<T>.plusLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Collection<T>.plusLaw(elements: Array<out T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Collection<T>.plusLaw(elements: Iterable<T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Collection<T>.plusLaw(elements: Sequence<T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Iterable<T>.plusLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Iterable<T>.plusLaw(elements: Array<out T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Iterable<T>.plusLaw(elements: Iterable<T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Iterable<T>.plusLaw(elements: Sequence<T>): List<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun <T> Collection<T>.plusElementLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.plusElement pre-conditions" }
  return plusElement(element)
    .post({ true }, { "kotlin.collections.plusElement post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.plusElementLaw(element: T): List<T> {
  pre(true) { "kotlin.collections.plusElement pre-conditions" }
  return plusElement(element)
    .post({ true }, { "kotlin.collections.plusElement post-conditions" })
}

@Law
public inline fun <T> Collection<T>.randomLaw(): T {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun <T> Collection<T>.randomLaw(random: Random): T {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun <T> Collection<T>.randomOrNullLaw(): T? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun <T> Collection<T>.randomOrNullLaw(random: Random): T? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.reduceLaw(operation: (acc: S, T) -> S): S {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.reduceIndexedLaw(operation: (index: Int, acc: S, T) -> S): S {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.reduceIndexedOrNullLaw(operation: (index: Int, acc: S, T) -> S): S? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.reduceOrNullLaw(operation: (acc: S, T) -> S): S? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> List<T>.reduceRightLaw(operation: (T, acc: S) -> S): S {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun <S, T : S> List<T>.reduceRightIndexedLaw(operation: (index: Int, T, acc: S) -> S): S {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> List<T>.reduceRightIndexedOrNullLaw(operation: (index: Int, T, acc: S) -> S): S? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun <S, T : S> List<T>.reduceRightOrNullLaw(operation: (T, acc: S) -> S): S? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public fun <T : Any> Iterable<T?>.requireNoNullsNullableLaw(): Iterable<T> {
  pre(true) { "kotlin.collections.requireNoNulls pre-conditions" }
  return requireNoNulls()
    .post({ true }, { "kotlin.collections.requireNoNulls post-conditions" })
}

@Law
public fun <T : Any> List<T?>.requireNoNullsNullableLaw(): List<T> {
  pre(true) { "kotlin.collections.requireNoNulls pre-conditions" }
  return requireNoNulls()
    .post({ true }, { "kotlin.collections.requireNoNulls post-conditions" })
}

@Law
public fun <T> Iterable<T>.reversedLaw(): List<T> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.runningFoldLaw(initial: R, operation: (acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, T) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.runningReduceLaw(operation: (acc: S, T) -> S): List<S> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun <S, T : S> Iterable<T>.runningReduceIndexedLaw(operation: (index: Int, acc: S, T) -> S): List<S> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.scanLaw(initial: R, operation: (acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, T) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public fun <T> MutableList<T>.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun <T> Iterable<T>.singleLaw(): T {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.singleLaw(predicate: (T) -> Boolean): T {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun <T> List<T>.singleLaw(): T {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun <T> Iterable<T>.singleOrNullLaw(): T? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.singleOrNullLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun <T> List<T>.singleOrNullLaw(): T? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun <T> List<T>.sliceLaw(indices: Iterable<Int>): List<T> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun <T> List<T>.sliceLaw(indices: IntRange): List<T> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> MutableList<T>.sortByLaw(crossinline selector: (T) -> R?): Unit {
  pre(true) { "kotlin.collections.sortBy pre-conditions" }
  return sortBy(selector)
    .post({ true }, { "kotlin.collections.sortBy post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> MutableList<T>.sortByDescendingLaw(crossinline selector: (T) -> R?): Unit {
  pre(true) { "kotlin.collections.sortByDescending pre-conditions" }
  return sortByDescending(selector)
    .post({ true }, { "kotlin.collections.sortByDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> MutableList<T>.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Iterable<T>.sortedLaw(): List<T> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.sortedByLaw(crossinline selector: (T) -> R?): List<T> {
  pre(true) { "kotlin.collections.sortedBy pre-conditions" }
  return sortedBy(selector)
    .post({ true }, { "kotlin.collections.sortedBy post-conditions" })
}

@Law
public inline fun <T, R : Comparable<R>> Iterable<T>.sortedByDescendingLaw(crossinline selector: (T) -> R?): List<T> {
  pre(true) { "kotlin.collections.sortedByDescending pre-conditions" }
  return sortedByDescending(selector)
    .post({ true }, { "kotlin.collections.sortedByDescending post-conditions" })
}

@Law
public fun <T : Comparable<T>> Iterable<T>.sortedDescendingLaw(): List<T> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun <T> Iterable<T>.sortedWithLaw(comparator: Comparator<in T> /* = java.util.Comparator<in T> */): List<T> {
  pre(true) { "kotlin.collections.sortedWith pre-conditions" }
  return sortedWith(comparator)
    .post({ true }, { "kotlin.collections.sortedWith post-conditions" })
}

@Law
public infix fun <T> Iterable<T>.subtractLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.subtract pre-conditions" }
  return subtract(other)
    .post({ true }, { "kotlin.collections.subtract post-conditions" })
}

@Law
@JvmName("sumLawIterableByteInt")
public fun Iterable<Byte>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableDouble")
public fun Iterable<Double>.sumLaw(): Double {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableFloat")
public fun Iterable<Float>.sumLaw(): Float {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableIntInt")
public fun Iterable<Int>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableLong")
public fun Iterable<Long>.sumLaw(): Long {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableShortInt")
public fun Iterable<Short>.sumLaw(): Int {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumOfLawIterableDouble")
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIterableInt")
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIterableLong")
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIterableUInt")
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawIterableULong")
public inline fun <T> Iterable<T>.sumOfLaw(selector: (T) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
public fun <T> Iterable<T>.takeLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun <T> List<T>.takeLastLaw(n: Int): List<T> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public inline fun <T> List<T>.takeLastWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun <T> Iterable<T>.takeWhileLaw(predicate: (T) -> Boolean): List<T> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public fun Collection<Boolean>.toBooleanArrayLaw(): BooleanArray {
  pre(true) { "kotlin.collections.toBooleanArray pre-conditions" }
  return toBooleanArray()
    .post({ true }, { "kotlin.collections.toBooleanArray post-conditions" })
}

@Law
public fun Collection<Byte>.toByteArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.toByteArray pre-conditions" }
  return toByteArray()
    .post({ true }, { "kotlin.collections.toByteArray post-conditions" })
}

@Law
public fun Collection<Char>.toCharArrayLaw(): CharArray {
  pre(true) { "kotlin.collections.toCharArray pre-conditions" }
  return toCharArray()
    .post({ true }, { "kotlin.collections.toCharArray post-conditions" })
}

@Law
public fun <T, C : MutableCollection<in T>> Iterable<T>.toCollectionLaw(destination: C): C {
  pre(true) { "kotlin.collections.toCollection pre-conditions" }
  return toCollection(destination)
    .post({ true }, { "kotlin.collections.toCollection post-conditions" })
}

@Law
public fun Collection<Double>.toDoubleArrayLaw(): DoubleArray {
  pre(true) { "kotlin.collections.toDoubleArray pre-conditions" }
  return toDoubleArray()
    .post({ true }, { "kotlin.collections.toDoubleArray post-conditions" })
}

@Law
public fun Collection<Float>.toFloatArrayLaw(): FloatArray {
  pre(true) { "kotlin.collections.toFloatArray pre-conditions" }
  return toFloatArray()
    .post({ true }, { "kotlin.collections.toFloatArray post-conditions" })
}

@Law
public fun <T> Iterable<T>.toHashSetLaw(): HashSet<T> /* = java.util.HashSet<T> */ {
  pre(true) { "kotlin.collections.toHashSet pre-conditions" }
  return toHashSet()
    .post({ true }, { "kotlin.collections.toHashSet post-conditions" })
}

@Law
public fun Collection<Int>.toIntArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.toIntArray pre-conditions" }
  return toIntArray()
    .post({ true }, { "kotlin.collections.toIntArray post-conditions" })
}

@Law
public fun <T> Iterable<T>.toListLaw(): List<T> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun Collection<Long>.toLongArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.toLongArray pre-conditions" }
  return toLongArray()
    .post({ true }, { "kotlin.collections.toLongArray post-conditions" })
}

@Law
public fun <T> Collection<T>.toMutableListLaw(): MutableList<T> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun <T> Iterable<T>.toMutableListLaw(): MutableList<T> {
  pre(true) { "kotlin.collections.toMutableList pre-conditions" }
  return toMutableList()
    .post({ true }, { "kotlin.collections.toMutableList post-conditions" })
}

@Law
public fun <T> Iterable<T>.toMutableSetLaw(): MutableSet<T> {
  pre(true) { "kotlin.collections.toMutableSet pre-conditions" }
  return toMutableSet()
    .post({ true }, { "kotlin.collections.toMutableSet post-conditions" })
}

@Law
public fun <T> Iterable<T>.toSetLaw(): Set<T> {
  pre(true) { "kotlin.collections.toSet pre-conditions" }
  return toSet()
    .post({ true }, { "kotlin.collections.toSet post-conditions" })
}

@Law
public fun Collection<Short>.toShortArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.toShortArray pre-conditions" }
  return toShortArray()
    .post({ true }, { "kotlin.collections.toShortArray post-conditions" })
}

@Law
public infix fun <T> Iterable<T>.unionLaw(other: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.union pre-conditions" }
  return union(other)
    .post({ true }, { "kotlin.collections.union post-conditions" })
}

@Law
public fun <T> Iterable<T>.windowedLaw(size: Int, step: Int, partialWindows: Boolean): List<List<T>> {
  pre(true) { "kotlin.collections.windowed pre-conditions" }
  return windowed(size, step, partialWindows)
    .post({ true }, { "kotlin.collections.windowed post-conditions" })
}

@Law
public fun <T, R> Iterable<T>.windowedLaw(
  size: Int,
  step: Int,
  partialWindows: Boolean,
  transform: (List<T>) -> R
): List<R> {
  pre(true) { "kotlin.collections.windowed pre-conditions" }
  return windowed(size, step, partialWindows, transform)
    .post({ true }, { "kotlin.collections.windowed post-conditions" })
}

@Law
public fun <T> Iterable<T>.withIndexLaw(): Iterable<IndexedValue<T>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public infix fun <T, R> Iterable<T>.zipLaw(other: Array<out R>): List<Pair<T, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <T, R, V> Iterable<T>.zipLaw(other: Array<out R>, transform: (a: T, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <T, R> Iterable<T>.zipLaw(other: Iterable<R>): List<Pair<T, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <T, R, V> Iterable<T>.zipLaw(other: Iterable<R>, transform: (a: T, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public fun <T> Iterable<T>.zipWithNextLaw(): List<Pair<T, T>> {
  pre(true) { "kotlin.collections.zipWithNext pre-conditions" }
  return zipWithNext()
    .post({ true }, { "kotlin.collections.zipWithNext post-conditions" })
}

@Law
public inline fun <T, R> Iterable<T>.zipWithNextLaw(transform: (a: T, b: T) -> R): List<R> {
  pre(true) { "kotlin.collections.zipWithNext pre-conditions" }
  return zipWithNext(transform)
    .post({ true }, { "kotlin.collections.zipWithNext post-conditions" })
}

@Law
public fun <T, K> Grouping<T, K>.eachCountLaw(): Map<K, Int> {
  pre(true) { "kotlin.collections.eachCount pre-conditions" }
  return eachCount()
    .post({ true }, { "kotlin.collections.eachCount post-conditions" })
}

@Law
public inline fun <T, K, R> Grouping<T, K>.aggregateLaw(operation: (key: K, accumulator: R?, element: T, first: Boolean) -> R): Map<K, R> {
  pre(true) { "kotlin.collections.aggregate pre-conditions" }
  return aggregate(operation)
    .post({ true }, { "kotlin.collections.aggregate post-conditions" })
}

@Law
public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.aggregateToLaw(
  destination: M,
  operation: (key: K, accumulator: R?, element: T, first: Boolean) -> R
): M {
  pre(true) { "kotlin.collections.aggregateTo pre-conditions" }
  return aggregateTo(destination, operation)
    .post({ true }, { "kotlin.collections.aggregateTo post-conditions" })
}

@Law
public fun <T, K, M : MutableMap<in K, Int>> Grouping<T, K>.eachCountToLaw(destination: M): M {
  pre(true) { "kotlin.collections.eachCountTo pre-conditions" }
  return eachCountTo(destination)
    .post({ true }, { "kotlin.collections.eachCountTo post-conditions" })
}

@Law
public inline fun <T, K, R> Grouping<T, K>.foldLaw(
  initialValueSelector: (key: K, element: T) -> R,
  operation: (key: K, accumulator: R, element: T) -> R
): Map<K, R> {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initialValueSelector, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <T, K, R> Grouping<T, K>.foldLaw(
  initialValue: R,
  operation: (accumulator: R, element: T) -> R
): Map<K, R> {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initialValue, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.foldToLaw(
  destination: M,
  initialValueSelector: (key: K, element: T) -> R,
  operation: (key: K, accumulator: R, element: T) -> R
): M {
  pre(true) { "kotlin.collections.foldTo pre-conditions" }
  return foldTo(destination, initialValueSelector, operation)
    .post({ true }, { "kotlin.collections.foldTo post-conditions" })
}

@Law
public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.foldToLaw(
  destination: M,
  initialValue: R,
  operation: (accumulator: R, element: T) -> R
): M {
  pre(true) { "kotlin.collections.foldTo pre-conditions" }
  return foldTo(destination, initialValue, operation)
    .post({ true }, { "kotlin.collections.foldTo post-conditions" })
}

@Law
public inline fun <S, T : S, K> Grouping<T, K>.reduceLaw(operation: (key: K, accumulator: S, element: T) -> S): Map<K, S> {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun <S, T : S, K, M : MutableMap<in K, S>> Grouping<T, K>.reduceToLaw(
  destination: M,
  operation: (key: K, accumulator: S, element: T) -> S
): M {
  pre(true) { "kotlin.collections.reduceTo pre-conditions" }
  return reduceTo(destination, operation)
    .post({ true }, { "kotlin.collections.reduceTo post-conditions" })
}

@Law
@JvmName("withDefaultLawMap")
public fun <K, V> Map<K, V>.withDefaultLaw(defaultValue: (key: K) -> V): Map<K, V> {
  pre(true) { "kotlin.collections.withDefault pre-conditions" }
  return withDefault(defaultValue)
    .post({ true }, { "kotlin.collections.withDefault post-conditions" })
}

@Law
@JvmName("withDefaultLawMutableMap")
public fun <K, V> MutableMap<K, V>.withDefaultLaw(defaultValue: (key: K) -> V): MutableMap<K, V> {
  pre(true) { "kotlin.collections.withDefault pre-conditions" }
  return withDefault(defaultValue)
    .post({ true }, { "kotlin.collections.withDefault post-conditions" })
}

@Law
public fun <K, V> mapOfLaw(pair: Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.mapOf pre-conditions" }
  return mapOf(pair)
    .post({ true }, { "kotlin.collections.mapOf post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <K, V> buildMapLaw(capacity: Int, builderAction: MutableMap<K, V>.() -> Unit): Map<K, V> {
  pre(true) { "kotlin.collections.buildMap pre-conditions" }
  return buildMap(capacity, builderAction)
    .post({ true }, { "kotlin.collections.buildMap post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <K, V> buildMapLaw(builderAction: MutableMap<K, V>.() -> Unit): Map<K, V> {
  pre(true) { "kotlin.collections.buildMap pre-conditions" }
  return buildMap(builderAction)
    .post({ true }, { "kotlin.collections.buildMap post-conditions" })
}

@Law
public fun <K, V> emptyMapLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.emptyMap pre-conditions" }
  return emptyMap<K, V>()
    .post({ true }, { "kotlin.collections.emptyMap post-conditions" })
}

@Law
public inline fun <K, V> hashMapOfLaw(): HashMap<K, V> /* = java.util.HashMap<K, V> */ {
  pre(true) { "kotlin.collections.hashMapOf pre-conditions" }
  return hashMapOf<K, V>()
    .post({ true }, { "kotlin.collections.hashMapOf post-conditions" })
}

@Law
public fun <K, V> hashMapOfLaw(vararg pairs: Pair<K, V>): HashMap<K, V> /* = java.util.HashMap<K, V> */ {
  pre(true) { "kotlin.collections.hashMapOf pre-conditions" }
  return hashMapOf(*pairs)
    .post({ true }, { "kotlin.collections.hashMapOf post-conditions" })
}

@Law
public inline fun <K, V> linkedMapOfLaw(): LinkedHashMap<K, V> /* = java.util.LinkedHashMap<K, V> */ {
  pre(true) { "kotlin.collections.linkedMapOf pre-conditions" }
  return linkedMapOf<K, V>()
    .post({ true }, { "kotlin.collections.linkedMapOf post-conditions" })
}

@Law
public fun <K, V> linkedMapOfLaw(vararg pairs: Pair<K, V>): LinkedHashMap<K, V> /* = java.util.LinkedHashMap<K, V> */ {
  pre(true) { "kotlin.collections.linkedMapOf pre-conditions" }
  return linkedMapOf(*pairs)
    .post({ true }, { "kotlin.collections.linkedMapOf post-conditions" })
}

@Law
public inline fun <K, V> mapOfLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.mapOf pre-conditions" }
  return mapOf<K, V>()
    .post({ true }, { "kotlin.collections.mapOf post-conditions" })
}

@Law
public fun <K, V> mapOfLaw(vararg pairs: Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.mapOf pre-conditions" }
  return mapOf(*pairs)
    .post({ true }, { "kotlin.collections.mapOf post-conditions" })
}

@Law
public inline fun <K, V> mutableMapOfLaw(): MutableMap<K, V> {
  pre(true) { "kotlin.collections.mutableMapOf pre-conditions" }
  return mutableMapOf<K, V>()
    .post({ true }, { "kotlin.collections.mutableMapOf post-conditions" })
}

@Law
public fun <K, V> mutableMapOfLaw(vararg pairs: Pair<K, V>): MutableMap<K, V> {
  pre(true) { "kotlin.collections.mutableMapOf pre-conditions" }
  return mutableMapOf(*pairs)
    .post({ true }, { "kotlin.collections.mutableMapOf post-conditions" })
}

@Law
public inline fun <K, V> Map.Entry<K, V>.component1Law(): K {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun <K, V> Map.Entry<K, V>.component2Law(): V {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.containsLaw(key: K): Boolean {
  pre(true) { "kotlin.collections.contains pre-conditions" }
  return contains(key)
    .post({ true }, { "kotlin.collections.contains post-conditions" })
}

@Law
public inline fun <K> Map<out K, *>.containsKeyLaw(key: K): Boolean {
  pre(true) { "kotlin.collections.containsKey pre-conditions" }
  return containsKey(key)
    .post({ true }, { "kotlin.collections.containsKey post-conditions" })
}

@Law
public inline fun <K, V> Map<K, V>.containsValueLaw(value: V): Boolean {
  pre(true) { "kotlin.collections.containsValue pre-conditions" }
  return containsValue(value)
    .post({ true }, { "kotlin.collections.containsValue post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.filterLaw(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.filterKeysLaw(predicate: (K) -> Boolean): Map<K, V> {
  pre(true) { "kotlin.collections.filterKeys pre-conditions" }
  return filterKeys(predicate)
    .post({ true }, { "kotlin.collections.filterKeys post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.filterNotLaw(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.filterNotToLaw(
  destination: M,
  predicate: (Map.Entry<K, V>) -> Boolean
): M {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.filterToLaw(
  destination: M,
  predicate: (Map.Entry<K, V>) -> Boolean
): M {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.filterValuesLaw(predicate: (V) -> Boolean): Map<K, V> {
  pre(true) { "kotlin.collections.filterValues pre-conditions" }
  return filterValues(predicate)
    .post({ true }, { "kotlin.collections.filterValues post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.getLaw(key: K): V? {
  pre(true) { "kotlin.collections.get pre-conditions" }
  return get(key)
    .post({ true }, { "kotlin.collections.get post-conditions" })
}

@Law
public inline fun <K, V> Map<K, V>.getOrElseLaw(key: K, defaultValue: () -> V): V {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(key, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.getOrPutLaw(key: K, defaultValue: () -> V): V {
  pre(true) { "kotlin.collections.getOrPut pre-conditions" }
  return getOrPut(key, defaultValue)
    .post({ true }, { "kotlin.collections.getOrPut post-conditions" })
}

@Law
public fun <K, V> Map<K, V>.getValueLaw(key: K): V {
  pre(true) { "kotlin.collections.getValue pre-conditions" }
  return getValue(key)
    .post({ true }, { "kotlin.collections.getValue post-conditions" })
}

// @Law
// public inline fun <M : Map<*, *>, R> M.ifEmptyLaw(defaultValue: () -> R): R where M : R  {
//  pre(true) { "kotlin.collections.ifEmpty pre-conditions" }
//  return ifEmpty(defaultValue)
//    .post({ true }, { "kotlin.collections.ifEmpty post-conditions" })
// }

@Law
public inline fun <K, V> Map<out K, V>.isNotEmptyLaw(): Boolean {
  pre(true) { "kotlin.collections.isNotEmpty pre-conditions" }
  return isNotEmpty()
    .post({ true }, { "kotlin.collections.isNotEmpty post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>?.isNullOrEmptyNullableLaw(): Boolean {
  pre(true) { "kotlin.collections.isNullOrEmpty pre-conditions" }
  return isNullOrEmpty()
    .post({ true }, { "kotlin.collections.isNullOrEmpty post-conditions" })
}

@Law
@JvmName("iteratorLawMap")
public inline fun <K, V> Map<out K, V>.iteratorLaw(): Iterator<Map.Entry<K, V>> {
  pre(true) { "kotlin.collections.iterator pre-conditions" }
  return iterator()
    .post({ true }, { "kotlin.collections.iterator post-conditions" })
}

@Law
@JvmName("iteratorLawMutableMap")
public inline fun <K, V> MutableMap<K, V>.iteratorLaw(): MutableIterator<MutableMap.MutableEntry<K, V>> {
  pre(true) { "kotlin.collections.iterator pre-conditions" }
  return iterator()
    .post({ true }, { "kotlin.collections.iterator post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.mapKeysLaw(transform: (Map.Entry<K, V>) -> R): Map<R, V> {
  pre(true) { "kotlin.collections.mapKeys pre-conditions" }
  return mapKeys(transform)
    .post({ true }, { "kotlin.collections.mapKeys post-conditions" })
}

@Law
public inline fun <K, V, R, M : MutableMap<in R, in V>> Map<out K, V>.mapKeysToLaw(
  destination: M,
  transform: (Map.Entry<K, V>) -> R
): M {
  pre(true) { "kotlin.collections.mapKeysTo pre-conditions" }
  return mapKeysTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapKeysTo post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.mapValuesLaw(transform: (Map.Entry<K, V>) -> R): Map<K, R> {
  pre(true) { "kotlin.collections.mapValues pre-conditions" }
  return mapValues(transform)
    .post({ true }, { "kotlin.collections.mapValues post-conditions" })
}

@Law
public inline fun <K, V, R, M : MutableMap<in K, in R>> Map<out K, V>.mapValuesToLaw(
  destination: M,
  transform: (Map.Entry<K, V>) -> R
): M {
  pre(true) { "kotlin.collections.mapValuesTo pre-conditions" }
  return mapValuesTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapValuesTo post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.minusLaw(key: K): Map<K, V> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(key)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.minusLaw(keys: Array<out K>): Map<K, V> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(keys)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.minusLaw(keys: Iterable<K>): Map<K, V> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(keys)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.minusLaw(keys: Sequence<K>): Map<K, V> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(keys)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.minusAssignLaw(key: K): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(key)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.minusAssignLaw(keys: Array<out K>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(keys)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.minusAssignLaw(keys: Iterable<K>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(keys)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.minusAssignLaw(keys: Sequence<K>): Unit {
  pre(true) { "kotlin.collections.minusAssign pre-conditions" }
  return minusAssign(keys)
    .post({ true }, { "kotlin.collections.minusAssign post-conditions" })
}

@Law
public inline fun <K, V> Map<K, V>?.orEmptyNullableLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.orEmpty pre-conditions" }
  return orEmpty()
    .post({ true }, { "kotlin.collections.orEmpty post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.plusLaw(pairs: Array<out Pair<K, V>>): Map<K, V> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(pairs)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.plusLaw(pair: Pair<K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(pair)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.plusLaw(pairs: Iterable<Pair<K, V>>): Map<K, V> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(pairs)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.plusLaw(map: Map<out K, V>): Map<K, V> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(map)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.plusLaw(pairs: Sequence<Pair<K, V>>): Map<K, V> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(pairs)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, in V>.plusAssignLaw(pairs: Array<out Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(pairs)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, in V>.plusAssignLaw(pair: Pair<K, V>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(pair)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, in V>.plusAssignLaw(pairs: Iterable<Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(pairs)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, in V>.plusAssignLaw(map: Map<K, V>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(map)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<in K, in V>.plusAssignLaw(pairs: Sequence<Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.plusAssign pre-conditions" }
  return plusAssign(pairs)
    .post({ true }, { "kotlin.collections.plusAssign post-conditions" })
}

@Law
public fun <K, V> MutableMap<in K, in V>.putAllLaw(pairs: Array<out Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.putAll pre-conditions" }
  return putAll(pairs)
    .post({ true }, { "kotlin.collections.putAll post-conditions" })
}

@Law
public fun <K, V> MutableMap<in K, in V>.putAllLaw(pairs: Iterable<Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.putAll pre-conditions" }
  return putAll(pairs)
    .post({ true }, { "kotlin.collections.putAll post-conditions" })
}

@Law
public fun <K, V> MutableMap<in K, in V>.putAllLaw(pairs: Sequence<Pair<K, V>>): Unit {
  pre(true) { "kotlin.collections.putAll pre-conditions" }
  return putAll(pairs)
    .post({ true }, { "kotlin.collections.putAll post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<out K, V>.removeLaw(key: K): V? {
  pre(true) { "kotlin.collections.remove pre-conditions" }
  return remove(key)
    .post({ true }, { "kotlin.collections.remove post-conditions" })
}

@Law
public inline fun <K, V> MutableMap<K, V>.setLaw(key: K, value: V): Unit {
  pre(true) { "kotlin.collections.set pre-conditions" }
  return set(key, value)
    .post({ true }, { "kotlin.collections.set post-conditions" })
}

@Law
public fun <K, V> Array<out Pair<K, V>>.toMapLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap()
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V, M : MutableMap<in K, in V>> Array<out Pair<K, V>>.toMapLaw(destination: M): M {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap(destination)
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V> Iterable<Pair<K, V>>.toMapLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap()
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V, M : MutableMap<in K, in V>> Iterable<Pair<K, V>>.toMapLaw(destination: M): M {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap(destination)
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.toMapLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap()
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.toMapLaw(destination: M): M {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap(destination)
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V> Sequence<Pair<K, V>>.toMapLaw(): Map<K, V> {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap()
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V, M : MutableMap<in K, in V>> Sequence<Pair<K, V>>.toMapLaw(destination: M): M {
  pre(true) { "kotlin.collections.toMap pre-conditions" }
  return toMap(destination)
    .post({ true }, { "kotlin.collections.toMap post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.toMutableMapLaw(): MutableMap<K, V> {
  pre(true) { "kotlin.collections.toMutableMap pre-conditions" }
  return toMutableMap()
    .post({ true }, { "kotlin.collections.toMutableMap post-conditions" })
}

@Law
public inline fun <K, V> Map.Entry<K, V>.toPairLaw(): Pair<K, V> {
  pre(true) { "kotlin.collections.toPair pre-conditions" }
  return toPair()
    .post({ true }, { "kotlin.collections.toPair post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.allLaw(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.anyLaw(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.asIterableLaw(): Iterable<Map.Entry<K, V>> {
  pre(true) { "kotlin.collections.asIterable pre-conditions" }
  return asIterable()
    .post({ true }, { "kotlin.collections.asIterable post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.asSequenceLaw(): Sequence<Map.Entry<K, V>> {
  pre(true) { "kotlin.collections.asSequence pre-conditions" }
  return asSequence()
    .post({ true }, { "kotlin.collections.asSequence post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.countLaw(): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count()
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.countLaw(predicate: (Map.Entry<K, V>) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun <K, V, R : Any> Map<out K, V>.firstNotNullOfLaw(transform: (Map.Entry<K, V>) -> R?): R {
  pre(true) { "kotlin.collections.firstNotNullOf pre-conditions" }
  return firstNotNullOf(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOf post-conditions" })
}

@Law
public inline fun <K, V, R : Any> Map<out K, V>.firstNotNullOfOrNullLaw(transform: (Map.Entry<K, V>) -> R?): R? {
  pre(true) { "kotlin.collections.firstNotNullOfOrNull pre-conditions" }
  return firstNotNullOfOrNull(transform)
    .post({ true }, { "kotlin.collections.firstNotNullOfOrNull post-conditions" })
}

@Law
@JvmName("flatMapLawMapIterable")
public inline fun <K, V, R> Map<out K, V>.flatMapLaw(transform: (Map.Entry<K, V>) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapLawMapSequence")
public inline fun <K, V, R> Map<out K, V>.flatMapLaw(transform: (Map.Entry<K, V>) -> Sequence<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
@JvmName("flatMapToLawMapIterable")
public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapToLaw(
  destination: C,
  transform: (Map.Entry<K, V>) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
@JvmName("flatMapToLawMapSequence")
public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapToLaw(
  destination: C,
  transform: (Map.Entry<K, V>) -> Sequence<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.forEachLaw(action: (Map.Entry<K, V>) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.mapLaw(transform: (Map.Entry<K, V>) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <K, V, R : Any> Map<out K, V>.mapNotNullLaw(transform: (Map.Entry<K, V>) -> R?): List<R> {
  pre(true) { "kotlin.collections.mapNotNull pre-conditions" }
  return mapNotNull(transform)
    .post({ true }, { "kotlin.collections.mapNotNull post-conditions" })
}

@Law
public inline fun <K, V, R : Any, C : MutableCollection<in R>> Map<out K, V>.mapNotNullToLaw(
  destination: C,
  transform: (Map.Entry<K, V>) -> R?
): C {
  pre(true) { "kotlin.collections.mapNotNullTo pre-conditions" }
  return mapNotNullTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapNotNullTo post-conditions" })
}

@Law
public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.mapToLaw(
  destination: C,
  transform: (Map.Entry<K, V>) -> R
): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxByOrNullLaw(selector: (Map.Entry<K, V>) -> R): Map.Entry<K, V>? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxOfLaw(selector: (Map.Entry<K, V>) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.maxOfLaw(selector: (Map.Entry<K, V>) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.maxOfLaw(selector: (Map.Entry<K, V>) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxOfOrNullLaw(selector: (Map.Entry<K, V>) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.maxOfOrNullLaw(selector: (Map.Entry<K, V>) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.maxOfOrNullLaw(selector: (Map.Entry<K, V>) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Map.Entry<K, V>) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Map.Entry<K, V>) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.maxWithOrNullLaw(comparator: Comparator<in Map.Entry<K, V>> /* = java.util.Comparator<in Map.Entry<K, V>> */): Map.Entry<K, V>? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minByOrNullLaw(selector: (Map.Entry<K, V>) -> R): Map.Entry<K, V>? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minOfLaw(selector: (Map.Entry<K, V>) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.minOfLaw(selector: (Map.Entry<K, V>) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.minOfLaw(selector: (Map.Entry<K, V>) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minOfOrNullLaw(selector: (Map.Entry<K, V>) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.minOfOrNullLaw(selector: (Map.Entry<K, V>) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.minOfOrNullLaw(selector: (Map.Entry<K, V>) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Map.Entry<K, V>) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <K, V, R> Map<out K, V>.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (Map.Entry<K, V>) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.minWithOrNullLaw(comparator: Comparator<in Map.Entry<K, V>> /* = java.util.Comparator<in Map.Entry<K, V>> */): Map.Entry<K, V>? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <K, V> Map<out K, V>.noneLaw(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun <K, V, M : Map<out K, V>> M.onEachLaw(action: (Map.Entry<K, V>) -> Unit): M {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun <K, V, M : Map<out K, V>> M.onEachIndexedLaw(action: (index: Int, Map.Entry<K, V>) -> Unit): M {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public fun <K, V> Map<out K, V>.toListLaw(): List<Pair<K, V>> {
  pre(true) { "kotlin.collections.toList pre-conditions" }
  return toList()
    .post({ true }, { "kotlin.collections.toList post-conditions" })
}

@Law
public fun <T> setOfLaw(element: T): Set<T> {
  pre(true) { "kotlin.collections.setOf pre-conditions" }
  return setOf(element)
    .post({ true }, { "kotlin.collections.setOf post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <E> buildSetLaw(capacity: Int, builderAction: MutableSet<E>.() -> Unit): Set<E> {
  pre(true) { "kotlin.collections.buildSet pre-conditions" }
  return buildSet(capacity, builderAction)
    .post({ true }, { "kotlin.collections.buildSet post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun <E> buildSetLaw(builderAction: MutableSet<E>.() -> Unit): Set<E> {
  pre(true) { "kotlin.collections.buildSet pre-conditions" }
  return buildSet(builderAction)
    .post({ true }, { "kotlin.collections.buildSet post-conditions" })
}

@Law
public fun <T> emptySetLaw(): Set<T> {
  pre(true) { "kotlin.collections.emptySet pre-conditions" }
  return emptySet<T>()
    .post({ true }, { "kotlin.collections.emptySet post-conditions" })
}

@Law
public inline fun <T> hashSetOfLaw(): HashSet<T> /* = java.util.HashSet<T> */ {
  pre(true) { "kotlin.collections.hashSetOf pre-conditions" }
  return hashSetOf<T>()
    .post({ true }, { "kotlin.collections.hashSetOf post-conditions" })
}

@Law
public fun <T> hashSetOfLaw(vararg elements: T): HashSet<T> /* = java.util.HashSet<T> */ {
  pre(true) { "kotlin.collections.hashSetOf pre-conditions" }
  return hashSetOf(*elements)
    .post({ true }, { "kotlin.collections.hashSetOf post-conditions" })
}

@Law
public inline fun <T> linkedSetOfLaw(): LinkedHashSet<T> /* = java.util.LinkedHashSet<T> */ {
  pre(true) { "kotlin.collections.linkedSetOf pre-conditions" }
  return linkedSetOf<T>()
    .post({ true }, { "kotlin.collections.linkedSetOf post-conditions" })
}

@Law
public fun <T> linkedSetOfLaw(vararg elements: T): LinkedHashSet<T> /* = java.util.LinkedHashSet<T> */ {
  pre(true) { "kotlin.collections.linkedSetOf pre-conditions" }
  return linkedSetOf(*elements)
    .post({ true }, { "kotlin.collections.linkedSetOf post-conditions" })
}

@Law
public inline fun <T> mutableSetOfLaw(): MutableSet<T> {
  pre(true) { "kotlin.collections.mutableSetOf pre-conditions" }
  return mutableSetOf<T>()
    .post({ true }, { "kotlin.collections.mutableSetOf post-conditions" })
}

@Law
public fun <T> mutableSetOfLaw(vararg elements: T): MutableSet<T> {
  pre(true) { "kotlin.collections.mutableSetOf pre-conditions" }
  return mutableSetOf(*elements)
    .post({ true }, { "kotlin.collections.mutableSetOf post-conditions" })
}

@Law
public inline fun <T> setOfLaw(): Set<T> {
  pre(true) { "kotlin.collections.setOf pre-conditions" }
  return setOf<T>()
    .post({ true }, { "kotlin.collections.setOf post-conditions" })
}

@Law
public fun <T> setOfLaw(vararg elements: T): Set<T> {
  pre(true) { "kotlin.collections.setOf pre-conditions" }
  return setOf(*elements)
    .post({ true }, { "kotlin.collections.setOf post-conditions" })
}

@Law
public fun <T : Any> setOfNotNullLaw(element: T?): Set<T> {
  pre(true) { "kotlin.collections.setOfNotNull pre-conditions" }
  return setOfNotNull(element)
    .post({ true }, { "kotlin.collections.setOfNotNull post-conditions" })
}

@Law
public fun <T : Any> setOfNotNullLaw(vararg elements: T?): Set<T> {
  pre(true) { "kotlin.collections.setOfNotNull pre-conditions" }
  return setOfNotNull(*elements)
    .post({ true }, { "kotlin.collections.setOfNotNull post-conditions" })
}

@Law
public inline fun <T> Set<T>?.orEmptyNullableLaw(): Set<T> {
  pre(true) { "kotlin.collections.orEmpty pre-conditions" }
  return orEmpty()
    .post({ true }, { "kotlin.collections.orEmpty post-conditions" })
}

@Law
public fun <T> Set<T>.minusLaw(element: T): Set<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(element)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Set<T>.minusLaw(elements: Array<out T>): Set<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Set<T>.minusLaw(elements: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public fun <T> Set<T>.minusLaw(elements: Sequence<T>): Set<T> {
  pre(true) { "kotlin.collections.minus pre-conditions" }
  return minus(elements)
    .post({ true }, { "kotlin.collections.minus post-conditions" })
}

@Law
public inline fun <T> Set<T>.minusElementLaw(element: T): Set<T> {
  pre(true) { "kotlin.collections.minusElement pre-conditions" }
  return minusElement(element)
    .post({ true }, { "kotlin.collections.minusElement post-conditions" })
}

@Law
public fun <T> Set<T>.plusLaw(element: T): Set<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Set<T>.plusLaw(elements: Array<out T>): Set<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Set<T>.plusLaw(elements: Iterable<T>): Set<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun <T> Set<T>.plusLaw(elements: Sequence<T>): Set<T> {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun <T> Set<T>.plusElementLaw(element: T): Set<T> {
  pre(true) { "kotlin.collections.plusElement pre-conditions" }
  return plusElement(element)
    .post({ true }, { "kotlin.collections.plusElement post-conditions" })
}

@Law
@JvmName("sumLawIterableUByte")
public fun Iterable<UByte>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableUInt")
public fun Iterable<UInt>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableULong")
public fun Iterable<ULong>.sumLaw(): ULong {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
@JvmName("sumLawIterableUShort")
public fun Iterable<UShort>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Collection<UByte>.toUByteArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.toUByteArray pre-conditions" }
  return toUByteArray()
    .post({ true }, { "kotlin.collections.toUByteArray post-conditions" })
}

@Law
public fun Collection<UInt>.toUIntArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.toUIntArray pre-conditions" }
  return toUIntArray()
    .post({ true }, { "kotlin.collections.toUIntArray post-conditions" })
}

@Law
public fun Collection<ULong>.toULongArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.toULongArray pre-conditions" }
  return toULongArray()
    .post({ true }, { "kotlin.collections.toULongArray post-conditions" })
}

@Law
public fun Collection<UShort>.toUShortArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.toUShortArray pre-conditions" }
  return toUShortArray()
    .post({ true }, { "kotlin.collections.toUShortArray post-conditions" })
}

@Law
@JvmName("getValueLawMap")
public inline fun <V, V1 : V> Map<in String, V>.getValueLaw(thisRef: Any?, property: KProperty<*>): V1 {
  pre(true) { "kotlin.collections.getValue pre-conditions" }
  return getValue<V, V1>(thisRef, property)
    .post({ true }, { "kotlin.collections.getValue post-conditions" })
}

@Law
@JvmName("getValueLawMutableMap")
public inline fun <V, V1 : V> MutableMap<in String, out V>.getValueLaw(thisRef: Any?, property: KProperty<*>): V1 {
  pre(true) { "kotlin.collections.getValue pre-conditions" }
  return getValue<V, V1>(thisRef, property)
    .post({ true }, { "kotlin.collections.getValue post-conditions" })
}

@Law
public inline fun <V> MutableMap<in String, in V>.setValueLaw(thisRef: Any?, property: KProperty<*>, value: V): Unit {
  pre(true) { "kotlin.collections.setValue pre-conditions" }
  return setValue(thisRef, property, value)
    .post({ true }, { "kotlin.collections.setValue post-conditions" })
}

@Law
public fun UByteArray.asListLaw(): List<UByte> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun UIntArray.asListLaw(): List<UInt> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun ULongArray.asListLaw(): List<ULong> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public fun UShortArray.asListLaw(): List<UShort> {
  pre(true) { "kotlin.collections.asList pre-conditions" }
  return asList()
    .post({ true }, { "kotlin.collections.asList post-conditions" })
}

@Law
public inline fun UByteArray.elementAtLaw(index: Int): UByte {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun UIntArray.elementAtLaw(index: Int): UInt {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun ULongArray.elementAtLaw(index: Int): ULong {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun UShortArray.elementAtLaw(index: Int): UShort {
  pre(true) { "kotlin.collections.elementAt pre-conditions" }
  return elementAt(index)
    .post({ true }, { "kotlin.collections.elementAt post-conditions" })
}

@Law
public inline fun UByteArray.allLaw(predicate: (UByte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun UIntArray.allLaw(predicate: (UInt) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun ULongArray.allLaw(predicate: (ULong) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun UShortArray.allLaw(predicate: (UShort) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.all pre-conditions" }
  return all(predicate)
    .post({ true }, { "kotlin.collections.all post-conditions" })
}

@Law
public inline fun UByteArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UByteArray.anyLaw(predicate: (UByte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UIntArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UIntArray.anyLaw(predicate: (UInt) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun ULongArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun ULongArray.anyLaw(predicate: (ULong) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UShortArray.anyLaw(): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any()
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UShortArray.anyLaw(predicate: (UShort) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.any pre-conditions" }
  return any(predicate)
    .post({ true }, { "kotlin.collections.any post-conditions" })
}

@Law
public inline fun UByteArray.asByteArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.asByteArray pre-conditions" }
  return asByteArray()
    .post({ true }, { "kotlin.collections.asByteArray post-conditions" })
}

@Law
public inline fun UIntArray.asIntArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.asIntArray pre-conditions" }
  return asIntArray()
    .post({ true }, { "kotlin.collections.asIntArray post-conditions" })
}

@Law
public inline fun ULongArray.asLongArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.asLongArray pre-conditions" }
  return asLongArray()
    .post({ true }, { "kotlin.collections.asLongArray post-conditions" })
}

@Law
public inline fun UShortArray.asShortArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.asShortArray pre-conditions" }
  return asShortArray()
    .post({ true }, { "kotlin.collections.asShortArray post-conditions" })
}

@Law
public inline fun ByteArray.asUByteArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.asUByteArray pre-conditions" }
  return asUByteArray()
    .post({ true }, { "kotlin.collections.asUByteArray post-conditions" })
}

@Law
public inline fun IntArray.asUIntArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.asUIntArray pre-conditions" }
  return asUIntArray()
    .post({ true }, { "kotlin.collections.asUIntArray post-conditions" })
}

@Law
public inline fun LongArray.asULongArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.asULongArray pre-conditions" }
  return asULongArray()
    .post({ true }, { "kotlin.collections.asULongArray post-conditions" })
}

@Law
public inline fun ShortArray.asUShortArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.asUShortArray pre-conditions" }
  return asUShortArray()
    .post({ true }, { "kotlin.collections.asUShortArray post-conditions" })
}

@Law
public inline fun <V> UByteArray.associateWithLaw(valueSelector: (UByte) -> V): Map<UByte, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> UIntArray.associateWithLaw(valueSelector: (UInt) -> V): Map<UInt, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> ULongArray.associateWithLaw(valueSelector: (ULong) -> V): Map<ULong, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V> UShortArray.associateWithLaw(valueSelector: (UShort) -> V): Map<UShort, V> {
  pre(true) { "kotlin.collections.associateWith pre-conditions" }
  return associateWith(valueSelector)
    .post({ true }, { "kotlin.collections.associateWith post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in UByte, in V>> UByteArray.associateWithToLaw(
  destination: M,
  valueSelector: (UByte) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in UInt, in V>> UIntArray.associateWithToLaw(
  destination: M,
  valueSelector: (UInt) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in ULong, in V>> ULongArray.associateWithToLaw(
  destination: M,
  valueSelector: (ULong) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun <V, M : MutableMap<in UShort, in V>> UShortArray.associateWithToLaw(
  destination: M,
  valueSelector: (UShort) -> V
): M {
  pre(true) { "kotlin.collections.associateWithTo pre-conditions" }
  return associateWithTo(destination, valueSelector)
    .post({ true }, { "kotlin.collections.associateWithTo post-conditions" })
}

@Law
public inline fun UByteArray.component1Law(): UByte {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun UIntArray.component1Law(): UInt {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun ULongArray.component1Law(): ULong {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun UShortArray.component1Law(): UShort {
  pre(true) { "kotlin.collections.component1 pre-conditions" }
  return component1()
    .post({ true }, { "kotlin.collections.component1 post-conditions" })
}

@Law
public inline fun UByteArray.component2Law(): UByte {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun UIntArray.component2Law(): UInt {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun ULongArray.component2Law(): ULong {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun UShortArray.component2Law(): UShort {
  pre(true) { "kotlin.collections.component2 pre-conditions" }
  return component2()
    .post({ true }, { "kotlin.collections.component2 post-conditions" })
}

@Law
public inline fun UByteArray.component3Law(): UByte {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun UIntArray.component3Law(): UInt {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun ULongArray.component3Law(): ULong {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun UShortArray.component3Law(): UShort {
  pre(true) { "kotlin.collections.component3 pre-conditions" }
  return component3()
    .post({ true }, { "kotlin.collections.component3 post-conditions" })
}

@Law
public inline fun UByteArray.component4Law(): UByte {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun UIntArray.component4Law(): UInt {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun ULongArray.component4Law(): ULong {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun UShortArray.component4Law(): UShort {
  pre(true) { "kotlin.collections.component4 pre-conditions" }
  return component4()
    .post({ true }, { "kotlin.collections.component4 post-conditions" })
}

@Law
public inline fun UByteArray.component5Law(): UByte {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun UIntArray.component5Law(): UInt {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun ULongArray.component5Law(): ULong {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public inline fun UShortArray.component5Law(): UShort {
  pre(true) { "kotlin.collections.component5 pre-conditions" }
  return component5()
    .post({ true }, { "kotlin.collections.component5 post-conditions" })
}

@Law
public infix fun UByteArray?.contentEqualsNullableLaw(other: UByteArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public infix fun UIntArray?.contentEqualsNullableLaw(other: UIntArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public infix fun ULongArray?.contentEqualsNullableLaw(other: ULongArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public infix fun UShortArray?.contentEqualsNullableLaw(other: UShortArray?): Boolean {
  pre(true) { "kotlin.collections.contentEquals pre-conditions" }
  return contentEquals(other)
    .post({ true }, { "kotlin.collections.contentEquals post-conditions" })
}

@Law
public fun UByteArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public fun UIntArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public fun ULongArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public fun UShortArray?.contentHashCodeNullableLaw(): Int {
  pre(true) { "kotlin.collections.contentHashCode pre-conditions" }
  return contentHashCode()
    .post({ true }, { "kotlin.collections.contentHashCode post-conditions" })
}

@Law
public fun UByteArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public fun UIntArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public fun ULongArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public fun UShortArray?.contentToStringNullableLaw(): String {
  pre(true) { "kotlin.collections.contentToString pre-conditions" }
  return contentToString()
    .post({ true }, { "kotlin.collections.contentToString post-conditions" })
}

@Law
public inline fun UByteArray.copyIntoLaw(
  destination: UByteArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): UByteArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public inline fun UIntArray.copyIntoLaw(
  destination: UIntArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): UIntArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public inline fun ULongArray.copyIntoLaw(
  destination: ULongArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): ULongArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public inline fun UShortArray.copyIntoLaw(
  destination: UShortArray,
  destinationOffset: Int,
  startIndex: Int,
  endIndex: Int
): UShortArray {
  pre(true) { "kotlin.collections.copyInto pre-conditions" }
  return copyInto(destination, destinationOffset, startIndex, endIndex)
    .post({ true }, { "kotlin.collections.copyInto post-conditions" })
}

@Law
public inline fun UByteArray.copyOfLaw(): UByteArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UByteArray.copyOfLaw(newSize: Int): UByteArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UIntArray.copyOfLaw(): UIntArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UIntArray.copyOfLaw(newSize: Int): UIntArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ULongArray.copyOfLaw(): ULongArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun ULongArray.copyOfLaw(newSize: Int): ULongArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UShortArray.copyOfLaw(): UShortArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf()
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UShortArray.copyOfLaw(newSize: Int): UShortArray {
  pre(true) { "kotlin.collections.copyOf pre-conditions" }
  return copyOf(newSize)
    .post({ true }, { "kotlin.collections.copyOf post-conditions" })
}

@Law
public inline fun UByteArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): UByteArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun UIntArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): UIntArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun ULongArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): ULongArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun UShortArray.copyOfRangeLaw(fromIndex: Int, toIndex: Int): UShortArray {
  pre(true) { "kotlin.collections.copyOfRange pre-conditions" }
  return copyOfRange(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.copyOfRange post-conditions" })
}

@Law
public inline fun UByteArray.countLaw(predicate: (UByte) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun UIntArray.countLaw(predicate: (UInt) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun ULongArray.countLaw(predicate: (ULong) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public inline fun UShortArray.countLaw(predicate: (UShort) -> Boolean): Int {
  pre(true) { "kotlin.collections.count pre-conditions" }
  return count(predicate)
    .post({ true }, { "kotlin.collections.count post-conditions" })
}

@Law
public fun UByteArray.dropLaw(n: Int): List<UByte> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun UIntArray.dropLaw(n: Int): List<UInt> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun ULongArray.dropLaw(n: Int): List<ULong> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun UShortArray.dropLaw(n: Int): List<UShort> {
  pre(true) { "kotlin.collections.drop pre-conditions" }
  return drop(n)
    .post({ true }, { "kotlin.collections.drop post-conditions" })
}

@Law
public fun UByteArray.dropLastLaw(n: Int): List<UByte> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun UIntArray.dropLastLaw(n: Int): List<UInt> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun ULongArray.dropLastLaw(n: Int): List<ULong> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public fun UShortArray.dropLastLaw(n: Int): List<UShort> {
  pre(true) { "kotlin.collections.dropLast pre-conditions" }
  return dropLast(n)
    .post({ true }, { "kotlin.collections.dropLast post-conditions" })
}

@Law
public inline fun UByteArray.dropLastWhileLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun UIntArray.dropLastWhileLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun ULongArray.dropLastWhileLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun UShortArray.dropLastWhileLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.dropLastWhile pre-conditions" }
  return dropLastWhile(predicate)
    .post({ true }, { "kotlin.collections.dropLastWhile post-conditions" })
}

@Law
public inline fun UByteArray.dropWhileLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun UIntArray.dropWhileLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun ULongArray.dropWhileLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun UShortArray.dropWhileLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.dropWhile pre-conditions" }
  return dropWhile(predicate)
    .post({ true }, { "kotlin.collections.dropWhile post-conditions" })
}

@Law
public inline fun UByteArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> UByte): UByte {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun UIntArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> UInt): UInt {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun ULongArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> ULong): ULong {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun UShortArray.elementAtOrElseLaw(index: Int, defaultValue: (Int) -> UShort): UShort {
  pre(true) { "kotlin.collections.elementAtOrElse pre-conditions" }
  return elementAtOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.elementAtOrElse post-conditions" })
}

@Law
public inline fun UByteArray.elementAtOrNullLaw(index: Int): UByte? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun UIntArray.elementAtOrNullLaw(index: Int): UInt? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun ULongArray.elementAtOrNullLaw(index: Int): ULong? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public inline fun UShortArray.elementAtOrNullLaw(index: Int): UShort? {
  pre(true) { "kotlin.collections.elementAtOrNull pre-conditions" }
  return elementAtOrNull(index)
    .post({ true }, { "kotlin.collections.elementAtOrNull post-conditions" })
}

@Law
public fun UByteArray.fillLaw(element: UByte, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun UIntArray.fillLaw(element: UInt, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun ULongArray.fillLaw(element: ULong, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public fun UShortArray.fillLaw(element: UShort, fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.fill pre-conditions" }
  return fill(element, fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.fill post-conditions" })
}

@Law
public inline fun UByteArray.filterLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun UIntArray.filterLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun ULongArray.filterLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun UShortArray.filterLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.filter pre-conditions" }
  return filter(predicate)
    .post({ true }, { "kotlin.collections.filter post-conditions" })
}

@Law
public inline fun UByteArray.filterIndexedLaw(predicate: (index: Int, UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun UIntArray.filterIndexedLaw(predicate: (index: Int, UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun ULongArray.filterIndexedLaw(predicate: (index: Int, ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun UShortArray.filterIndexedLaw(predicate: (index: Int, UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.filterIndexed pre-conditions" }
  return filterIndexed(predicate)
    .post({ true }, { "kotlin.collections.filterIndexed post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UByte>> UByteArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, UByte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UInt>> UIntArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, UInt) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in ULong>> ULongArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, ULong) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UShort>> UShortArray.filterIndexedToLaw(
  destination: C,
  predicate: (index: Int, UShort) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterIndexedTo pre-conditions" }
  return filterIndexedTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterIndexedTo post-conditions" })
}

@Law
public inline fun UByteArray.filterNotLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun UIntArray.filterNotLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun ULongArray.filterNotLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun UShortArray.filterNotLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.filterNot pre-conditions" }
  return filterNot(predicate)
    .post({ true }, { "kotlin.collections.filterNot post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UByte>> UByteArray.filterNotToLaw(
  destination: C,
  predicate: (UByte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UInt>> UIntArray.filterNotToLaw(
  destination: C,
  predicate: (UInt) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in ULong>> ULongArray.filterNotToLaw(
  destination: C,
  predicate: (ULong) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UShort>> UShortArray.filterNotToLaw(
  destination: C,
  predicate: (UShort) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterNotTo pre-conditions" }
  return filterNotTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterNotTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UByte>> UByteArray.filterToLaw(
  destination: C,
  predicate: (UByte) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UInt>> UIntArray.filterToLaw(
  destination: C,
  predicate: (UInt) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in ULong>> ULongArray.filterToLaw(
  destination: C,
  predicate: (ULong) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun <C : MutableCollection<in UShort>> UShortArray.filterToLaw(
  destination: C,
  predicate: (UShort) -> Boolean
): C {
  pre(true) { "kotlin.collections.filterTo pre-conditions" }
  return filterTo(destination, predicate)
    .post({ true }, { "kotlin.collections.filterTo post-conditions" })
}

@Law
public inline fun UByteArray.findLaw(predicate: (UByte) -> Boolean): UByte? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun UIntArray.findLaw(predicate: (UInt) -> Boolean): UInt? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun ULongArray.findLaw(predicate: (ULong) -> Boolean): ULong? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun UShortArray.findLaw(predicate: (UShort) -> Boolean): UShort? {
  pre(true) { "kotlin.collections.find pre-conditions" }
  return find(predicate)
    .post({ true }, { "kotlin.collections.find post-conditions" })
}

@Law
public inline fun UByteArray.findLastLaw(predicate: (UByte) -> Boolean): UByte? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun UIntArray.findLastLaw(predicate: (UInt) -> Boolean): UInt? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun ULongArray.findLastLaw(predicate: (ULong) -> Boolean): ULong? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun UShortArray.findLastLaw(predicate: (UShort) -> Boolean): UShort? {
  pre(true) { "kotlin.collections.findLast pre-conditions" }
  return findLast(predicate)
    .post({ true }, { "kotlin.collections.findLast post-conditions" })
}

@Law
public inline fun UByteArray.firstLaw(): UByte {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun UByteArray.firstLaw(predicate: (UByte) -> Boolean): UByte {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun UIntArray.firstLaw(): UInt {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun UIntArray.firstLaw(predicate: (UInt) -> Boolean): UInt {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun ULongArray.firstLaw(): ULong {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun ULongArray.firstLaw(predicate: (ULong) -> Boolean): ULong {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun UShortArray.firstLaw(): UShort {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first()
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public inline fun UShortArray.firstLaw(predicate: (UShort) -> Boolean): UShort {
  pre(true) { "kotlin.collections.first pre-conditions" }
  return first(predicate)
    .post({ true }, { "kotlin.collections.first post-conditions" })
}

@Law
public fun UByteArray.firstOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun UByteArray.firstOrNullLaw(predicate: (UByte) -> Boolean): UByte? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun UIntArray.firstOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun UIntArray.firstOrNullLaw(predicate: (UInt) -> Boolean): UInt? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun ULongArray.firstOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun ULongArray.firstOrNullLaw(predicate: (ULong) -> Boolean): ULong? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public fun UShortArray.firstOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull()
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun UShortArray.firstOrNullLaw(predicate: (UShort) -> Boolean): UShort? {
  pre(true) { "kotlin.collections.firstOrNull pre-conditions" }
  return firstOrNull(predicate)
    .post({ true }, { "kotlin.collections.firstOrNull post-conditions" })
}

@Law
public inline fun <R> UByteArray.flatMapLaw(transform: (UByte) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> UIntArray.flatMapLaw(transform: (UInt) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> ULongArray.flatMapLaw(transform: (ULong) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> UShortArray.flatMapLaw(transform: (UShort) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMap pre-conditions" }
  return flatMap(transform)
    .post({ true }, { "kotlin.collections.flatMap post-conditions" })
}

@Law
public inline fun <R> UByteArray.flatMapIndexedLaw(transform: (index: Int, UByte) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.flatMapIndexedLaw(transform: (index: Int, UInt) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.flatMapIndexedLaw(transform: (index: Int, ULong) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.flatMapIndexedLaw(transform: (index: Int, UShort) -> Iterable<R>): List<R> {
  pre(true) { "kotlin.collections.flatMapIndexed pre-conditions" }
  return flatMapIndexed(transform)
    .post({ true }, { "kotlin.collections.flatMapIndexed post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UByteArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, UByte) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UIntArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, UInt) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ULongArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, ULong) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UShortArray.flatMapIndexedToLaw(
  destination: C,
  transform: (index: Int, UShort) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapIndexedTo pre-conditions" }
  return flatMapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UByteArray.flatMapToLaw(
  destination: C,
  transform: (UByte) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UIntArray.flatMapToLaw(
  destination: C,
  transform: (UInt) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ULongArray.flatMapToLaw(
  destination: C,
  transform: (ULong) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UShortArray.flatMapToLaw(
  destination: C,
  transform: (UShort) -> Iterable<R>
): C {
  pre(true) { "kotlin.collections.flatMapTo pre-conditions" }
  return flatMapTo(destination, transform)
    .post({ true }, { "kotlin.collections.flatMapTo post-conditions" })
}

@Law
public inline fun <R> UByteArray.foldLaw(initial: R, operation: (acc: R, UByte) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> UIntArray.foldLaw(initial: R, operation: (acc: R, UInt) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> ULongArray.foldLaw(initial: R, operation: (acc: R, ULong) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> UShortArray.foldLaw(initial: R, operation: (acc: R, UShort) -> R): R {
  pre(true) { "kotlin.collections.fold pre-conditions" }
  return fold(initial, operation)
    .post({ true }, { "kotlin.collections.fold post-conditions" })
}

@Law
public inline fun <R> UByteArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, UByte) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, UInt) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, ULong) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.foldIndexedLaw(initial: R, operation: (index: Int, acc: R, UShort) -> R): R {
  pre(true) { "kotlin.collections.foldIndexed pre-conditions" }
  return foldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldIndexed post-conditions" })
}

@Law
public inline fun <R> UByteArray.foldRightLaw(initial: R, operation: (UByte, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> UIntArray.foldRightLaw(initial: R, operation: (UInt, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> ULongArray.foldRightLaw(initial: R, operation: (ULong, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> UShortArray.foldRightLaw(initial: R, operation: (UShort, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRight pre-conditions" }
  return foldRight(initial, operation)
    .post({ true }, { "kotlin.collections.foldRight post-conditions" })
}

@Law
public inline fun <R> UByteArray.foldRightIndexedLaw(initial: R, operation: (index: Int, UByte, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.foldRightIndexedLaw(initial: R, operation: (index: Int, UInt, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.foldRightIndexedLaw(initial: R, operation: (index: Int, ULong, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.foldRightIndexedLaw(initial: R, operation: (index: Int, UShort, acc: R) -> R): R {
  pre(true) { "kotlin.collections.foldRightIndexed pre-conditions" }
  return foldRightIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.foldRightIndexed post-conditions" })
}

@Law
public inline fun UByteArray.forEachLaw(action: (UByte) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun UIntArray.forEachLaw(action: (UInt) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun ULongArray.forEachLaw(action: (ULong) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun UShortArray.forEachLaw(action: (UShort) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEach pre-conditions" }
  return forEach(action)
    .post({ true }, { "kotlin.collections.forEach post-conditions" })
}

@Law
public inline fun UByteArray.forEachIndexedLaw(action: (index: Int, UByte) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun UIntArray.forEachIndexedLaw(action: (index: Int, UInt) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun ULongArray.forEachIndexedLaw(action: (index: Int, ULong) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun UShortArray.forEachIndexedLaw(action: (index: Int, UShort) -> Unit): Unit {
  pre(true) { "kotlin.collections.forEachIndexed pre-conditions" }
  return forEachIndexed(action)
    .post({ true }, { "kotlin.collections.forEachIndexed post-conditions" })
}

@Law
public inline fun UByteArray.getOrElseLaw(index: Int, defaultValue: (Int) -> UByte): UByte {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun UIntArray.getOrElseLaw(index: Int, defaultValue: (Int) -> UInt): UInt {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun ULongArray.getOrElseLaw(index: Int, defaultValue: (Int) -> ULong): ULong {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public inline fun UShortArray.getOrElseLaw(index: Int, defaultValue: (Int) -> UShort): UShort {
  pre(true) { "kotlin.collections.getOrElse pre-conditions" }
  return getOrElse(index, defaultValue)
    .post({ true }, { "kotlin.collections.getOrElse post-conditions" })
}

@Law
public fun UByteArray.getOrNullLaw(index: Int): UByte? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun UIntArray.getOrNullLaw(index: Int): UInt? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun ULongArray.getOrNullLaw(index: Int): ULong? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public fun UShortArray.getOrNullLaw(index: Int): UShort? {
  pre(true) { "kotlin.collections.getOrNull pre-conditions" }
  return getOrNull(index)
    .post({ true }, { "kotlin.collections.getOrNull post-conditions" })
}

@Law
public inline fun <K> UByteArray.groupByLaw(keySelector: (UByte) -> K): Map<K, List<UByte>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> UByteArray.groupByLaw(
  keySelector: (UByte) -> K,
  valueTransform: (UByte) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> UIntArray.groupByLaw(keySelector: (UInt) -> K): Map<K, List<UInt>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> UIntArray.groupByLaw(keySelector: (UInt) -> K, valueTransform: (UInt) -> V): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> ULongArray.groupByLaw(keySelector: (ULong) -> K): Map<K, List<ULong>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> ULongArray.groupByLaw(
  keySelector: (ULong) -> K,
  valueTransform: (ULong) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K> UShortArray.groupByLaw(keySelector: (UShort) -> K): Map<K, List<UShort>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, V> UShortArray.groupByLaw(
  keySelector: (UShort) -> K,
  valueTransform: (UShort) -> V
): Map<K, List<V>> {
  pre(true) { "kotlin.collections.groupBy pre-conditions" }
  return groupBy(keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupBy post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<UByte>>> UByteArray.groupByToLaw(
  destination: M,
  keySelector: (UByte) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UByteArray.groupByToLaw(
  destination: M,
  keySelector: (UByte) -> K,
  valueTransform: (UByte) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<UInt>>> UIntArray.groupByToLaw(
  destination: M,
  keySelector: (UInt) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UIntArray.groupByToLaw(
  destination: M,
  keySelector: (UInt) -> K,
  valueTransform: (UInt) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<ULong>>> ULongArray.groupByToLaw(
  destination: M,
  keySelector: (ULong) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ULongArray.groupByToLaw(
  destination: M,
  keySelector: (ULong) -> K,
  valueTransform: (ULong) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, M : MutableMap<in K, MutableList<UShort>>> UShortArray.groupByToLaw(
  destination: M,
  keySelector: (UShort) -> K
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UShortArray.groupByToLaw(
  destination: M,
  keySelector: (UShort) -> K,
  valueTransform: (UShort) -> V
): M {
  pre(true) { "kotlin.collections.groupByTo pre-conditions" }
  return groupByTo(destination, keySelector, valueTransform)
    .post({ true }, { "kotlin.collections.groupByTo post-conditions" })
}

@Law
public inline fun UByteArray.indexOfLaw(element: UByte): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun UIntArray.indexOfLaw(element: UInt): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun ULongArray.indexOfLaw(element: ULong): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun UShortArray.indexOfLaw(element: UShort): Int {
  pre(true) { "kotlin.collections.indexOf pre-conditions" }
  return indexOf(element)
    .post({ true }, { "kotlin.collections.indexOf post-conditions" })
}

@Law
public inline fun UByteArray.indexOfFirstLaw(predicate: (UByte) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun UIntArray.indexOfFirstLaw(predicate: (UInt) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun ULongArray.indexOfFirstLaw(predicate: (ULong) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun UShortArray.indexOfFirstLaw(predicate: (UShort) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfFirst pre-conditions" }
  return indexOfFirst(predicate)
    .post({ true }, { "kotlin.collections.indexOfFirst post-conditions" })
}

@Law
public inline fun UByteArray.indexOfLastLaw(predicate: (UByte) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun UIntArray.indexOfLastLaw(predicate: (UInt) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun ULongArray.indexOfLastLaw(predicate: (ULong) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun UShortArray.indexOfLastLaw(predicate: (UShort) -> Boolean): Int {
  pre(true) { "kotlin.collections.indexOfLast pre-conditions" }
  return indexOfLast(predicate)
    .post({ true }, { "kotlin.collections.indexOfLast post-conditions" })
}

@Law
public inline fun UByteArray.lastLaw(): UByte {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UByteArray.lastLaw(predicate: (UByte) -> Boolean): UByte {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UIntArray.lastLaw(): UInt {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UIntArray.lastLaw(predicate: (UInt) -> Boolean): UInt {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun ULongArray.lastLaw(): ULong {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun ULongArray.lastLaw(predicate: (ULong) -> Boolean): ULong {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UShortArray.lastLaw(): UShort {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last()
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UShortArray.lastLaw(predicate: (UShort) -> Boolean): UShort {
  pre(true) { "kotlin.collections.last pre-conditions" }
  return last(predicate)
    .post({ true }, { "kotlin.collections.last post-conditions" })
}

@Law
public inline fun UByteArray.lastIndexOfLaw(element: UByte): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public inline fun UIntArray.lastIndexOfLaw(element: UInt): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public inline fun ULongArray.lastIndexOfLaw(element: ULong): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public inline fun UShortArray.lastIndexOfLaw(element: UShort): Int {
  pre(true) { "kotlin.collections.lastIndexOf pre-conditions" }
  return lastIndexOf(element)
    .post({ true }, { "kotlin.collections.lastIndexOf post-conditions" })
}

@Law
public fun UByteArray.lastOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun UByteArray.lastOrNullLaw(predicate: (UByte) -> Boolean): UByte? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun UIntArray.lastOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun UIntArray.lastOrNullLaw(predicate: (UInt) -> Boolean): UInt? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun ULongArray.lastOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun ULongArray.lastOrNullLaw(predicate: (ULong) -> Boolean): ULong? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public fun UShortArray.lastOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull()
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun UShortArray.lastOrNullLaw(predicate: (UShort) -> Boolean): UShort? {
  pre(true) { "kotlin.collections.lastOrNull pre-conditions" }
  return lastOrNull(predicate)
    .post({ true }, { "kotlin.collections.lastOrNull post-conditions" })
}

@Law
public inline fun <R> UByteArray.mapLaw(transform: (UByte) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> UIntArray.mapLaw(transform: (UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> ULongArray.mapLaw(transform: (ULong) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> UShortArray.mapLaw(transform: (UShort) -> R): List<R> {
  pre(true) { "kotlin.collections.map pre-conditions" }
  return map(transform)
    .post({ true }, { "kotlin.collections.map post-conditions" })
}

@Law
public inline fun <R> UByteArray.mapIndexedLaw(transform: (index: Int, UByte) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.mapIndexedLaw(transform: (index: Int, UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.mapIndexedLaw(transform: (index: Int, ULong) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.mapIndexedLaw(transform: (index: Int, UShort) -> R): List<R> {
  pre(true) { "kotlin.collections.mapIndexed pre-conditions" }
  return mapIndexed(transform)
    .post({ true }, { "kotlin.collections.mapIndexed post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UByteArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, UByte) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UIntArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, UInt) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ULongArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, ULong) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UShortArray.mapIndexedToLaw(
  destination: C,
  transform: (index: Int, UShort) -> R
): C {
  pre(true) { "kotlin.collections.mapIndexedTo pre-conditions" }
  return mapIndexedTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapIndexedTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UByteArray.mapToLaw(destination: C, transform: (UByte) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UIntArray.mapToLaw(destination: C, transform: (UInt) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> ULongArray.mapToLaw(destination: C, transform: (ULong) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R, C : MutableCollection<in R>> UShortArray.mapToLaw(destination: C, transform: (UShort) -> R): C {
  pre(true) { "kotlin.collections.mapTo pre-conditions" }
  return mapTo(destination, transform)
    .post({ true }, { "kotlin.collections.mapTo post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.maxByOrNullLaw(selector: (UByte) -> R): UByte? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.maxByOrNullLaw(selector: (UInt) -> R): UInt? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.maxByOrNullLaw(selector: (ULong) -> R): ULong? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.maxByOrNullLaw(selector: (UShort) -> R): UShort? {
  pre(true) { "kotlin.collections.maxByOrNull pre-conditions" }
  return maxByOrNull(selector)
    .post({ true }, { "kotlin.collections.maxByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.maxOfLaw(selector: (UByte) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UByteArray.maxOfLaw(selector: (UByte) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UByteArray.maxOfLaw(selector: (UByte) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.maxOfLaw(selector: (UInt) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UIntArray.maxOfLaw(selector: (UInt) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UIntArray.maxOfLaw(selector: (UInt) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.maxOfLaw(selector: (ULong) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ULongArray.maxOfLaw(selector: (ULong) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun ULongArray.maxOfLaw(selector: (ULong) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.maxOfLaw(selector: (UShort) -> R): R {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UShortArray.maxOfLaw(selector: (UShort) -> Double): Double {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun UShortArray.maxOfLaw(selector: (UShort) -> Float): Float {
  pre(true) { "kotlin.collections.maxOf pre-conditions" }
  return maxOf(selector)
    .post({ true }, { "kotlin.collections.maxOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.maxOfOrNullLaw(selector: (UByte) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UByteArray.maxOfOrNullLaw(selector: (UByte) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UByteArray.maxOfOrNullLaw(selector: (UByte) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.maxOfOrNullLaw(selector: (UInt) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UIntArray.maxOfOrNullLaw(selector: (UInt) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UIntArray.maxOfOrNullLaw(selector: (UInt) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.maxOfOrNullLaw(selector: (ULong) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ULongArray.maxOfOrNullLaw(selector: (ULong) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun ULongArray.maxOfOrNullLaw(selector: (ULong) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.maxOfOrNullLaw(selector: (UShort) -> R): R? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UShortArray.maxOfOrNullLaw(selector: (UShort) -> Double): Double? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun UShortArray.maxOfOrNullLaw(selector: (UShort) -> Float): Float? {
  pre(true) { "kotlin.collections.maxOfOrNull pre-conditions" }
  return maxOfOrNull(selector)
    .post({ true }, { "kotlin.collections.maxOfOrNull post-conditions" })
}

@Law
public inline fun <R> UByteArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UByte) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> UIntArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UInt) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> ULongArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (ULong) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> UShortArray.maxOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UShort) -> R
): R {
  pre(true) { "kotlin.collections.maxOfWith pre-conditions" }
  return maxOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWith post-conditions" })
}

@Law
public inline fun <R> UByteArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UByte) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> UIntArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UInt) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ULongArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (ULong) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> UShortArray.maxOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UShort) -> R
): R? {
  pre(true) { "kotlin.collections.maxOfWithOrNull pre-conditions" }
  return maxOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.maxOfWithOrNull post-conditions" })
}

@Law
public fun UByteArray.maxOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun UIntArray.maxOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun ULongArray.maxOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun UShortArray.maxOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.maxOrNull pre-conditions" }
  return maxOrNull()
    .post({ true }, { "kotlin.collections.maxOrNull post-conditions" })
}

@Law
public fun UByteArray.maxWithOrNullLaw(comparator: Comparator<in UByte> /* = java.util.Comparator<in UByte> */): UByte? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun UIntArray.maxWithOrNullLaw(comparator: Comparator<in UInt> /* = java.util.Comparator<in UInt> */): UInt? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun ULongArray.maxWithOrNullLaw(comparator: Comparator<in ULong> /* = java.util.Comparator<in ULong> */): ULong? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public fun UShortArray.maxWithOrNullLaw(comparator: Comparator<in UShort> /* = java.util.Comparator<in UShort> */): UShort? {
  pre(true) { "kotlin.collections.maxWithOrNull pre-conditions" }
  return maxWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.maxWithOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.minByOrNullLaw(selector: (UByte) -> R): UByte? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.minByOrNullLaw(selector: (UInt) -> R): UInt? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.minByOrNullLaw(selector: (ULong) -> R): ULong? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.minByOrNullLaw(selector: (UShort) -> R): UShort? {
  pre(true) { "kotlin.collections.minByOrNull pre-conditions" }
  return minByOrNull(selector)
    .post({ true }, { "kotlin.collections.minByOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.minOfLaw(selector: (UByte) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UByteArray.minOfLaw(selector: (UByte) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UByteArray.minOfLaw(selector: (UByte) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.minOfLaw(selector: (UInt) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UIntArray.minOfLaw(selector: (UInt) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UIntArray.minOfLaw(selector: (UInt) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.minOfLaw(selector: (ULong) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ULongArray.minOfLaw(selector: (ULong) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun ULongArray.minOfLaw(selector: (ULong) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.minOfLaw(selector: (UShort) -> R): R {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UShortArray.minOfLaw(selector: (UShort) -> Double): Double {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun UShortArray.minOfLaw(selector: (UShort) -> Float): Float {
  pre(true) { "kotlin.collections.minOf pre-conditions" }
  return minOf(selector)
    .post({ true }, { "kotlin.collections.minOf post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UByteArray.minOfOrNullLaw(selector: (UByte) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UByteArray.minOfOrNullLaw(selector: (UByte) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UByteArray.minOfOrNullLaw(selector: (UByte) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UIntArray.minOfOrNullLaw(selector: (UInt) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UIntArray.minOfOrNullLaw(selector: (UInt) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UIntArray.minOfOrNullLaw(selector: (UInt) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> ULongArray.minOfOrNullLaw(selector: (ULong) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ULongArray.minOfOrNullLaw(selector: (ULong) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun ULongArray.minOfOrNullLaw(selector: (ULong) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R : Comparable<R>> UShortArray.minOfOrNullLaw(selector: (UShort) -> R): R? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UShortArray.minOfOrNullLaw(selector: (UShort) -> Double): Double? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun UShortArray.minOfOrNullLaw(selector: (UShort) -> Float): Float? {
  pre(true) { "kotlin.collections.minOfOrNull pre-conditions" }
  return minOfOrNull(selector)
    .post({ true }, { "kotlin.collections.minOfOrNull post-conditions" })
}

@Law
public inline fun <R> UByteArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UByte) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> UIntArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UInt) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> ULongArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (ULong) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> UShortArray.minOfWithLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UShort) -> R
): R {
  pre(true) { "kotlin.collections.minOfWith pre-conditions" }
  return minOfWith(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWith post-conditions" })
}

@Law
public inline fun <R> UByteArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UByte) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> UIntArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UInt) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> ULongArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (ULong) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public inline fun <R> UShortArray.minOfWithOrNullLaw(
  comparator: Comparator<in R> /* = java.util.Comparator<in R> */,
  selector: (UShort) -> R
): R? {
  pre(true) { "kotlin.collections.minOfWithOrNull pre-conditions" }
  return minOfWithOrNull(comparator, selector)
    .post({ true }, { "kotlin.collections.minOfWithOrNull post-conditions" })
}

@Law
public fun UByteArray.minOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun UIntArray.minOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun ULongArray.minOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun UShortArray.minOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.minOrNull pre-conditions" }
  return minOrNull()
    .post({ true }, { "kotlin.collections.minOrNull post-conditions" })
}

@Law
public fun UByteArray.minWithOrNullLaw(comparator: Comparator<in UByte> /* = java.util.Comparator<in UByte> */): UByte? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun UIntArray.minWithOrNullLaw(comparator: Comparator<in UInt> /* = java.util.Comparator<in UInt> */): UInt? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun ULongArray.minWithOrNullLaw(comparator: Comparator<in ULong> /* = java.util.Comparator<in ULong> */): ULong? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public fun UShortArray.minWithOrNullLaw(comparator: Comparator<in UShort> /* = java.util.Comparator<in UShort> */): UShort? {
  pre(true) { "kotlin.collections.minWithOrNull pre-conditions" }
  return minWithOrNull(comparator)
    .post({ true }, { "kotlin.collections.minWithOrNull post-conditions" })
}

@Law
public inline fun UByteArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UByteArray.noneLaw(predicate: (UByte) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UIntArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UIntArray.noneLaw(predicate: (UInt) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun ULongArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun ULongArray.noneLaw(predicate: (ULong) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UShortArray.noneLaw(): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none()
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UShortArray.noneLaw(predicate: (UShort) -> Boolean): Boolean {
  pre(true) { "kotlin.collections.none pre-conditions" }
  return none(predicate)
    .post({ true }, { "kotlin.collections.none post-conditions" })
}

@Law
public inline fun UByteArray.onEachLaw(action: (UByte) -> Unit): UByteArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun UIntArray.onEachLaw(action: (UInt) -> Unit): UIntArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun ULongArray.onEachLaw(action: (ULong) -> Unit): ULongArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun UShortArray.onEachLaw(action: (UShort) -> Unit): UShortArray {
  pre(true) { "kotlin.collections.onEach pre-conditions" }
  return onEach(action)
    .post({ true }, { "kotlin.collections.onEach post-conditions" })
}

@Law
public inline fun UByteArray.onEachIndexedLaw(action: (index: Int, UByte) -> Unit): UByteArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun UIntArray.onEachIndexedLaw(action: (index: Int, UInt) -> Unit): UIntArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun ULongArray.onEachIndexedLaw(action: (index: Int, ULong) -> Unit): ULongArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun UShortArray.onEachIndexedLaw(action: (index: Int, UShort) -> Unit): UShortArray {
  pre(true) { "kotlin.collections.onEachIndexed pre-conditions" }
  return onEachIndexed(action)
    .post({ true }, { "kotlin.collections.onEachIndexed post-conditions" })
}

@Law
public inline fun UByteArray.plusLaw(element: UByte): UByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UByteArray.plusLaw(elements: UByteArray): UByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun UByteArray.plusLaw(elements: Collection<UByte>): UByteArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UIntArray.plusLaw(element: UInt): UIntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UIntArray.plusLaw(elements: UIntArray): UIntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun UIntArray.plusLaw(elements: Collection<UInt>): UIntArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun ULongArray.plusLaw(element: ULong): ULongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun ULongArray.plusLaw(elements: ULongArray): ULongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun ULongArray.plusLaw(elements: Collection<ULong>): ULongArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UShortArray.plusLaw(element: UShort): UShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(element)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UShortArray.plusLaw(elements: UShortArray): UShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public fun UShortArray.plusLaw(elements: Collection<UShort>): UShortArray {
  pre(true) { "kotlin.collections.plus pre-conditions" }
  return plus(elements)
    .post({ true }, { "kotlin.collections.plus post-conditions" })
}

@Law
public inline fun UByteArray.randomLaw(): UByte {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun UByteArray.randomLaw(random: Random): UByte {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun UIntArray.randomLaw(): UInt {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun UIntArray.randomLaw(random: Random): UInt {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun ULongArray.randomLaw(): ULong {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun ULongArray.randomLaw(random: Random): ULong {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun UShortArray.randomLaw(): UShort {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random()
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public fun UShortArray.randomLaw(random: Random): UShort {
  pre(true) { "kotlin.collections.random pre-conditions" }
  return random(random)
    .post({ true }, { "kotlin.collections.random post-conditions" })
}

@Law
public inline fun UByteArray.randomOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun UByteArray.randomOrNullLaw(random: Random): UByte? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun UIntArray.randomOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun UIntArray.randomOrNullLaw(random: Random): UInt? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun ULongArray.randomOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun ULongArray.randomOrNullLaw(random: Random): ULong? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun UShortArray.randomOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull()
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public fun UShortArray.randomOrNullLaw(random: Random): UShort? {
  pre(true) { "kotlin.collections.randomOrNull pre-conditions" }
  return randomOrNull(random)
    .post({ true }, { "kotlin.collections.randomOrNull post-conditions" })
}

@Law
public inline fun UByteArray.reduceLaw(operation: (acc: UByte, UByte) -> UByte): UByte {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun UIntArray.reduceLaw(operation: (acc: UInt, UInt) -> UInt): UInt {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun ULongArray.reduceLaw(operation: (acc: ULong, ULong) -> ULong): ULong {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun UShortArray.reduceLaw(operation: (acc: UShort, UShort) -> UShort): UShort {
  pre(true) { "kotlin.collections.reduce pre-conditions" }
  return reduce(operation)
    .post({ true }, { "kotlin.collections.reduce post-conditions" })
}

@Law
public inline fun UByteArray.reduceIndexedLaw(operation: (index: Int, acc: UByte, UByte) -> UByte): UByte {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun UIntArray.reduceIndexedLaw(operation: (index: Int, acc: UInt, UInt) -> UInt): UInt {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun ULongArray.reduceIndexedLaw(operation: (index: Int, acc: ULong, ULong) -> ULong): ULong {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun UShortArray.reduceIndexedLaw(operation: (index: Int, acc: UShort, UShort) -> UShort): UShort {
  pre(true) { "kotlin.collections.reduceIndexed pre-conditions" }
  return reduceIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceIndexed post-conditions" })
}

@Law
public inline fun UByteArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: UByte, UByte) -> UByte): UByte? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun UIntArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: UInt, UInt) -> UInt): UInt? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun ULongArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: ULong, ULong) -> ULong): ULong? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun UShortArray.reduceIndexedOrNullLaw(operation: (index: Int, acc: UShort, UShort) -> UShort): UShort? {
  pre(true) { "kotlin.collections.reduceIndexedOrNull pre-conditions" }
  return reduceIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceIndexedOrNull post-conditions" })
}

@Law
public inline fun UByteArray.reduceOrNullLaw(operation: (acc: UByte, UByte) -> UByte): UByte? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun UIntArray.reduceOrNullLaw(operation: (acc: UInt, UInt) -> UInt): UInt? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun ULongArray.reduceOrNullLaw(operation: (acc: ULong, ULong) -> ULong): ULong? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun UShortArray.reduceOrNullLaw(operation: (acc: UShort, UShort) -> UShort): UShort? {
  pre(true) { "kotlin.collections.reduceOrNull pre-conditions" }
  return reduceOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceOrNull post-conditions" })
}

@Law
public inline fun UByteArray.reduceRightLaw(operation: (UByte, acc: UByte) -> UByte): UByte {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun UIntArray.reduceRightLaw(operation: (UInt, acc: UInt) -> UInt): UInt {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun ULongArray.reduceRightLaw(operation: (ULong, acc: ULong) -> ULong): ULong {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun UShortArray.reduceRightLaw(operation: (UShort, acc: UShort) -> UShort): UShort {
  pre(true) { "kotlin.collections.reduceRight pre-conditions" }
  return reduceRight(operation)
    .post({ true }, { "kotlin.collections.reduceRight post-conditions" })
}

@Law
public inline fun UByteArray.reduceRightIndexedLaw(operation: (index: Int, UByte, acc: UByte) -> UByte): UByte {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun UIntArray.reduceRightIndexedLaw(operation: (index: Int, UInt, acc: UInt) -> UInt): UInt {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun ULongArray.reduceRightIndexedLaw(operation: (index: Int, ULong, acc: ULong) -> ULong): ULong {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun UShortArray.reduceRightIndexedLaw(operation: (index: Int, UShort, acc: UShort) -> UShort): UShort {
  pre(true) { "kotlin.collections.reduceRightIndexed pre-conditions" }
  return reduceRightIndexed(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexed post-conditions" })
}

@Law
public inline fun UByteArray.reduceRightIndexedOrNullLaw(operation: (index: Int, UByte, acc: UByte) -> UByte): UByte? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun UIntArray.reduceRightIndexedOrNullLaw(operation: (index: Int, UInt, acc: UInt) -> UInt): UInt? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun ULongArray.reduceRightIndexedOrNullLaw(operation: (index: Int, ULong, acc: ULong) -> ULong): ULong? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun UShortArray.reduceRightIndexedOrNullLaw(operation: (index: Int, UShort, acc: UShort) -> UShort): UShort? {
  pre(true) { "kotlin.collections.reduceRightIndexedOrNull pre-conditions" }
  return reduceRightIndexedOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightIndexedOrNull post-conditions" })
}

@Law
public inline fun UByteArray.reduceRightOrNullLaw(operation: (UByte, acc: UByte) -> UByte): UByte? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun UIntArray.reduceRightOrNullLaw(operation: (UInt, acc: UInt) -> UInt): UInt? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun ULongArray.reduceRightOrNullLaw(operation: (ULong, acc: ULong) -> ULong): ULong? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun UShortArray.reduceRightOrNullLaw(operation: (UShort, acc: UShort) -> UShort): UShort? {
  pre(true) { "kotlin.collections.reduceRightOrNull pre-conditions" }
  return reduceRightOrNull(operation)
    .post({ true }, { "kotlin.collections.reduceRightOrNull post-conditions" })
}

@Law
public inline fun UByteArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun UByteArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun UIntArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun UIntArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun ULongArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun ULongArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun UShortArray.reverseLaw(): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse()
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public inline fun UShortArray.reverseLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.reverse pre-conditions" }
  return reverse(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.reverse post-conditions" })
}

@Law
public fun UByteArray.reversedLaw(): List<UByte> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun UIntArray.reversedLaw(): List<UInt> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun ULongArray.reversedLaw(): List<ULong> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public fun UShortArray.reversedLaw(): List<UShort> {
  pre(true) { "kotlin.collections.reversed pre-conditions" }
  return reversed()
    .post({ true }, { "kotlin.collections.reversed post-conditions" })
}

@Law
public inline fun UByteArray.reversedArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public inline fun UIntArray.reversedArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public inline fun ULongArray.reversedArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public inline fun UShortArray.reversedArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.reversedArray pre-conditions" }
  return reversedArray()
    .post({ true }, { "kotlin.collections.reversedArray post-conditions" })
}

@Law
public inline fun <R> UByteArray.runningFoldLaw(initial: R, operation: (acc: R, UByte) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> UIntArray.runningFoldLaw(initial: R, operation: (acc: R, UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> ULongArray.runningFoldLaw(initial: R, operation: (acc: R, ULong) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> UShortArray.runningFoldLaw(initial: R, operation: (acc: R, UShort) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFold pre-conditions" }
  return runningFold(initial, operation)
    .post({ true }, { "kotlin.collections.runningFold post-conditions" })
}

@Law
public inline fun <R> UByteArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, UByte) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.runningFoldIndexedLaw(initial: R, operation: (index: Int, acc: R, UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, ULong) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.runningFoldIndexedLaw(
  initial: R,
  operation: (index: Int, acc: R, UShort) -> R
): List<R> {
  pre(true) { "kotlin.collections.runningFoldIndexed pre-conditions" }
  return runningFoldIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.runningFoldIndexed post-conditions" })
}

@Law
public inline fun UByteArray.runningReduceLaw(operation: (acc: UByte, UByte) -> UByte): List<UByte> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun UIntArray.runningReduceLaw(operation: (acc: UInt, UInt) -> UInt): List<UInt> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun ULongArray.runningReduceLaw(operation: (acc: ULong, ULong) -> ULong): List<ULong> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun UShortArray.runningReduceLaw(operation: (acc: UShort, UShort) -> UShort): List<UShort> {
  pre(true) { "kotlin.collections.runningReduce pre-conditions" }
  return runningReduce(operation)
    .post({ true }, { "kotlin.collections.runningReduce post-conditions" })
}

@Law
public inline fun UByteArray.runningReduceIndexedLaw(operation: (index: Int, acc: UByte, UByte) -> UByte): List<UByte> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun UIntArray.runningReduceIndexedLaw(operation: (index: Int, acc: UInt, UInt) -> UInt): List<UInt> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun ULongArray.runningReduceIndexedLaw(operation: (index: Int, acc: ULong, ULong) -> ULong): List<ULong> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun UShortArray.runningReduceIndexedLaw(operation: (index: Int, acc: UShort, UShort) -> UShort): List<UShort> {
  pre(true) { "kotlin.collections.runningReduceIndexed pre-conditions" }
  return runningReduceIndexed(operation)
    .post({ true }, { "kotlin.collections.runningReduceIndexed post-conditions" })
}

@Law
public inline fun <R> UByteArray.scanLaw(initial: R, operation: (acc: R, UByte) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> UIntArray.scanLaw(initial: R, operation: (acc: R, UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> ULongArray.scanLaw(initial: R, operation: (acc: R, ULong) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> UShortArray.scanLaw(initial: R, operation: (acc: R, UShort) -> R): List<R> {
  pre(true) { "kotlin.collections.scan pre-conditions" }
  return scan(initial, operation)
    .post({ true }, { "kotlin.collections.scan post-conditions" })
}

@Law
public inline fun <R> UByteArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, UByte) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> UIntArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, UInt) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> ULongArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, ULong) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public inline fun <R> UShortArray.scanIndexedLaw(initial: R, operation: (index: Int, acc: R, UShort) -> R): List<R> {
  pre(true) { "kotlin.collections.scanIndexed pre-conditions" }
  return scanIndexed(initial, operation)
    .post({ true }, { "kotlin.collections.scanIndexed post-conditions" })
}

@Law
public fun UByteArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun UByteArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun UIntArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun UIntArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ULongArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun ULongArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun UShortArray.shuffleLaw(): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle()
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public fun UShortArray.shuffleLaw(random: Random): Unit {
  pre(true) { "kotlin.collections.shuffle pre-conditions" }
  return shuffle(random)
    .post({ true }, { "kotlin.collections.shuffle post-conditions" })
}

@Law
public inline fun UByteArray.singleLaw(): UByte {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun UByteArray.singleLaw(predicate: (UByte) -> Boolean): UByte {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun UIntArray.singleLaw(): UInt {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun UIntArray.singleLaw(predicate: (UInt) -> Boolean): UInt {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun ULongArray.singleLaw(): ULong {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun ULongArray.singleLaw(predicate: (ULong) -> Boolean): ULong {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun UShortArray.singleLaw(): UShort {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single()
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public inline fun UShortArray.singleLaw(predicate: (UShort) -> Boolean): UShort {
  pre(true) { "kotlin.collections.single pre-conditions" }
  return single(predicate)
    .post({ true }, { "kotlin.collections.single post-conditions" })
}

@Law
public fun UByteArray.singleOrNullLaw(): UByte? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun UByteArray.singleOrNullLaw(predicate: (UByte) -> Boolean): UByte? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun UIntArray.singleOrNullLaw(): UInt? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun UIntArray.singleOrNullLaw(predicate: (UInt) -> Boolean): UInt? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun ULongArray.singleOrNullLaw(): ULong? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun ULongArray.singleOrNullLaw(predicate: (ULong) -> Boolean): ULong? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun UShortArray.singleOrNullLaw(): UShort? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull()
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public inline fun UShortArray.singleOrNullLaw(predicate: (UShort) -> Boolean): UShort? {
  pre(true) { "kotlin.collections.singleOrNull pre-conditions" }
  return singleOrNull(predicate)
    .post({ true }, { "kotlin.collections.singleOrNull post-conditions" })
}

@Law
public fun UByteArray.sliceLaw(indices: Iterable<Int>): List<UByte> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UByteArray.sliceLaw(indices: IntRange): List<UByte> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UIntArray.sliceLaw(indices: Iterable<Int>): List<UInt> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UIntArray.sliceLaw(indices: IntRange): List<UInt> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ULongArray.sliceLaw(indices: Iterable<Int>): List<ULong> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun ULongArray.sliceLaw(indices: IntRange): List<ULong> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UShortArray.sliceLaw(indices: Iterable<Int>): List<UShort> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UShortArray.sliceLaw(indices: IntRange): List<UShort> {
  pre(true) { "kotlin.collections.slice pre-conditions" }
  return slice(indices)
    .post({ true }, { "kotlin.collections.slice post-conditions" })
}

@Law
public fun UByteArray.sliceArrayLaw(indices: Collection<Int>): UByteArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UByteArray.sliceArrayLaw(indices: IntRange): UByteArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UIntArray.sliceArrayLaw(indices: Collection<Int>): UIntArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UIntArray.sliceArrayLaw(indices: IntRange): UIntArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ULongArray.sliceArrayLaw(indices: Collection<Int>): ULongArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun ULongArray.sliceArrayLaw(indices: IntRange): ULongArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UShortArray.sliceArrayLaw(indices: Collection<Int>): UShortArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UShortArray.sliceArrayLaw(indices: IntRange): UShortArray {
  pre(true) { "kotlin.collections.sliceArray pre-conditions" }
  return sliceArray(indices)
    .post({ true }, { "kotlin.collections.sliceArray post-conditions" })
}

@Law
public fun UByteArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UByteArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UIntArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UIntArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ULongArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun ULongArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UShortArray.sortLaw(): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort()
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UShortArray.sortLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sort pre-conditions" }
  return sort(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sort post-conditions" })
}

@Law
public fun UByteArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UByteArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UIntArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UIntArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ULongArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun ULongArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UShortArray.sortDescendingLaw(): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending()
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UShortArray.sortDescendingLaw(fromIndex: Int, toIndex: Int): Unit {
  pre(true) { "kotlin.collections.sortDescending pre-conditions" }
  return sortDescending(fromIndex, toIndex)
    .post({ true }, { "kotlin.collections.sortDescending post-conditions" })
}

@Law
public fun UByteArray.sortedLaw(): List<UByte> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun UIntArray.sortedLaw(): List<UInt> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun ULongArray.sortedLaw(): List<ULong> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun UShortArray.sortedLaw(): List<UShort> {
  pre(true) { "kotlin.collections.sorted pre-conditions" }
  return sorted()
    .post({ true }, { "kotlin.collections.sorted post-conditions" })
}

@Law
public fun UByteArray.sortedArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun UIntArray.sortedArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun ULongArray.sortedArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun UShortArray.sortedArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.sortedArray pre-conditions" }
  return sortedArray()
    .post({ true }, { "kotlin.collections.sortedArray post-conditions" })
}

@Law
public fun UByteArray.sortedArrayDescendingLaw(): UByteArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun UIntArray.sortedArrayDescendingLaw(): UIntArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun ULongArray.sortedArrayDescendingLaw(): ULongArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun UShortArray.sortedArrayDescendingLaw(): UShortArray {
  pre(true) { "kotlin.collections.sortedArrayDescending pre-conditions" }
  return sortedArrayDescending()
    .post({ true }, { "kotlin.collections.sortedArrayDescending post-conditions" })
}

@Law
public fun UByteArray.sortedDescendingLaw(): List<UByte> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun UIntArray.sortedDescendingLaw(): List<UInt> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun ULongArray.sortedDescendingLaw(): List<ULong> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun UShortArray.sortedDescendingLaw(): List<UShort> {
  pre(true) { "kotlin.collections.sortedDescending pre-conditions" }
  return sortedDescending()
    .post({ true }, { "kotlin.collections.sortedDescending post-conditions" })
}

@Law
public fun Array<out UByte>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out UInt>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out ULong>.sumLaw(): ULong {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public fun Array<out UShort>.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun UByteArray.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun UIntArray.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun ULongArray.sumLaw(): ULong {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun UShortArray.sumLaw(): UInt {
  pre(true) { "kotlin.collections.sum pre-conditions" }
  return sum()
    .post({ true }, { "kotlin.collections.sum post-conditions" })
}

@Law
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUByteInt")
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUByteLong")
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUByteUInt")
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUByteULong")
public inline fun UByteArray.sumOfLaw(selector: (UByte) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUIntArrayDouble")
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUIntArrayInt")
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUIntArrayLong")
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUIntArrayUInt")
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUIntArrayULong")
public inline fun UIntArray.sumOfLaw(selector: (UInt) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawULongArrayDouble")
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawULongArrayInt")
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawULongArrayLong")
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawULongArrayUInt")
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawULongArrayULong")
public inline fun ULongArray.sumOfLaw(selector: (ULong) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUShortArrayDouble")
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> Double): Double {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUShortArrayInt")
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> Int): Int {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUShortArrayLong")
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> Long): Long {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUShortArrayUInt")
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> UInt): UInt {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUShortArrayULong")
public inline fun UShortArray.sumOfLaw(selector: (UShort) -> ULong): ULong {
  pre(true) { "kotlin.collections.sumOf pre-conditions" }
  return sumOf(selector)
    .post({ true }, { "kotlin.collections.sumOf post-conditions" })
}

@Law
@JvmName("sumOfLawUByteArrayUByte")
public fun UByteArray.takeLaw(n: Int): List<UByte> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun UIntArray.takeLaw(n: Int): List<UInt> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun ULongArray.takeLaw(n: Int): List<ULong> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun UShortArray.takeLaw(n: Int): List<UShort> {
  pre(true) { "kotlin.collections.take pre-conditions" }
  return take(n)
    .post({ true }, { "kotlin.collections.take post-conditions" })
}

@Law
public fun UByteArray.takeLastLaw(n: Int): List<UByte> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun UIntArray.takeLastLaw(n: Int): List<UInt> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun ULongArray.takeLastLaw(n: Int): List<ULong> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public fun UShortArray.takeLastLaw(n: Int): List<UShort> {
  pre(true) { "kotlin.collections.takeLast pre-conditions" }
  return takeLast(n)
    .post({ true }, { "kotlin.collections.takeLast post-conditions" })
}

@Law
public inline fun UByteArray.takeLastWhileLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun UIntArray.takeLastWhileLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun ULongArray.takeLastWhileLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun UShortArray.takeLastWhileLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.takeLastWhile pre-conditions" }
  return takeLastWhile(predicate)
    .post({ true }, { "kotlin.collections.takeLastWhile post-conditions" })
}

@Law
public inline fun UByteArray.takeWhileLaw(predicate: (UByte) -> Boolean): List<UByte> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun UIntArray.takeWhileLaw(predicate: (UInt) -> Boolean): List<UInt> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun ULongArray.takeWhileLaw(predicate: (ULong) -> Boolean): List<ULong> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun UShortArray.takeWhileLaw(predicate: (UShort) -> Boolean): List<UShort> {
  pre(true) { "kotlin.collections.takeWhile pre-conditions" }
  return takeWhile(predicate)
    .post({ true }, { "kotlin.collections.takeWhile post-conditions" })
}

@Law
public inline fun UByteArray.toByteArrayLaw(): ByteArray {
  pre(true) { "kotlin.collections.toByteArray pre-conditions" }
  return toByteArray()
    .post({ true }, { "kotlin.collections.toByteArray post-conditions" })
}

@Law
public inline fun UIntArray.toIntArrayLaw(): IntArray {
  pre(true) { "kotlin.collections.toIntArray pre-conditions" }
  return toIntArray()
    .post({ true }, { "kotlin.collections.toIntArray post-conditions" })
}

@Law
public inline fun ULongArray.toLongArrayLaw(): LongArray {
  pre(true) { "kotlin.collections.toLongArray pre-conditions" }
  return toLongArray()
    .post({ true }, { "kotlin.collections.toLongArray post-conditions" })
}

@Law
public inline fun UShortArray.toShortArrayLaw(): ShortArray {
  pre(true) { "kotlin.collections.toShortArray pre-conditions" }
  return toShortArray()
    .post({ true }, { "kotlin.collections.toShortArray post-conditions" })
}

@Law
public fun UByteArray.toTypedArrayLaw(): Array<UByte> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun UIntArray.toTypedArrayLaw(): Array<UInt> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun ULongArray.toTypedArrayLaw(): Array<ULong> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun UShortArray.toTypedArrayLaw(): Array<UShort> {
  pre(true) { "kotlin.collections.toTypedArray pre-conditions" }
  return toTypedArray()
    .post({ true }, { "kotlin.collections.toTypedArray post-conditions" })
}

@Law
public fun Array<out UByte>.toUByteArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.toUByteArray pre-conditions" }
  return toUByteArray()
    .post({ true }, { "kotlin.collections.toUByteArray post-conditions" })
}

@Law
public inline fun ByteArray.toUByteArrayLaw(): UByteArray {
  pre(true) { "kotlin.collections.toUByteArray pre-conditions" }
  return toUByteArray()
    .post({ true }, { "kotlin.collections.toUByteArray post-conditions" })
}

@Law
public fun Array<out UInt>.toUIntArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.toUIntArray pre-conditions" }
  return toUIntArray()
    .post({ true }, { "kotlin.collections.toUIntArray post-conditions" })
}

@Law
public inline fun IntArray.toUIntArrayLaw(): UIntArray {
  pre(true) { "kotlin.collections.toUIntArray pre-conditions" }
  return toUIntArray()
    .post({ true }, { "kotlin.collections.toUIntArray post-conditions" })
}

@Law
public fun Array<out ULong>.toULongArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.toULongArray pre-conditions" }
  return toULongArray()
    .post({ true }, { "kotlin.collections.toULongArray post-conditions" })
}

@Law
public inline fun LongArray.toULongArrayLaw(): ULongArray {
  pre(true) { "kotlin.collections.toULongArray pre-conditions" }
  return toULongArray()
    .post({ true }, { "kotlin.collections.toULongArray post-conditions" })
}

@Law
public fun Array<out UShort>.toUShortArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.toUShortArray pre-conditions" }
  return toUShortArray()
    .post({ true }, { "kotlin.collections.toUShortArray post-conditions" })
}

@Law
public inline fun ShortArray.toUShortArrayLaw(): UShortArray {
  pre(true) { "kotlin.collections.toUShortArray pre-conditions" }
  return toUShortArray()
    .post({ true }, { "kotlin.collections.toUShortArray post-conditions" })
}

@Law
public fun UByteArray.withIndexLaw(): Iterable<IndexedValue<UByte>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun UIntArray.withIndexLaw(): Iterable<IndexedValue<UInt>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun ULongArray.withIndexLaw(): Iterable<IndexedValue<ULong>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public fun UShortArray.withIndexLaw(): Iterable<IndexedValue<UShort>> {
  pre(true) { "kotlin.collections.withIndex pre-conditions" }
  return withIndex()
    .post({ true }, { "kotlin.collections.withIndex post-conditions" })
}

@Law
public infix fun <R> UByteArray.zipLaw(other: Array<out R>): List<Pair<UByte, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UByteArray.zipLaw(other: Array<out R>, transform: (a: UByte, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun UByteArray.zipLaw(other: UByteArray): List<Pair<UByte, UByte>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> UByteArray.zipLaw(other: UByteArray, transform: (a: UByte, b: UByte) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> UByteArray.zipLaw(other: Iterable<R>): List<Pair<UByte, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UByteArray.zipLaw(other: Iterable<R>, transform: (a: UByte, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> UIntArray.zipLaw(other: Array<out R>): List<Pair<UInt, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UIntArray.zipLaw(other: Array<out R>, transform: (a: UInt, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun UIntArray.zipLaw(other: UIntArray): List<Pair<UInt, UInt>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> UIntArray.zipLaw(other: UIntArray, transform: (a: UInt, b: UInt) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> UIntArray.zipLaw(other: Iterable<R>): List<Pair<UInt, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UIntArray.zipLaw(other: Iterable<R>, transform: (a: UInt, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ULongArray.zipLaw(other: Array<out R>): List<Pair<ULong, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ULongArray.zipLaw(other: Array<out R>, transform: (a: ULong, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun ULongArray.zipLaw(other: ULongArray): List<Pair<ULong, ULong>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> ULongArray.zipLaw(other: ULongArray, transform: (a: ULong, b: ULong) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> ULongArray.zipLaw(other: Iterable<R>): List<Pair<ULong, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> ULongArray.zipLaw(other: Iterable<R>, transform: (a: ULong, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> UShortArray.zipLaw(other: Array<out R>): List<Pair<UShort, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UShortArray.zipLaw(other: Array<out R>, transform: (a: UShort, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun UShortArray.zipLaw(other: UShortArray): List<Pair<UShort, UShort>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <V> UShortArray.zipLaw(other: UShortArray, transform: (a: UShort, b: UShort) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public infix fun <R> UShortArray.zipLaw(other: Iterable<R>): List<Pair<UShort, R>> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

@Law
public inline fun <R, V> UShortArray.zipLaw(other: Iterable<R>, transform: (a: UShort, b: R) -> V): List<V> {
  pre(true) { "kotlin.collections.zip pre-conditions" }
  return zip(other, transform)
    .post({ true }, { "kotlin.collections.zip post-conditions" })
}

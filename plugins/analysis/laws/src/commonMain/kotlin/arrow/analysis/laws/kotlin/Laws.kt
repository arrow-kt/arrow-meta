package arrow.analysis.laws.kotlin

import arrow.analysis.Law
import arrow.analysis.post
import arrow.analysis.pre
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

@Law
public fun Int.safeDiv(other: Int): Int {
  pre(other != 0) { "other is not zero" }
  return this / other
}

@Law
public fun Throwable.addSuppressedLaw(exception: Throwable): Unit {
  pre(true) { "addSuppressed pre-conditions" }
  return addSuppressed(exception)
    .post({ true }, { "addSuppressed post-conditions" })
}

@Law
public inline fun Throwable.printStackTraceLaw(): Unit {
  pre(true) { "printStackTrace pre-conditions" }
  return printStackTrace()
    .post({ true }, { "printStackTrace post-conditions" })
}

@Law
public fun Throwable.stackTraceToStringLaw(): String {
  pre(true) { "stackTraceToString pre-conditions" }
  return stackTraceToString()
    .post({ true }, { "stackTraceToString post-conditions" })
}

@Law
public fun <T> lazyLaw(initializer: () -> T): Lazy<T> {
  pre(true) { "lazy pre-conditions" }
  return lazy(initializer)
    .post({ true }, { "lazy post-conditions" })
}

@Law
public fun <T> lazyLaw(lock: Any?, initializer: () -> T): Lazy<T> {
  pre(true) { "lazy pre-conditions" }
  return lazy(lock, initializer)
    .post({ true }, { "lazy post-conditions" })
}

@Law
public fun <T> lazyLaw(mode: LazyThreadSafetyMode, initializer: () -> T): Lazy<T> {
  pre(true) { "lazy pre-conditions" }
  return lazy(mode, initializer)
    .post({ true }, { "lazy post-conditions" })
}

@Law
public fun <T> lazyOfLaw(value: T): Lazy<T> {
  pre(true) { "lazyOf pre-conditions" }
  return lazyOf(value)
    .post({ true }, { "lazyOf post-conditions" })
}

@Law
public inline fun <T> Lazy<T>.getValueLaw(thisRef: Any?, property: KProperty<*>): T {
  pre(true) { "getValue pre-conditions" }
  return getValue(thisRef, property)
    .post({ true }, { "getValue post-conditions" })
}

@Law
public inline fun Byte.floorDivLaw(other: Byte): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Byte.floorDivLaw(other: Int): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Byte.floorDivLaw(other: Long): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Byte.floorDivLaw(other: Short): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Int.floorDivLaw(other: Byte): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Int.floorDivLaw(other: Int): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Int.floorDivLaw(other: Long): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Int.floorDivLaw(other: Short): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Long.floorDivLaw(other: Byte): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Long.floorDivLaw(other: Int): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Long.floorDivLaw(other: Long): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Long.floorDivLaw(other: Short): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Short.floorDivLaw(other: Byte): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Short.floorDivLaw(other: Int): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Short.floorDivLaw(other: Long): Long {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Short.floorDivLaw(other: Short): Int {
  pre(true) { "floorDiv pre-conditions" }
  return floorDiv(other)
    .post({ true }, { "floorDiv post-conditions" })
}

@Law
public inline fun Byte.modLaw(other: Byte): Byte {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Byte.modLaw(other: Int): Int {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Byte.modLaw(other: Long): Long {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Byte.modLaw(other: Short): Short {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Double.modLaw(other: Double): Double {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Double.modLaw(other: Float): Double {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Float.modLaw(other: Double): Double {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Float.modLaw(other: Float): Float {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Int.modLaw(other: Byte): Byte {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Int.modLaw(other: Int): Int {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Int.modLaw(other: Long): Long {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Int.modLaw(other: Short): Short {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Long.modLaw(other: Byte): Byte {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Long.modLaw(other: Int): Int {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Long.modLaw(other: Long): Long {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Long.modLaw(other: Short): Short {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Short.modLaw(other: Byte): Byte {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Short.modLaw(other: Int): Int {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Short.modLaw(other: Long): Long {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Short.modLaw(other: Short): Short {
  pre(true) { "mod pre-conditions" }
  return mod(other)
    .post({ true }, { "mod post-conditions" })
}

@Law
public inline fun Int.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun Long.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun Int.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun Long.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun Int.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun Long.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun Double.Companion.fromBitsLaw(bits: Long): Double {
  pre(true) { "fromBits pre-conditions" }
  return fromBits(bits)
    .post({ true }, { "fromBits post-conditions" })
}

@Law
public inline fun Float.Companion.fromBitsLaw(bits: Int): Float {
  pre(true) { "fromBits pre-conditions" }
  return fromBits(bits)
    .post({ true }, { "fromBits post-conditions" })
}

@Law
public inline fun Double.isFiniteLaw(): Boolean {
  pre(true) { "isFinite pre-conditions" }
  return isFinite()
    .post({ true }, { "isFinite post-conditions" })
}

@Law
public inline fun Float.isFiniteLaw(): Boolean {
  pre(true) { "isFinite pre-conditions" }
  return isFinite()
    .post({ true }, { "isFinite post-conditions" })
}

@Law
public inline fun Double.isInfiniteLaw(): Boolean {
  pre(true) { "isInfinite pre-conditions" }
  return isInfinite()
    .post({ true }, { "isInfinite post-conditions" })
}

@Law
public inline fun Float.isInfiniteLaw(): Boolean {
  pre(true) { "isInfinite pre-conditions" }
  return isInfinite()
    .post({ true }, { "isInfinite post-conditions" })
}

@Law
public inline fun Double.isNaNLaw(): Boolean {
  pre(true) { "isNaN pre-conditions" }
  return isNaN()
    .post({ true }, { "isNaN post-conditions" })
}

@Law
public inline fun Float.isNaNLaw(): Boolean {
  pre(true) { "isNaN pre-conditions" }
  return isNaN()
    .post({ true }, { "isNaN post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun Int.rotateLeftLaw(bitCount: Int): Int {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun Long.rotateLeftLaw(bitCount: Int): Long {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun Int.rotateRightLaw(bitCount: Int): Int {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun Long.rotateRightLaw(bitCount: Int): Long {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@Law
public inline fun Int.takeHighestOneBitLaw(): Int {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun Long.takeHighestOneBitLaw(): Long {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun Int.takeLowestOneBitLaw(): Int {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun Long.takeLowestOneBitLaw(): Long {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun Double.toBitsLaw(): Long {
  pre(true) { "toBits pre-conditions" }
  return toBits()
    .post({ true }, { "toBits post-conditions" })
}

@Law
public inline fun Float.toBitsLaw(): Int {
  pre(true) { "toBits pre-conditions" }
  return toBits()
    .post({ true }, { "toBits post-conditions" })
}

@Law
public inline fun Double.toRawBitsLaw(): Long {
  pre(true) { "toRawBits pre-conditions" }
  return toRawBits()
    .post({ true }, { "toRawBits post-conditions" })
}

@Law
public inline fun Float.toRawBitsLaw(): Int {
  pre(true) { "toRawBits pre-conditions" }
  return toRawBits()
    .post({ true }, { "toRawBits post-conditions" })
}

@Law
public inline fun Byte.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun Short.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun Byte.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun Short.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun Byte.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun Short.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@ExperimentalStdlibApi
@Law
public fun Byte.rotateLeftLaw(bitCount: Int): Byte {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public fun Short.rotateLeftLaw(bitCount: Int): Short {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public fun Byte.rotateRightLaw(bitCount: Int): Byte {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@ExperimentalStdlibApi
@Law
public fun Short.rotateRightLaw(bitCount: Int): Short {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@Law
public inline fun Byte.takeHighestOneBitLaw(): Byte {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun Short.takeHighestOneBitLaw(): Short {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun Byte.takeLowestOneBitLaw(): Byte {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun Short.takeLowestOneBitLaw(): Short {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun checkLaw(value: Boolean): Unit {
  pre(true) { "check pre-conditions" }
  return check(value)
    .post({ true }, { "check post-conditions" })
}

@Law
public inline fun checkLaw(value: Boolean, lazyMessage: () -> Any): Unit {
  pre(true) { "check pre-conditions" }
  return check(value, lazyMessage)
    .post({ true }, { "check post-conditions" })
}

@Law
public inline fun <T : Any> checkNotNullLaw(value: T?): T {
  pre(true) { "checkNotNull pre-conditions" }
  return checkNotNull(value)
    .post({ true }, { "checkNotNull post-conditions" })
}

@Law
public inline fun <T : Any> checkNotNullLaw(value: T?, lazyMessage: () -> Any): T {
  pre(true) { "checkNotNull pre-conditions" }
  return checkNotNull(value, lazyMessage)
    .post({ true }, { "checkNotNull post-conditions" })
}

@Law
public inline fun errorLaw(message: Any): Nothing {
  pre(true) { "error pre-conditions" }
  return error(message)
    .post({ true }, { "error post-conditions" })
}

@Law
public inline fun TODOLaw(): Nothing {
  pre(true) { "TODO pre-conditions" }
  return TODO()
    .post({ true }, { "TODO post-conditions" })
}

@Law
public inline fun TODOLaw(reason: String): Nothing {
  pre(true) { "TODO pre-conditions" }
  return TODO(reason)
    .post({ true }, { "TODO post-conditions" })
}

@Law
public inline fun repeatLaw(times: Int, action: (Int) -> Unit): Unit {
  pre(true) { "repeat pre-conditions" }
  return repeat(times, action)
    .post({ true }, { "repeat post-conditions" })
}

@Law
public inline fun <R> runLaw(block: () -> R): R {
  pre(true) { "run pre-conditions" }
  return run(block)
    .post({ true }, { "run post-conditions" })
}

@Law
public inline fun <T, R> withLaw(receiver: T, block: T.() -> R): R {
  pre(true) { "with pre-conditions" }
  return with(receiver, block)
    .post({ true }, { "with post-conditions" })
}

@Law
public inline fun <T> T.takeIfLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "takeIf pre-conditions" }
  return takeIf(predicate)
    .post({ true }, { "takeIf post-conditions" })
}

@Law
public inline fun <T> T.takeUnlessLaw(predicate: (T) -> Boolean): T? {
  pre(true) { "takeUnless pre-conditions" }
  return takeUnless(predicate)
    .post({ true }, { "takeUnless post-conditions" })
}

@Law
public inline fun CharLaw(code: UShort): Char {
  pre(true) { "Char pre-conditions" }
  return Char(code)
    .post({ true }, { "Char post-conditions" })
}

@Law
public inline fun CharLaw(code: Int): Char {
  pre(true) { "Char pre-conditions" }
  return Char(code)
    .post({ true }, { "Char post-conditions" })
}

@OptIn(ExperimentalStdlibApi::class)
@Law
public fun <T, R> DeepRecursiveFunction<T, R>.invokeLaw(value: T): R {
  pre(true) { "invoke pre-conditions" }
  return invoke(value)
    .post({ true }, { "invoke post-conditions" })
}

@Law
public inline fun Any?.hashCodeLaw(): Int {
  pre(true) { "hashCode pre-conditions" }
  return hashCode()
    .post({ true }, { "hashCode post-conditions" })
}

@Law
public inline fun <V> KProperty0<V>.getValueLaw(thisRef: Any?, property: KProperty<*>): V {
  pre(true) { "getValue pre-conditions" }
  return getValue(thisRef, property)
    .post({ true }, { "getValue post-conditions" })
}

@Law
public inline fun <T, V> KProperty1<T, V>.getValueLaw(thisRef: T, property: KProperty<*>): V {
  pre(true) { "getValue pre-conditions" }
  return getValue(thisRef, property)
    .post({ true }, { "getValue post-conditions" })
}

@Law
public inline fun <V> KMutableProperty0<V>.setValueLaw(thisRef: Any?, property: KProperty<*>, value: V): Unit {
  pre(true) { "setValue pre-conditions" }
  return setValue(thisRef, property, value)
    .post({ true }, { "setValue post-conditions" })
}

@Law
public inline fun <T, V> KMutableProperty1<T, V>.setValueLaw(thisRef: T, property: KProperty<*>, value: V): Unit {
  pre(true) { "setValue pre-conditions" }
  return setValue(thisRef, property, value)
    .post({ true }, { "setValue post-conditions" })
}

@Law
public inline fun <R> runCatchingLaw(block: () -> R): Result<R> {
  pre(true) { "runCatching pre-conditions" }
  return runCatching(block)
    .post({ true }, { "runCatching post-conditions" })
}

@Law
public inline fun <R, T> Result<T>.foldLaw(onSuccess: (value: T) -> R, onFailure: (exception: Throwable) -> R): R {
  pre(true) { "fold pre-conditions" }
  return fold(onSuccess, onFailure)
    .post({ true }, { "fold post-conditions" })
}

@Law
public inline fun <R, T : R> Result<T>.getOrDefaultLaw(defaultValue: R): R {
  pre(true) { "getOrDefault pre-conditions" }
  return getOrDefault(defaultValue)
    .post({ true }, { "getOrDefault post-conditions" })
}

@Law
public inline fun <R, T : R> Result<T>.getOrElseLaw(onFailure: (exception: Throwable) -> R): R {
  pre(true) { "getOrElse pre-conditions" }
  return getOrElse(onFailure)
    .post({ true }, { "getOrElse post-conditions" })
}

@Law
public inline fun <T> Result<T>.getOrThrowLaw(): T {
  pre(true) { "getOrThrow pre-conditions" }
  return getOrThrow()
    .post({ true }, { "getOrThrow post-conditions" })
}

@Law
public inline fun <R, T> Result<T>.mapLaw(transform: (value: T) -> R): Result<R> {
  pre(true) { "map pre-conditions" }
  return map(transform)
    .post({ true }, { "map post-conditions" })
}

@Law
public inline fun <R, T> Result<T>.mapCatchingLaw(transform: (value: T) -> R): Result<R> {
  pre(true) { "mapCatching pre-conditions" }
  return mapCatching(transform)
    .post({ true }, { "mapCatching post-conditions" })
}

@Law
public inline fun <T> Result<T>.onFailureLaw(action: (exception: Throwable) -> Unit): Result<T> {
  pre(true) { "onFailure pre-conditions" }
  return onFailure(action)
    .post({ true }, { "onFailure post-conditions" })
}

@Law
public inline fun <T> Result<T>.onSuccessLaw(action: (value: T) -> Unit): Result<T> {
  pre(true) { "onSuccess pre-conditions" }
  return onSuccess(action)
    .post({ true }, { "onSuccess post-conditions" })
}

@Law
public inline fun <R, T : R> Result<T>.recoverLaw(transform: (exception: Throwable) -> R): Result<R> {
  pre(true) { "recover pre-conditions" }
  return recover(transform)
    .post({ true }, { "recover post-conditions" })
}

@Law
public inline fun <R, T : R> Result<T>.recoverCatchingLaw(transform: (exception: Throwable) -> R): Result<R> {
  pre(true) { "recoverCatching pre-conditions" }
  return recoverCatching(transform)
    .post({ true }, { "recoverCatching post-conditions" })
}

@Law
public inline fun <T, R> T.runCatchingLaw(block: T.() -> R): Result<R> {
  pre(true) { "runCatching pre-conditions" }
  return runCatching(block)
    .post({ true }, { "runCatching post-conditions" })
}

@Law
public infix fun <A, B> A.toLaw(that: B): Pair<A, B> {
  pre(true) { "to pre-conditions" }
  return to(that)
    .post({ true }, { "to post-conditions" })
}

@Law
public fun <T> Pair<T, T>.toListLaw(): List<T> {
  pre(true) { "toList pre-conditions" }
  return toList()
    .post({ true }, { "toList post-conditions" })
}

@Law
public fun <T> Triple<T, T, T>.toListLaw(): List<T> {
  pre(true) { "toList pre-conditions" }
  return toList()
    .post({ true }, { "toList post-conditions" })
}

@Law
public inline fun UByteArrayLaw(size: Int, init: (Int) -> UByte): UByteArray {
  pre(true) { "UByteArray pre-conditions" }
  return UByteArray(size, init)
    .post({ true }, { "UByteArray post-conditions" })
}

@Law
public inline fun ubyteArrayOfLaw(vararg elements: UByte): UByteArray {
  pre(true) { "ubyteArrayOf pre-conditions" }
  return ubyteArrayOf(*elements)
    .post({ true }, { "ubyteArrayOf post-conditions" })
}

@Law
public inline fun Byte.toUByteLaw(): UByte {
  pre(true) { "toUByte pre-conditions" }
  return toUByte()
    .post({ true }, { "toUByte post-conditions" })
}

@Law
public inline fun Int.toUByteLaw(): UByte {
  pre(true) { "toUByte pre-conditions" }
  return toUByte()
    .post({ true }, { "toUByte post-conditions" })
}

@Law
public inline fun Long.toUByteLaw(): UByte {
  pre(true) { "toUByte pre-conditions" }
  return toUByte()
    .post({ true }, { "toUByte post-conditions" })
}

@Law
public inline fun Short.toUByteLaw(): UByte {
  pre(true) { "toUByte pre-conditions" }
  return toUByte()
    .post({ true }, { "toUByte post-conditions" })
}

@Law
public inline fun UIntArrayLaw(size: Int, init: (Int) -> UInt): UIntArray {
  pre(true) { "UIntArray pre-conditions" }
  return UIntArray(size, init)
    .post({ true }, { "UIntArray post-conditions" })
}

@Law
public inline fun uintArrayOfLaw(vararg elements: UInt): UIntArray {
  pre(true) { "uintArrayOf pre-conditions" }
  return uintArrayOf(*elements)
    .post({ true }, { "uintArrayOf post-conditions" })
}

@Law
public inline fun Byte.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun Double.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun Float.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun Int.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun Long.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun Short.toUIntLaw(): UInt {
  pre(true) { "toUInt pre-conditions" }
  return toUInt()
    .post({ true }, { "toUInt post-conditions" })
}

@Law
public inline fun ulongArrayOfLaw(vararg elements: ULong): ULongArray {
  pre(true) { "ulongArrayOf pre-conditions" }
  return ulongArrayOf(*elements)
    .post({ true }, { "ulongArrayOf post-conditions" })
}

@Law
public inline fun Byte.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun Double.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun Float.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun Int.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun Long.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun Short.toULongLaw(): ULong {
  pre(true) { "toULong pre-conditions" }
  return toULong()
    .post({ true }, { "toULong post-conditions" })
}

@Law
public inline fun UByte.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun UInt.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun ULong.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun UShort.countLeadingZeroBitsLaw(): Int {
  pre(true) { "countLeadingZeroBits pre-conditions" }
  return countLeadingZeroBits()
    .post({ true }, { "countLeadingZeroBits post-conditions" })
}

@Law
public inline fun UByte.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun UInt.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun ULong.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun UShort.countOneBitsLaw(): Int {
  pre(true) { "countOneBits pre-conditions" }
  return countOneBits()
    .post({ true }, { "countOneBits post-conditions" })
}

@Law
public inline fun UByte.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun UInt.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun ULong.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@Law
public inline fun UShort.countTrailingZeroBitsLaw(): Int {
  pre(true) { "countTrailingZeroBits pre-conditions" }
  return countTrailingZeroBits()
    .post({ true }, { "countTrailingZeroBits post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UByte.rotateLeftLaw(bitCount: Int): UByte {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UInt.rotateLeftLaw(bitCount: Int): UInt {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun ULong.rotateLeftLaw(bitCount: Int): ULong {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UShort.rotateLeftLaw(bitCount: Int): UShort {
  pre(true) { "rotateLeft pre-conditions" }
  return rotateLeft(bitCount)
    .post({ true }, { "rotateLeft post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UByte.rotateRightLaw(bitCount: Int): UByte {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UInt.rotateRightLaw(bitCount: Int): UInt {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun ULong.rotateRightLaw(bitCount: Int): ULong {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@ExperimentalStdlibApi
@Law
public inline fun UShort.rotateRightLaw(bitCount: Int): UShort {
  pre(true) { "rotateRight pre-conditions" }
  return rotateRight(bitCount)
    .post({ true }, { "rotateRight post-conditions" })
}

@Law
public inline fun UByte.takeHighestOneBitLaw(): UByte {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun UInt.takeHighestOneBitLaw(): UInt {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun ULong.takeHighestOneBitLaw(): ULong {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun UShort.takeHighestOneBitLaw(): UShort {
  pre(true) { "takeHighestOneBit pre-conditions" }
  return takeHighestOneBit()
    .post({ true }, { "takeHighestOneBit post-conditions" })
}

@Law
public inline fun UByte.takeLowestOneBitLaw(): UByte {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun UInt.takeLowestOneBitLaw(): UInt {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun ULong.takeLowestOneBitLaw(): ULong {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun UShort.takeLowestOneBitLaw(): UShort {
  pre(true) { "takeLowestOneBit pre-conditions" }
  return takeLowestOneBit()
    .post({ true }, { "takeLowestOneBit post-conditions" })
}

@Law
public inline fun UShortArrayLaw(size: Int, init: (Int) -> UShort): UShortArray{
  pre(true) { "UShortArray pre-conditions" }
  return UShortArray(size, init)
    .post({ true }, { "UShortArray post-conditions" })
}

@Law
public inline fun ushortArrayOfLaw(vararg elements: UShort): UShortArray {
  pre(true) { "ushortArrayOf pre-conditions" }
  return ushortArrayOf(*elements)
    .post({ true }, { "ushortArrayOf post-conditions" })
}

@Law
public inline fun Byte.toUShortLaw(): UShort {
  pre(true) { "toUShort pre-conditions" }
  return toUShort()
    .post({ true }, { "toUShort post-conditions" })
}

@Law
public inline fun Int.toUShortLaw(): UShort {
  pre(true) { "toUShort pre-conditions" }
  return toUShort()
    .post({ true }, { "toUShort post-conditions" })
}

@Law
public inline fun Long.toUShortLaw(): UShort {
  pre(true) { "toUShort pre-conditions" }
  return toUShort()
    .post({ true }, { "toUShort post-conditions" })
}

@Law
public inline fun Short.toUShortLaw(): UShort {
  pre(true) { "toUShort pre-conditions" }
  return toUShort()
    .post({ true }, { "toUShort post-conditions" })
}

@Law
public inline fun <reified T> arrayOfLaw(vararg elements: T): Array<T> {
  pre(true) { "arrayOf pre-conditions" }
  return arrayOf(*elements)
    .post({ true }, { "arrayOf post-conditions" })
}

@Law
public inline fun <reified T> arrayOfNullsLaw(size: Int): Array<T?> {
  pre(true) { "arrayOfNulls pre-conditions" }
  return arrayOfNulls<T>(size)
    .post({ true }, { "arrayOfNulls post-conditions" })
}

@Law
public fun booleanArrayOfLaw(vararg elements: Boolean): BooleanArray {
  pre(true) { "booleanArrayOf pre-conditions" }
  return booleanArrayOf(*elements)
    .post({ true }, { "booleanArrayOf post-conditions" })
}

@Law
public fun byteArrayOfLaw(vararg elements: Byte): ByteArray {
  pre(true) { "byteArrayOf pre-conditions" }
  return byteArrayOf(*elements)
    .post({ true }, { "byteArrayOf post-conditions" })
}

@Law
public fun charArrayOfLaw(vararg elements: Char): CharArray {
  pre(true) { "charArrayOf pre-conditions" }
  return charArrayOf(*elements)
    .post({ true }, { "charArrayOf post-conditions" })
}

@Law
public fun doubleArrayOfLaw(vararg elements: Double): DoubleArray {
  pre(true) { "doubleArrayOf pre-conditions" }
  return doubleArrayOf(*elements)
    .post({ true }, { "doubleArrayOf post-conditions" })
}

@Law
public inline fun <reified T> emptyArrayLaw(): Array<T> {
  pre(true) { "emptyArray pre-conditions" }
  return emptyArray<T>()
    .post({ true }, { "emptyArray post-conditions" })
}

@Law
public inline fun <reified T : Enum<T>> enumValueOfLaw(name: String): T {
  pre(true) { "enumValueOf pre-conditions" }
  return enumValueOf<T>(name)
    .post({ true }, { "enumValueOf post-conditions" })
}

@Law
public inline fun <reified T : Enum<T>> enumValuesLaw(): Array<T> {
  pre(true) { "enumValues pre-conditions" }
  return enumValues<T>()
    .post({ true }, { "enumValues post-conditions" })
}

@Law
public fun floatArrayOfLaw(vararg elements: Float): FloatArray {
  pre(true) { "floatArrayOf pre-conditions" }
  return floatArrayOf(*elements)
    .post({ true }, { "floatArrayOf post-conditions" })
}

@Law
public fun intArrayOfLaw(vararg elements: Int): IntArray {
  pre(true) { "intArrayOf pre-conditions" }
  return intArrayOf(*elements)
    .post({ true }, { "intArrayOf post-conditions" })
}

@Law
public fun longArrayOfLaw(vararg elements: Long): LongArray {
  pre(true) { "longArrayOf pre-conditions" }
  return longArrayOf(*elements)
    .post({ true }, { "longArrayOf post-conditions" })
}

@Law
public fun shortArrayOfLaw(vararg elements: Short): ShortArray {
  pre(true) { "shortArrayOf pre-conditions" }
  return shortArrayOf(*elements)
    .post({ true }, { "shortArrayOf post-conditions" })
}

@Law
public fun String?.plusLaw(other: Any?): String {
  pre(true) { "plus pre-conditions" }
  return plus(other)
    .post({ true }, { "plus post-conditions" })
}

@Law
public fun Any?.toStringLaw(): String {
  pre(true) { "toString pre-conditions" }
  return toString()
    .post({ true }, { "toString post-conditions" })
}

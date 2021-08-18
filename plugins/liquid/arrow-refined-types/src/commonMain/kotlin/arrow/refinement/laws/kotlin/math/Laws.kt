package arrow.refinement.laws.kotlin.math

import arrow.refinement.Law
import arrow.refinement.pre
import arrow.refinement.post
import kotlin.jvm.JvmName
import kotlin.math.* // ktlint-disable no-wildcard-imports

@Law
@JvmName("absLawDoubleDouble")
public inline fun absLaw(x: Double): Double {
  pre(true) { "kotlin.math.abs pre-conditions" }
  return abs(x)
    .post({ true }, { "kotlin.math.abs post-conditions" })
}

@Law
@JvmName("absLawFloatFloat")
public inline fun absLaw(x: Float): Float {
  pre(true) { "kotlin.math.abs pre-conditions" }
  return abs(x)
    .post({ true }, { "kotlin.math.abs post-conditions" })
}

@Law
@JvmName("absLawIntInt")
public inline fun absLaw(n: Int): Int {
  pre(true) { "kotlin.math.abs pre-conditions" }
  return abs(n)
    .post({ true }, { "kotlin.math.abs post-conditions" })
}

@Law
@JvmName("absLawLongLong")
public inline fun absLaw(n: Long): Long {
  pre(true) { "kotlin.math.abs pre-conditions" }
  return abs(n)
    .post({ true }, { "kotlin.math.abs post-conditions" })
}

@Law
@JvmName("acosLawDoubleDouble")
public inline fun acosLaw(x: Double): Double {
  pre(true) { "kotlin.math.acos pre-conditions" }
  return acos(x)
    .post({ true }, { "kotlin.math.acos post-conditions" })
}

@Law
@JvmName("acosLawFloatFloat")
public inline fun acosLaw(x: Float): Float {
  pre(true) { "kotlin.math.acos pre-conditions" }
  return acos(x)
    .post({ true }, { "kotlin.math.acos post-conditions" })
}

@Law
@JvmName("acoshLawDoubleDouble")
public fun acoshLaw(x: Double): Double {
  pre(true) { "kotlin.math.acosh pre-conditions" }
  return acosh(x)
    .post({ true }, { "kotlin.math.acosh post-conditions" })
}

@Law
@JvmName("acoshLawFloatFloat")
public inline fun acoshLaw(x: Float): Float {
  pre(true) { "kotlin.math.acosh pre-conditions" }
  return acosh(x)
    .post({ true }, { "kotlin.math.acosh post-conditions" })
}

@Law
@JvmName("asinLawDoubleDouble")
public inline fun asinLaw(x: Double): Double {
  pre(true) { "kotlin.math.asin pre-conditions" }
  return asin(x)
    .post({ true }, { "kotlin.math.asin post-conditions" })
}

@Law
@JvmName("asinLawFloatFloat")
public inline fun asinLaw(x: Float): Float {
  pre(true) { "kotlin.math.asin pre-conditions" }
  return asin(x)
    .post({ true }, { "kotlin.math.asin post-conditions" })
}

@Law
@JvmName("asinhLawDoubleDouble")
public fun asinhLaw(x: Double): Double {
  pre(true) { "kotlin.math.asinh pre-conditions" }
  return asinh(x)
    .post({ true }, { "kotlin.math.asinh post-conditions" })
}

@Law
@JvmName("asinhLawFloatFloat")
public inline fun asinhLaw(x: Float): Float {
  pre(true) { "kotlin.math.asinh pre-conditions" }
  return asinh(x)
    .post({ true }, { "kotlin.math.asinh post-conditions" })
}

@Law
@JvmName("atanLawDoubleDouble")
public inline fun atanLaw(x: Double): Double {
  pre(true) { "kotlin.math.atan pre-conditions" }
  return atan(x)
    .post({ true }, { "kotlin.math.atan post-conditions" })
}

@Law
@JvmName("atanLawFloatFloat")
public inline fun atanLaw(x: Float): Float {
  pre(true) { "kotlin.math.atan pre-conditions" }
  return atan(x)
    .post({ true }, { "kotlin.math.atan post-conditions" })
}

@Law
@JvmName("atan2LawDoubleDoubleDouble")
public inline fun atan2Law(y: Double, x: Double): Double {
  pre(true) { "kotlin.math.atan2 pre-conditions" }
  return atan2(y, x)
    .post({ true }, { "kotlin.math.atan2 post-conditions" })
}

@Law
@JvmName("atan2LawFloatFloatFloat")
public inline fun atan2Law(y: Float, x: Float): Float {
  pre(true) { "kotlin.math.atan2 pre-conditions" }
  return atan2(y, x)
    .post({ true }, { "kotlin.math.atan2 post-conditions" })
}

@Law
@JvmName("atanhLawDoubleDouble")
public fun atanhLaw(x: Double): Double {
  pre(true) { "kotlin.math.atanh pre-conditions" }
  return atanh(x)
    .post({ true }, { "kotlin.math.atanh post-conditions" })
}

@Law
@JvmName("atanhLawFloatFloat")
public inline fun atanhLaw(x: Float): Float {
  pre(true) { "kotlin.math.atanh pre-conditions" }
  return atanh(x)
    .post({ true }, { "kotlin.math.atanh post-conditions" })
}

@Law
@JvmName("ceilLawDoubleDouble")
public inline fun ceilLaw(x: Double): Double {
  pre(true) { "kotlin.math.ceil pre-conditions" }
  return ceil(x)
    .post({ true }, { "kotlin.math.ceil post-conditions" })
}

@Law
@JvmName("ceilLawFloatFloat")
public inline fun ceilLaw(x: Float): Float {
  pre(true) { "kotlin.math.ceil pre-conditions" }
  return ceil(x)
    .post({ true }, { "kotlin.math.ceil post-conditions" })
}

@Law
@JvmName("cosLawDoubleDouble")
public inline fun cosLaw(x: Double): Double {
  pre(true) { "kotlin.math.cos pre-conditions" }
  return cos(x)
    .post({ true }, { "kotlin.math.cos post-conditions" })
}

@Law
@JvmName("cosLawFloatFloat")
public inline fun cosLaw(x: Float): Float {
  pre(true) { "kotlin.math.cos pre-conditions" }
  return cos(x)
    .post({ true }, { "kotlin.math.cos post-conditions" })
}

@Law
@JvmName("coshLawDoubleDouble")
public inline fun coshLaw(x: Double): Double {
  pre(true) { "kotlin.math.cosh pre-conditions" }
  return cosh(x)
    .post({ true }, { "kotlin.math.cosh post-conditions" })
}

@Law
@JvmName("coshLawFloatFloat")
public inline fun coshLaw(x: Float): Float {
  pre(true) { "kotlin.math.cosh pre-conditions" }
  return cosh(x)
    .post({ true }, { "kotlin.math.cosh post-conditions" })
}

@Law
@JvmName("expLawDoubleDouble")
public inline fun expLaw(x: Double): Double {
  pre(true) { "kotlin.math.exp pre-conditions" }
  return exp(x)
    .post({ true }, { "kotlin.math.exp post-conditions" })
}

@Law
@JvmName("expLawFloatFloat")
public inline fun expLaw(x: Float): Float {
  pre(true) { "kotlin.math.exp pre-conditions" }
  return exp(x)
    .post({ true }, { "kotlin.math.exp post-conditions" })
}

@Law
@JvmName("expm1LawDoubleDouble")
public inline fun expm1Law(x: Double): Double {
  pre(true) { "kotlin.math.expm1 pre-conditions" }
  return expm1(x)
    .post({ true }, { "kotlin.math.expm1 post-conditions" })
}

@Law
@JvmName("expm1LawFloatFloat")
public inline fun expm1Law(x: Float): Float {
  pre(true) { "kotlin.math.expm1 pre-conditions" }
  return expm1(x)
    .post({ true }, { "kotlin.math.expm1 post-conditions" })
}

@Law
@JvmName("floorLawDoubleDouble")
public inline fun floorLaw(x: Double): Double {
  pre(true) { "kotlin.math.floor pre-conditions" }
  return floor(x)
    .post({ true }, { "kotlin.math.floor post-conditions" })
}

@Law
@JvmName("floorLawFloatFloat")
public inline fun floorLaw(x: Float): Float {
  pre(true) { "kotlin.math.floor pre-conditions" }
  return floor(x)
    .post({ true }, { "kotlin.math.floor post-conditions" })
}

@Law
@JvmName("hypotLawDoubleDoubleDouble")
public inline fun hypotLaw(x: Double, y: Double): Double {
  pre(true) { "kotlin.math.hypot pre-conditions" }
  return hypot(x, y)
    .post({ true }, { "kotlin.math.hypot post-conditions" })
}

@Law
@JvmName("hypotLawFloatFloatFloat")
public inline fun hypotLaw(x: Float, y: Float): Float {
  pre(true) { "kotlin.math.hypot pre-conditions" }
  return hypot(x, y)
    .post({ true }, { "kotlin.math.hypot post-conditions" })
}

@Law
@JvmName("lnLawDoubleDouble")
public inline fun lnLaw(x: Double): Double {
  pre(true) { "kotlin.math.ln pre-conditions" }
  return ln(x)
    .post({ true }, { "kotlin.math.ln post-conditions" })
}

@Law
@JvmName("lnLawFloatFloat")
public inline fun lnLaw(x: Float): Float {
  pre(true) { "kotlin.math.ln pre-conditions" }
  return ln(x)
    .post({ true }, { "kotlin.math.ln post-conditions" })
}

@Law
@JvmName("ln1pLawDoubleDouble")
public inline fun ln1pLaw(x: Double): Double {
  pre(true) { "kotlin.math.ln1p pre-conditions" }
  return ln1p(x)
    .post({ true }, { "kotlin.math.ln1p post-conditions" })
}

@Law
@JvmName("ln1pLawFloatFloat")
public inline fun ln1pLaw(x: Float): Float {
  pre(true) { "kotlin.math.ln1p pre-conditions" }
  return ln1p(x)
    .post({ true }, { "kotlin.math.ln1p post-conditions" })
}

@Law
@JvmName("logLawDoubleDoubleDouble")
public fun logLaw(x: Double, base: Double): Double {
  pre(true) { "kotlin.math.log pre-conditions" }
  return log(x, base)
    .post({ true }, { "kotlin.math.log post-conditions" })
}

@Law
@JvmName("logLawFloatFloatFloat")
public fun logLaw(x: Float, base: Float): Float {
  pre(true) { "kotlin.math.log pre-conditions" }
  return log(x, base)
    .post({ true }, { "kotlin.math.log post-conditions" })
}

@Law
@JvmName("log10LawDoubleDouble")
public inline fun log10Law(x: Double): Double {
  pre(true) { "kotlin.math.log10 pre-conditions" }
  return log10(x)
    .post({ true }, { "kotlin.math.log10 post-conditions" })
}

@Law
@JvmName("log10LawFloatFloat")
public inline fun log10Law(x: Float): Float {
  pre(true) { "kotlin.math.log10 pre-conditions" }
  return log10(x)
    .post({ true }, { "kotlin.math.log10 post-conditions" })
}

@Law
@JvmName("log2LawDoubleDouble")
public fun log2Law(x: Double): Double {
  pre(true) { "kotlin.math.log2 pre-conditions" }
  return log2(x)
    .post({ true }, { "kotlin.math.log2 post-conditions" })
}

@Law
@JvmName("log2LawFloatFloat")
public fun log2Law(x: Float): Float {
  pre(true) { "kotlin.math.log2 pre-conditions" }
  return log2(x)
    .post({ true }, { "kotlin.math.log2 post-conditions" })
}

@Law
@JvmName("maxLawDoubleDoubleDouble")
public inline fun maxLaw(a: Double, b: Double): Double {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("maxLawFloatFloatFloat")
public inline fun maxLaw(a: Float, b: Float): Float {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("maxLawIntIntInt")
public inline fun maxLaw(a: Int, b: Int): Int {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("maxLawLongLongLong")
public inline fun maxLaw(a: Long, b: Long): Long {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("minLawDoubleDoubleDouble")
public inline fun minLaw(a: Double, b: Double): Double {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

@Law
@JvmName("minLawFloatFloatFloat")
public inline fun minLaw(a: Float, b: Float): Float {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

@Law
@JvmName("minLawIntIntInt")
public inline fun minLaw(a: Int, b: Int): Int {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

@Law
@JvmName("minLawLongLongLong")
public inline fun minLaw(a: Long, b: Long): Long {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

@Law
@JvmName("roundLawDoubleDouble")
public inline fun roundLaw(x: Double): Double {
  pre(true) { "kotlin.math.round pre-conditions" }
  return round(x)
    .post({ true }, { "kotlin.math.round post-conditions" })
}

@Law
@JvmName("roundLawFloatFloat")
public inline fun roundLaw(x: Float): Float {
  pre(true) { "kotlin.math.round pre-conditions" }
  return round(x)
    .post({ true }, { "kotlin.math.round post-conditions" })
}

@Law
@JvmName("signLawDoubleDouble")
public inline fun signLaw(x: Double): Double {
  pre(true) { "kotlin.math.sign pre-conditions" }
  return sign(x)
    .post({ true }, { "kotlin.math.sign post-conditions" })
}

@Law
@JvmName("signLawFloatFloat")
public inline fun signLaw(x: Float): Float {
  pre(true) { "kotlin.math.sign pre-conditions" }
  return sign(x)
    .post({ true }, { "kotlin.math.sign post-conditions" })
}

@Law
@JvmName("sinLawDoubleDouble")
public inline fun sinLaw(x: Double): Double {
  pre(true) { "kotlin.math.sin pre-conditions" }
  return sin(x)
    .post({ true }, { "kotlin.math.sin post-conditions" })
}

@Law
@JvmName("sinLawFloatFloat")
public inline fun sinLaw(x: Float): Float {
  pre(true) { "kotlin.math.sin pre-conditions" }
  return sin(x)
    .post({ true }, { "kotlin.math.sin post-conditions" })
}

@Law
@JvmName("sinhLawDoubleDouble")
public inline fun sinhLaw(x: Double): Double {
  pre(true) { "kotlin.math.sinh pre-conditions" }
  return sinh(x)
    .post({ true }, { "kotlin.math.sinh post-conditions" })
}

@Law
@JvmName("sinhLawFloatFloat")
public inline fun sinhLaw(x: Float): Float {
  pre(true) { "kotlin.math.sinh pre-conditions" }
  return sinh(x)
    .post({ true }, { "kotlin.math.sinh post-conditions" })
}

@Law
@JvmName("sqrtLawDoubleDouble")
public inline fun sqrtLaw(x: Double): Double {
  pre(true) { "kotlin.math.sqrt pre-conditions" }
  return sqrt(x)
    .post({ true }, { "kotlin.math.sqrt post-conditions" })
}

@Law
@JvmName("sqrtLawFloatFloat")
public inline fun sqrtLaw(x: Float): Float {
  pre(true) { "kotlin.math.sqrt pre-conditions" }
  return sqrt(x)
    .post({ true }, { "kotlin.math.sqrt post-conditions" })
}

@Law
@JvmName("tanLawDoubleDouble")
public inline fun tanLaw(x: Double): Double {
  pre(true) { "kotlin.math.tan pre-conditions" }
  return tan(x)
    .post({ true }, { "kotlin.math.tan post-conditions" })
}

@Law
@JvmName("tanLawFloatFloat")
public inline fun tanLaw(x: Float): Float {
  pre(true) { "kotlin.math.tan pre-conditions" }
  return tan(x)
    .post({ true }, { "kotlin.math.tan post-conditions" })
}

@Law
@JvmName("tanhLawDoubleDouble")
public inline fun tanhLaw(x: Double): Double {
  pre(true) { "kotlin.math.tanh pre-conditions" }
  return tanh(x)
    .post({ true }, { "kotlin.math.tanh post-conditions" })
}

@Law
@JvmName("tanhLawFloatFloat")
public inline fun tanhLaw(x: Float): Float {
  pre(true) { "kotlin.math.tanh pre-conditions" }
  return tanh(x)
    .post({ true }, { "kotlin.math.tanh post-conditions" })
}

@Law
@JvmName("truncateLawDoubleDouble")
public fun truncateLaw(x: Double): Double {
  pre(true) { "kotlin.math.truncate pre-conditions" }
  return truncate(x)
    .post({ true }, { "kotlin.math.truncate post-conditions" })
}

@Law
@JvmName("truncateLawFloatFloat")
public fun truncateLaw(x: Float): Float {
  pre(true) { "kotlin.math.truncate pre-conditions" }
  return truncate(x)
    .post({ true }, { "kotlin.math.truncate post-conditions" })
}

@Law
@JvmName("nextDownLawDouble")
public inline fun Double.nextDownLaw(): Double {
  pre(true) { "kotlin.math.nextDown pre-conditions" }
  return nextDown()
    .post({ true }, { "kotlin.math.nextDown post-conditions" })
}

@Law
@JvmName("nextTowardsLawDoubleDouble")
public inline fun Double.nextTowardsLaw(to: Double): Double {
  pre(true) { "kotlin.math.nextTowards pre-conditions" }
  return nextTowards(to)
    .post({ true }, { "kotlin.math.nextTowards post-conditions" })
}

@Law
@JvmName("nextUpLawDouble")
public inline fun Double.nextUpLaw(): Double {
  pre(true) { "kotlin.math.nextUp pre-conditions" }
  return nextUp()
    .post({ true }, { "kotlin.math.nextUp post-conditions" })
}

@Law
@JvmName("powLawDoubleDouble")
public inline fun Double.powLaw(x: Double): Double {
  pre(true) { "kotlin.math.pow pre-conditions" }
  return pow(x)
    .post({ true }, { "kotlin.math.pow post-conditions" })
}

@Law
@JvmName("powLawIntDouble")
public inline fun Double.powLaw(n: Int): Double {
  pre(true) { "kotlin.math.pow pre-conditions" }
  return pow(n)
    .post({ true }, { "kotlin.math.pow post-conditions" })
}

@Law
@JvmName("powLawFloatFloat")
public inline fun Float.powLaw(x: Float): Float {
  pre(true) { "kotlin.math.pow pre-conditions" }
  return pow(x)
    .post({ true }, { "kotlin.math.pow post-conditions" })
}

@Law
@JvmName("powLawIntFloat")
public inline fun Float.powLaw(n: Int): Float {
  pre(true) { "kotlin.math.pow pre-conditions" }
  return pow(n)
    .post({ true }, { "kotlin.math.pow post-conditions" })
}

@Law
@JvmName("roundToIntLawInt")
public fun Double.roundToIntLaw(): Int {
  pre(true) { "kotlin.math.roundToInt pre-conditions" }
  return roundToInt()
    .post({ true }, { "kotlin.math.roundToInt post-conditions" })
}

@Law
@JvmName("roundToIntLawInt")
public fun Float.roundToIntLaw(): Int {
  pre(true) { "kotlin.math.roundToInt pre-conditions" }
  return roundToInt()
    .post({ true }, { "kotlin.math.roundToInt post-conditions" })
}

@Law
@JvmName("roundToLongLawLong")
public fun Double.roundToLongLaw(): Long {
  pre(true) { "kotlin.math.roundToLong pre-conditions" }
  return roundToLong()
    .post({ true }, { "kotlin.math.roundToLong post-conditions" })
}

@Law
@JvmName("roundToLongLawLong")
public fun Float.roundToLongLaw(): Long {
  pre(true) { "kotlin.math.roundToLong pre-conditions" }
  return roundToLong()
    .post({ true }, { "kotlin.math.roundToLong post-conditions" })
}

@Law
@JvmName("withSignLawDoubleDouble")
public inline fun Double.withSignLaw(sign: Double): Double {
  pre(true) { "kotlin.math.withSign pre-conditions" }
  return withSign(sign)
    .post({ true }, { "kotlin.math.withSign post-conditions" })
}

@Law
@JvmName("withSignLawIntDouble")
public inline fun Double.withSignLaw(sign: Int): Double {
  pre(true) { "kotlin.math.withSign pre-conditions" }
  return withSign(sign)
    .post({ true }, { "kotlin.math.withSign post-conditions" })
}

@Law
@JvmName("withSignLawFloatFloat")
public inline fun Float.withSignLaw(sign: Float): Float {
  pre(true) { "kotlin.math.withSign pre-conditions" }
  return withSign(sign)
    .post({ true }, { "kotlin.math.withSign post-conditions" })
}

@Law
@JvmName("withSignLawIntFloat")
public inline fun Float.withSignLaw(sign: Int): Float {
  pre(true) { "kotlin.math.withSign pre-conditions" }
  return withSign(sign)
    .post({ true }, { "kotlin.math.withSign post-conditions" })
}

@Law
@JvmName("maxLawUIntUIntUInt")
public inline fun maxLaw(a: UInt, b: UInt): UInt {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("maxLawULongULongULong")
public inline fun maxLaw(a: ULong, b: ULong): ULong {
  pre(true) { "kotlin.math.max pre-conditions" }
  return max(a, b)
    .post({ true }, { "kotlin.math.max post-conditions" })
}

@Law
@JvmName("minLawUIntUIntUInt")
public inline fun minLaw(a: UInt, b: UInt): UInt {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

@Law
@JvmName("minLawULongULongULong")
public inline fun minLaw(a: ULong, b: ULong): ULong {
  pre(true) { "kotlin.math.min pre-conditions" }
  return min(a, b)
    .post({ true }, { "kotlin.math.min post-conditions" })
}

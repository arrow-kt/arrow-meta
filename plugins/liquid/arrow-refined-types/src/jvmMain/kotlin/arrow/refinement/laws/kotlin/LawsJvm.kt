package arrow.refinement.laws.kotlin

import arrow.refinement.Law
import arrow.refinement.post
import arrow.refinement.pre

@Law
public inline fun kotlin.Throwable.printStackTraceLaw(stream: java.io.PrintStream): kotlin.Unit  {
  pre(true) { "kotlin.printStackTrace pre-conditions" }
  return printStackTrace(stream)
    .post({ true }, { "kotlin.printStackTrace post-conditions" })
}

@Law
public inline fun kotlin.Throwable.printStackTraceLaw(writer: java.io.PrintWriter): kotlin.Unit  {
  pre(true) { "kotlin.printStackTrace pre-conditions" }
  return printStackTrace(writer)
    .post({ true }, { "kotlin.printStackTrace post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.decLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.dec pre-conditions" }
  return dec()
    .post({ true }, { "kotlin.dec post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.divLaw(other: java.math.BigDecimal): java.math.BigDecimal  {
  pre(true) { "kotlin.div pre-conditions" }
  return div(other)
    .post({ true }, { "kotlin.div post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.incLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.inc pre-conditions" }
  return inc()
    .post({ true }, { "kotlin.inc post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.minusLaw(other: java.math.BigDecimal): java.math.BigDecimal  {
  pre(true) { "kotlin.minus pre-conditions" }
  return minus(other)
    .post({ true }, { "kotlin.minus post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.plusLaw(other: java.math.BigDecimal): java.math.BigDecimal  {
  pre(true) { "kotlin.plus pre-conditions" }
  return plus(other)
    .post({ true }, { "kotlin.plus post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.remLaw(other: java.math.BigDecimal): java.math.BigDecimal  {
  pre(true) { "kotlin.rem pre-conditions" }
  return rem(other)
    .post({ true }, { "kotlin.rem post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.timesLaw(other: java.math.BigDecimal): java.math.BigDecimal  {
  pre(true) { "kotlin.times pre-conditions" }
  return times(other)
    .post({ true }, { "kotlin.times post-conditions" })
}

@Law
public inline fun kotlin.Double.toBigDecimalLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal()
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Double.toBigDecimalLaw(mathContext: java.math.MathContext): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal(mathContext)
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Float.toBigDecimalLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal()
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Float.toBigDecimalLaw(mathContext: java.math.MathContext): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal(mathContext)
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Int.toBigDecimalLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal()
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Int.toBigDecimalLaw(mathContext: java.math.MathContext): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal(mathContext)
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Long.toBigDecimalLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal()
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Long.toBigDecimalLaw(mathContext: java.math.MathContext): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal(mathContext)
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun java.math.BigDecimal.unaryMinusLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.unaryMinus pre-conditions" }
  return unaryMinus()
    .post({ true }, { "kotlin.unaryMinus post-conditions" })
}

@Law
public inline infix fun java.math.BigInteger.andLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.and pre-conditions" }
  return and(other)
    .post({ true }, { "kotlin.and post-conditions" })
}

@Law
public inline fun java.math.BigInteger.decLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.dec pre-conditions" }
  return dec()
    .post({ true }, { "kotlin.dec post-conditions" })
}

@Law
public inline fun java.math.BigInteger.divLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.div pre-conditions" }
  return div(other)
    .post({ true }, { "kotlin.div post-conditions" })
}

@Law
public inline fun java.math.BigInteger.incLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.inc pre-conditions" }
  return inc()
    .post({ true }, { "kotlin.inc post-conditions" })
}

@Law
public inline fun java.math.BigInteger.invLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.inv pre-conditions" }
  return inv()
    .post({ true }, { "kotlin.inv post-conditions" })
}

@Law
public inline fun java.math.BigInteger.minusLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.minus pre-conditions" }
  return minus(other)
    .post({ true }, { "kotlin.minus post-conditions" })
}

@Law
public inline infix fun java.math.BigInteger.orLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.or pre-conditions" }
  return or(other)
    .post({ true }, { "kotlin.or post-conditions" })
}

@Law
public inline fun java.math.BigInteger.plusLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.plus pre-conditions" }
  return plus(other)
    .post({ true }, { "kotlin.plus post-conditions" })
}

@Law
public inline fun java.math.BigInteger.remLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.rem pre-conditions" }
  return rem(other)
    .post({ true }, { "kotlin.rem post-conditions" })
}

@Law
public inline infix fun java.math.BigInteger.shlLaw(n: kotlin.Int): java.math.BigInteger  {
  pre(true) { "kotlin.shl pre-conditions" }
  return shl(n)
    .post({ true }, { "kotlin.shl post-conditions" })
}

@Law
public inline infix fun java.math.BigInteger.shrLaw(n: kotlin.Int): java.math.BigInteger  {
  pre(true) { "kotlin.shr pre-conditions" }
  return shr(n)
    .post({ true }, { "kotlin.shr post-conditions" })
}

@Law
public inline fun java.math.BigInteger.timesLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.times pre-conditions" }
  return times(other)
    .post({ true }, { "kotlin.times post-conditions" })
}

@Law
public inline fun java.math.BigInteger.toBigDecimalLaw(): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal()
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun java.math.BigInteger.toBigDecimalLaw(scale: kotlin.Int, mathContext: java.math.MathContext): java.math.BigDecimal  {
  pre(true) { "kotlin.toBigDecimal pre-conditions" }
  return toBigDecimal(scale, mathContext)
    .post({ true }, { "kotlin.toBigDecimal post-conditions" })
}

@Law
public inline fun kotlin.Int.toBigIntegerLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.toBigInteger pre-conditions" }
  return toBigInteger()
    .post({ true }, { "kotlin.toBigInteger post-conditions" })
}


@Law
public inline fun assertLaw(value: kotlin.Boolean): kotlin.Unit  {
  pre(true) { "kotlin.assert pre-conditions" }
  return assert(value)
    .post({ true }, { "kotlin.assert post-conditions" })
}

@Law
public inline fun <R> synchronizedLaw(lock: kotlin.Any, block: () -> R): R  {
  pre(true) { "kotlin.synchronized pre-conditions" }
  return synchronized(lock, block)
    .post({ true }, { "kotlin.synchronized post-conditions" })
}

@Law
public inline fun kotlin.Long.toBigIntegerLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.toBigInteger pre-conditions" }
  return toBigInteger()
    .post({ true }, { "kotlin.toBigInteger post-conditions" })
}

@Law
public inline fun java.math.BigInteger.unaryMinusLaw(): java.math.BigInteger  {
  pre(true) { "kotlin.unaryMinus pre-conditions" }
  return unaryMinus()
    .post({ true }, { "kotlin.unaryMinus post-conditions" })
}

@Law
public inline infix fun java.math.BigInteger.xorLaw(other: java.math.BigInteger): java.math.BigInteger  {
  pre(true) { "kotlin.xor pre-conditions" }
  return xor(other)
    .post({ true }, { "kotlin.xor post-conditions" })
}

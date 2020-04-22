package arrow.meta.smt.dsl.numeral

import org.sosy_lab.common.rationals.Rational
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager
import java.math.BigDecimal
import java.math.BigInteger

interface NumeralSyntax<A : NumeralFormula, B : NumeralFormula> {
  val numeral: NumeralFormulaManager<A, B>

  fun variables(vararg v: String): List<B> =
    v.toList().mapNotNull { numeral.makeVariable(it) }

  infix fun A.eq(other: A): BooleanFormula = numeral.equal(this, other)

  infix fun A.gte(other: A): BooleanFormula = numeral.greaterOrEquals(this, other)

  infix fun A.lte(other: A): BooleanFormula = numeral.lessOrEquals(this, other)

  infix fun A.gt(other: A): BooleanFormula = numeral.greaterThan(this, other)

  infix fun A.lt(other: A): BooleanFormula = numeral.lessThan(this, other)

  operator fun A.plus(other: A): B = numeral.add(this, other)

  operator fun A.unaryMinus(): B = numeral.negate(this)

  operator fun A.div(other: A): B = numeral.divide(this, other)

  operator fun A.minus(other: A): B = numeral.subtract(this, other)

  operator fun A.times(other: A): B = numeral.multiply(this, other)

  fun BigInteger.to(): B = numeral.makeNumber(this)

  fun BigDecimal.to(): B = numeral.makeNumber(this)

  fun Long.to(): B = numeral.makeNumber(this)

  fun Rational.to(): B = numeral.makeNumber(this)

  fun Double.to(): B = numeral.makeNumber(this)

  fun Int.to(): B = this.toBigInteger().to()

  fun sum(vararg a: A): B = numeral.sum(a.toList())

  fun floor(a: A): NumeralFormula.IntegerFormula = numeral.floor(a)

  fun distinct(vararg a: A): BooleanFormula = numeral.distinct(a.toMutableList())
}
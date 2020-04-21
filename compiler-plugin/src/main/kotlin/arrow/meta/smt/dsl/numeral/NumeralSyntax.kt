package arrow.meta.smt.dsl.numeral

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface NumeralSyntax<A : NumeralFormula, B : NumeralFormula> {
  val numeral: NumeralFormulaManager<A, B>

  fun variables(vararg v: String): List<B> =
    v.toList().mapNotNull { numeral.makeVariable(it) }

  infix fun A.eq(other: A): BooleanFormula = numeral.equal(this, other)

  infix fun A.gte(other: A): BooleanFormula = numeral.greaterOrEquals(this, other)

  infix fun A.lte(other: A): BooleanFormula = numeral.lessOrEquals(this, other)

  infix fun A.gt(other: A) = numeral.greaterThan(this,  other)

  infix fun A.lt(other: A) = numeral.lessThan(this,  other)

  operator fun A.plus(other: A): B = numeral.add(this, other)

  operator fun A.unaryMinus(): B = numeral.negate(this)

  operator fun A.div(other: A): B = numeral.divide(this, other)

  operator fun A.minus(other: A): B = numeral.subtract(this, other)

  operator fun A.times(other: A): B = numeral.multiply(this, other)

}
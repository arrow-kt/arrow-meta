package arrow.meta.smt.dsl.numeral

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface NumeralSyntax<A : NumeralFormula, B : NumeralFormula> {
  val numeral: NumeralFormulaManager<A, B>

  fun variables(vararg v: String): List<B> =
    v.toList().mapNotNull { numeral.makeVariable(it) }

  infix fun A.eq(other: A): BooleanFormula = numeral.equal(this, other)

  infix fun A.plus(other: A): B =  numeral.add(this, other)
}
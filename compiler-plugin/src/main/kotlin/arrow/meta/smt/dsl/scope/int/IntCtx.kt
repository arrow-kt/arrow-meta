package arrow.meta.smt.dsl.scope.int

import arrow.meta.smt.dsl.numeral.NumeralSyntax
import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface IntCtx : SmtScope, NumeralSyntax<Int, Int> {
  override val int: IntegerFormulaManager
  override val numeral: NumeralFormulaManager<Int, Int>
    get() = int

  operator fun Int.rem(other: Int): Int = int.modulo(this, other)
}

typealias Int = NumeralFormula.IntegerFormula
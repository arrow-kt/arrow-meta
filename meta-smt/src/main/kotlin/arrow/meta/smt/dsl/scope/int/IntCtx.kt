package arrow.meta.smt.dsl.scope.int

import arrow.meta.smt.dsl.scope.SmtScope
import arrow.meta.smt.dsl.numeral.NumeralSyntax
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface IntCtx : SmtScope, NumeralSyntax<Int, Int> {
  override val int: IntegerFormulaManager
  override val numeral: NumeralFormulaManager<Int, Int>
    get() = int
}

typealias Int = NumeralFormula.IntegerFormula
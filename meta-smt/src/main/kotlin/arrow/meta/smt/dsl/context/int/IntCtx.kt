package arrow.meta.smt.dsl.context.int

import arrow.meta.smt.dsl.context.SmtContext
import arrow.meta.smt.dsl.numeral.NumeralSyntax
import org.sosy_lab.java_smt.api.IntegerFormulaManager
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface IntCtx : SmtContext, NumeralSyntax<Int, Int> {
  override val int: IntegerFormulaManager
  override val numeral: NumeralFormulaManager<Int, Int>
    get() = int
}

typealias Int = NumeralFormula.IntegerFormula
package arrow.meta.smt.dsl.scope.rational

import arrow.meta.smt.dsl.numeral.NumeralSyntax
import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager
import org.sosy_lab.java_smt.api.RationalFormulaManager

interface RationalCtx : SmtScope, NumeralSyntax<NumeralFormula, Rational> {
  override val rational: RationalFormulaManager
  override val numeral: NumeralFormulaManager<NumeralFormula, Rational>
    get() = rational
}

typealias Rational = NumeralFormula.RationalFormula
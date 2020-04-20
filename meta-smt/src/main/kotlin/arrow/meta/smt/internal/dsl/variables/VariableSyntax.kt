package arrow.meta.smt.internal.dsl.variables

import org.sosy_lab.java_smt.api.NumeralFormula
import org.sosy_lab.java_smt.api.NumeralFormulaManager

interface VariableSyntax {
  fun <A : NumeralFormula, B : NumeralFormula>
    NumeralFormulaManager<A, B>.variables(list: List<String>): List<B> =
    list.mapNotNull { makeVariable(it) }


}
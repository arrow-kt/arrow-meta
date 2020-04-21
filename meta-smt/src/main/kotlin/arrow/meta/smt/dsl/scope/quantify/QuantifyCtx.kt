package arrow.meta.smt.dsl.scope.quantify

import arrow.meta.smt.dsl.quantify.QuantifySyntax
import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager

interface QuantifyCtx : SmtScope, QuantifySyntax {
  override val quantify: QuantifiedFormulaManager
}
package arrow.meta.smt.dsl.scope.float

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.FloatingPointFormula
import org.sosy_lab.java_smt.api.FloatingPointFormulaManager

interface FloatCtx : SmtScope {
  override val float: FloatingPointFormulaManager
}

typealias Float = FloatingPointFormula
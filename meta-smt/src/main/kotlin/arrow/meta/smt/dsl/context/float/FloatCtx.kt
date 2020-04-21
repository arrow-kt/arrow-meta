package arrow.meta.smt.dsl.context.float

import arrow.meta.smt.dsl.context.SmtContext
import org.sosy_lab.java_smt.api.FloatingPointFormula
import org.sosy_lab.java_smt.api.FloatingPointFormulaManager

interface FloatCtx : SmtContext {
  override val float: FloatingPointFormulaManager
}

typealias Float = FloatingPointFormula
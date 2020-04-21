package arrow.meta.smt.dsl.scope.vector

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.BitvectorFormulaManager

interface VectorCtx : SmtScope {
  override val vector: BitvectorFormulaManager
}
package arrow.meta.smt.dsl.scope.array

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.ArrayFormulaManager

interface ArrayCtx : SmtScope {
  override val array: ArrayFormulaManager
}
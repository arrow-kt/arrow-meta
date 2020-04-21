package arrow.meta.smt.dsl.scope.rational

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.RationalFormulaManager

interface RationalCtx : SmtScope {
  override val rational: RationalFormulaManager
}
package arrow.meta.smt.dsl.scope.slogic

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SLFormulaManager

interface SLogicCtx : SmtScope {
  override val sLogic: SLFormulaManager
}
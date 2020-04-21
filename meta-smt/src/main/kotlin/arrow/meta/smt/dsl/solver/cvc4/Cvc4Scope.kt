package arrow.meta.smt.dsl.solver.cvc4

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SolverContext

/**
 * TODO: populate
 */
interface Cvc4Scope : SmtScope {

  companion object {
    // default
    fun scope(ctx: SolverContext): Cvc4Scope =
      SmtScope.default(ctx) as Cvc4Scope
  }
}


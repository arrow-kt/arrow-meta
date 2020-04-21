package arrow.meta.smt.dsl.solver.z3

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SolverContext

/**
 * TODO: populate
 */
interface Z3Scope : SmtScope {
  companion object {
    // default
    fun scope(ctx: SolverContext): Z3Scope =
      SmtScope.default(ctx) as Z3Scope
  }
}


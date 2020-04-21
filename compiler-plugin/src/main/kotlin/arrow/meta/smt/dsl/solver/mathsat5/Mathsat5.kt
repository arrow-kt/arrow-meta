package arrow.meta.smt.dsl.solver.mathsat5

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SolverContext

/**
 * TODO: populate
 */
interface Mathsat5Scope : SmtScope {
  companion object {
    // default
    fun scope(ctx: SolverContext): Mathsat5Scope=
      SmtScope.default(ctx) as Mathsat5Scope
  }
}


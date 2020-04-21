package arrow.meta.smt.dsl.solver.boolector

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SolverContext

/**
 * TODO: populate
 */
interface BoolectorScope : SmtScope {

  companion object {
    // default
    fun scope(ctx: SolverContext): BoolectorScope =
      SmtScope.default(ctx) as BoolectorScope
  }
}


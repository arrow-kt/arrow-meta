package arrow.meta.smt.dsl.solver.smtinterpol

import arrow.meta.smt.dsl.scope.SmtScope
import org.sosy_lab.java_smt.api.SolverContext

/**
 * TODO: populate
 */
interface SmtInterpolScope : SmtScope {

  companion object {
    // default
    fun scope(ctx: SolverContext): SmtInterpolScope =
      SmtScope.default(ctx) as SmtInterpolScope
  }
}


package arrow.meta.smt.internal.registry

import arrow.meta.smt.dsl.solver.SolverAlgebra
import arrow.meta.smt.dsl.solver.boolector.BoolectorScope
import arrow.meta.smt.dsl.solver.cvc4.Cvc4Scope
import arrow.meta.smt.dsl.solver.mathsat5.Mathsat5Scope
import arrow.meta.smt.dsl.solver.princess.PrincessScope
import arrow.meta.smt.dsl.solver.smtinterpol.SmtInterpolScope
import arrow.meta.smt.dsl.solver.z3.Z3Scope
import org.sosy_lab.common.ShutdownManager
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.BasicLogManager
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.SolverContext

interface SmtInternalRegistry {
  val conf: Configuration
    get() = Configuration.defaultConfiguration()

  val log: LogManager
    get() = BasicLogManager.create(conf)

  val notifier: ShutdownNotifier
    get() = ShutdownManager.create().notifier

  /**
   * Entry point for Smt Solvers
   */
  fun resolve(conf: Configuration, log: LogManager, notifier: ShutdownNotifier): SolverAlgebra

  fun registerSolver() {
    fun SolverAlgebra.register(): Any? =
      when (this) {
        is SolverAlgebra.Princess<*> -> {
          println("Initialize SMT Solver: $name")
          smt(PrincessScope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
        is SolverAlgebra.Z3<*> -> {
          println("Initialize SMT Solver: $name")
          smt(Z3Scope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
        is SolverAlgebra.Cvc4<*> -> {
          println("Initialize SMT Solver: $name")
          smt(Cvc4Scope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
        is SolverAlgebra.Mathsat5<*> -> {
          println("Initialize SMT Solver: $name")
          smt(Mathsat5Scope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
        is SolverAlgebra.SmtInterpol<*> -> {
          println("Initialize SMT Solver: $name")
          smt(SmtInterpolScope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
        is SolverAlgebra.Boolector<*> -> {
          println("Initialize SMT Solver: $name")
          smt(BoolectorScope.scope(ctx(SolverContextFactory.Solvers.PRINCESS, conf, log, notifier)))
        }
      }
    resolve(conf, log, notifier).register()
  }

  fun ctx(
    solver: SolverContextFactory.Solvers,
    conf: Configuration,
    log: LogManager,
    notifier: ShutdownNotifier
  ): SolverContext =
    SolverContextFactory.createSolverContext(conf, log, notifier, solver)
}
package arrow.meta.smt.internal.registry

import arrow.meta.smt.MetaSmt
import arrow.meta.smt.dsl.context.SmtContext
import arrow.meta.smt.dsl.context.default
import arrow.meta.smt.dsl.solverscope.princess.princess
import org.sosy_lab.common.ShutdownManager
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.BasicLogManager
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.SolverContext
import test

fun ctx(
  solver: SolverContextFactory.Solvers,
  conf: Configuration = Configuration.defaultConfiguration(),
  log: LogManager = BasicLogManager.create(conf),
  notifier: ShutdownNotifier = ShutdownManager.create().notifier
): SolverContext =
  SolverContextFactory.createSolverContext(conf, log, notifier, solver)

inline fun <reified F : SmtContext, A> register(smt: MetaSmt<F, A>): A {
  println("Initialize SMT: ${smt.name}")
  val conf = Configuration.defaultConfiguration()
  val log = BasicLogManager.create(conf)
  val notify = ShutdownManager.create().notifier
  val ctx: SolverContext = ctx(smt.solver, conf, log, notify)

  val a = when (smt.solver) {
    SolverContextFactory.Solvers.MATHSAT5 -> default(ctx)
    SolverContextFactory.Solvers.SMTINTERPOL -> default(ctx)
    SolverContextFactory.Solvers.Z3 -> default(ctx)
    SolverContextFactory.Solvers.PRINCESS ->
      princess(ctx.formulaManager, ctx)
    SolverContextFactory.Solvers.BOOLECTOR -> default(ctx)
    SolverContextFactory.Solvers.CVC4 -> default(ctx)
  }

  //return smt.smt()
  TODO()
}


fun main() {
  println(ctx(SolverContextFactory.Solvers.Z3).version)
  //register(test)
}
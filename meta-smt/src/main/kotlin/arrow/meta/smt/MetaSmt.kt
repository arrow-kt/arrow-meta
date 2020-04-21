package arrow.meta.smt

import arrow.meta.smt.dsl.context.SmtContext
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.SolverContextFactory

data class MetaSmt<F : SmtContext, A>(
  val name: String,
  val solver: SolverContextFactory.Solvers,
  val smt: F.(LogManager, ShutdownNotifier) -> A
)
import arrow.meta.internal.smt.Managers
import arrow.meta.internal.smt.MetaSmt
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.SolverContext

val test =
  MetaSmt("HelloWorld", SolverContextFactory.Solvers.PRINCESS) { ctx: SolverContext, manager: Managers ->
    println("Heello WOrld")
  }

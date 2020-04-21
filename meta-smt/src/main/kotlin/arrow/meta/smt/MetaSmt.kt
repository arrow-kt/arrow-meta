package arrow.meta.smt

import arrow.meta.smt.dsl.SmtSyntax
import arrow.meta.smt.dsl.scope.int.Int
import arrow.meta.smt.dsl.solver.SolverAlgebra
import arrow.meta.smt.internal.registry.SmtInternalRegistry
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.api.SolverContext

class MetaSmt : SmtSyntax, SmtInternalRegistry {
  override fun resolve(conf: Configuration, log: LogManager, notifier: ShutdownNotifier): SolverAlgebra =
    Princess("Hello World") {
      val (a: Int, b: Int, c: Int) = variables("a", "b", "c")
      val plusAssoc = forAll(a, b) {
        a plus b eq (b plus a)
      }
      val prover = ctx.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)
      prover.addConstraint(plusAssoc)
      if (!prover.isUnsat) {
        println("Model assginments${prover.modelAssignments}")
        val bb = prover.model.toList()
        println(bb)
      } else {
        prover
        println("Unsolved Model")
      }
      prover.close()
    }
}

//val contraint = //bool.or(
//  int.equal(int.divide(a, int.makeNumber(0)), c)
// int.equal(int.add(a, c), int.multiply(int.makeNumber(2), b))

fun main() {
  //println(ctx(SolverContextFactory.Solvers.Z3).version)
  MetaSmt().registerSolver()
}
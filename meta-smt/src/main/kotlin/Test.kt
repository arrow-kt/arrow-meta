import arrow.meta.smt.MetaSmt
import arrow.meta.smt.dsl.context.int.Int
import arrow.meta.smt.dsl.solverscope.princess.PrincessScope
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.SolverContext

val test =
  MetaSmt<PrincessScope, Unit?>("HelloWorld", SolverContextFactory.Solvers.PRINCESS) { log, notify ->
    val (a: Int, b: Int, c: Int) = variables("a", "b", "c")
    val cj = forAll(a, b) {
      a plus b eq c
    }
    val contraint = //bool.or(
      int.equal(int.divide(a, int.makeNumber(0)), c)
    val prover = ctx.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)
    prover.addConstraint(cj)
    if (!prover.isUnsat) {
      val bb = prover.model.toList()
      println(bb)
    } else {
      println("Unsolved Model")
    }
    prover.close()
  }


// int.equal(int.add(a, c), int.multiply(int.makeNumber(2), b))
//)
import arrow.meta.smt.internal.Managers
import arrow.meta.smt.internal.MetaSmt
import org.sosy_lab.java_smt.SolverContextFactory
import org.sosy_lab.java_smt.api.SolverContext

val test =
  MetaSmt("HelloWorld", SolverContextFactory.Solvers.PRINCESS) { ctx: SolverContext, manager: Managers ->
    manager.int?.let { int ->
      val (a, b, c) = int.variables(listOf("a", "b", "c"))
      val contraint = manager.bool.or(
        int.equal(int.add(a, b), c),
        int.equal(int.add(a, c), int.multiply(int.makeNumber(2), b))
      )
      val prover = ctx.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)
      prover.addConstraint(contraint)
      if (!prover.isUnsat) {
        val aa = prover.model.evaluate(a)
      }
    }


    /*manager.int?.makeVariable("a")?.let { a ->
      manager.int?.makeVariable("b")?.let { b ->
        manager.int?.makeVariable("c")?.let { c ->
        }
      }
    }*/

    println("Heello WOrld")
  }

package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.utils.NameProvider
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val solver: Solver = Solver(),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE
  ),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf(),
  val names: NameProvider = NameProvider(),
  val allAddedConstraints: MutableList<BooleanFormula> = mutableListOf()
) {

  private var stage = Stage.Init

  fun currentStage(): Stage = stage

  fun collecting(): Unit {
    stage = Stage.CollectConstraints
  }

  fun collectionEnds(): Unit {
    stage = Stage.Prove
  }

  fun <A> bracket(f: () -> A): A {
    prover.push()
    val result = f()
    prover.pop()
    return result
  }

  fun isIn(that: Stage) = stage == that

  fun addConstraint(formula: BooleanFormula) {
    prover.addConstraint(formula)
    allAddedConstraints.add(formula)
  }

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}
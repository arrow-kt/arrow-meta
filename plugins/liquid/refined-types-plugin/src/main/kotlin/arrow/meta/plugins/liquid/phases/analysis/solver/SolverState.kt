package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.continuations.ContSeq
import arrow.meta.plugins.liquid.smt.fieldNames
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.substituteVariable
import arrow.meta.plugins.liquid.smt.utils.NameProvider
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val log: (String) -> Unit,
  val solver: Solver = Solver(log),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE
  ),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf(),
  val names: NameProvider = NameProvider(),
  val solverTrace: MutableList<String> = mutableListOf()
) {

  private var stage = Stage.Init

  fun currentStage(): Stage = stage

  fun collecting(): Unit {
    stage = Stage.CollectConstraints
  }

  fun collectionEnds(): Unit {
    stage = Stage.Prove
  }

  private var parseErrors = false

  fun signalParseErrors(): Unit {
    parseErrors = true
  }

  fun hadParseErrors(): Boolean = parseErrors

  inline fun <A> bracket(f: () -> A): A {
    solverTrace.add("PUSH")
    prover.push()
    val result = f()
    prover.pop()
    solverTrace.add("POP")
    return result
  }

  val continuationBracket: ContSeq<Unit> =
    ContSeq { bracket { yield(Unit) } }

  fun isIn(that: Stage) = stage == that

  fun addConstraint(formula: BooleanFormula) {
    prover.addConstraint(formula)
    solverTrace.add(formula.toString())
  }

  /**
   * Introduces the field names as constants.
   * Do not forget to call before starting the check.
   */
  fun introduceFieldNamesInSolver() {
    solver.formulae {
      callableConstraints.flatMap {
        val myself =
          if (it.descriptor.isField())
            setOf(it.descriptor.fqNameSafe.asString())
          else
            emptySet()
        myself + fieldNames(it.pre + it.post)
      }.toSet().forEachIndexed { fieldIndex, fieldName ->
        val constraint = solver.ints {
          equal(makeVariable(fieldName), makeNumber(fieldIndex.toLong()))
        }
        addConstraint(constraint)
      }
    }
  }

  fun introduceFieldAxiomsInSolver() {
    try {
      callableConstraints
        .filter { it.descriptor.isField() && it.pre.isEmpty() && it.post.size == 1 }
        .forEach {
          solver.quantified {
            solver.ints {
              val name = it.descriptor.fqNameSafe.asString()
              val x = solver.makeObjectVariable("x")
              val post = solver.substituteVariable(
                it.post[0],
                mapOf(RESULT_VAR_NAME to solver.field(name, x), "this" to x))
              addConstraint(forall(x, post))
            }
          }
        }
    } catch (e: UnsupportedOperationException) {
      // solver does not support quantified formulae
      // we just get worst reasoning
    }
  }

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}

package arrow.meta.plugins.liquid.phases.analysis.solver

import arrow.meta.continuations.ContSeq
import arrow.meta.plugins.liquid.smt.Solver
import arrow.meta.plugins.liquid.smt.fieldNames
import arrow.meta.plugins.liquid.smt.utils.NameProvider
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val names: NameProvider = NameProvider(),
  val solver: Solver = Solver(names),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE
  ),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf(),
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

  /**
   * This signals that the rest of the computation
   * happens inside a push/pop bracket
   */
  val continuationBracket: ContSeq<Unit> =
    ContSeq { bracket { yield(Unit) } }

  fun isIn(that: Stage) = stage == that

  fun addConstraint(constraint: NamedConstraint) {
    prover.addConstraint(constraint.formula)
    solverTrace.add("${constraint.msg} : ${constraint.formula}")
  }

  /**
   * Introduces the field names as constants.
   * Do not forget to call before starting the check.
   */
  fun introduceFieldNamesInSolver() {
    solver.formulae {
      callableConstraints.flatMap { decl ->
        val descriptor = decl.descriptor
        val myself = if (descriptor.isField()) setOf(descriptor.fqNameSafe.asString()) else emptySet()
        myself + fieldNames((decl.pre + decl.post).map { it.formula }).map { it.first }
      }.toSet().forEachIndexed { fieldIndex, fieldName ->
        val constraint = solver.ints {
          NamedConstraint("[auto-generated] $fieldName == $fieldIndex", equal(makeVariable(fieldName), makeNumber(fieldIndex.toLong())))
        }
        addConstraint(constraint)
      }
    }
  }

  /* This will be useful if we manager to use a solver
     with support for quantification, such as Z3 */
  /*
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
  } */

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}

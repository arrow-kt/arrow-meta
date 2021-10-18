package arrow.meta.plugins.analysis.phases.analysis.solver.state

import arrow.meta.continuations.ContSeq
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.DeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.typeInvariants
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.utils.FieldProvider
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import arrow.meta.plugins.analysis.smt.utils.ReferencedElement
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val names: NameProvider = NameProvider(),
  val solver: Solver = Solver(names),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE
  ),
  val callableConstraints: MutableMap<FqName, MutableList<DeclarationConstraints>> = mutableMapOf(),
  val solverTrace: MutableList<String> = mutableListOf(),
  val fieldProvider: FieldProvider = FieldProvider(solver, prover)
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

  /**
   * This signals that part of the computation
   * happens inside a push/pop bracket
   */
  fun <A> scopedBracket(cont: () -> ContSeq<A>) =
    ContSeq.unit.map {
      solverTrace.add("PUSH (scoped)")
      prover.push()
    }.flatMap {
      cont()
    }.onEach {
      prover.pop()
      solverTrace.add("POP (scoped)")
    }

  fun isIn(that: Stage) = stage == that

  fun addConstraint(constraint: NamedConstraint) {
    prover.addConstraint(constraint.formula)
    solverTrace.add("${constraint.msg} : ${constraint.formula}")
  }

  fun addConstraintWithoutTrace(constraint: NamedConstraint) {
    prover.addConstraint(constraint.formula)
  }

  fun newName(
    context: ResolutionContext,
    prefix: String,
    element: Element?
  ): String = newName(context, prefix, element, null)

  fun newName(
    context: ResolutionContext,
    prefix: String,
    element: Element?,
    reference: Pair<ValueParameterDescriptor, ResolvedValueArgument>?
  ): String {
    val type = (element as? Expression)?.type(context)
    val info = element?.let { ReferencedElement(it, reference, type) }
    val newName = names.recordNewName(prefix, info)
    if (type != null && !type.isNullable()) {
      typeInvariants(context, type, newName).forEach { addConstraint(it) }
    }
    return newName
  }

  fun field(field: DeclarationDescriptor, formula: ObjectFormula): ObjectFormula {
    fieldProvider.introduce(field)
    return solver.field(field.fqNameSafe.name, formula)
  }

  companion object {
    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}

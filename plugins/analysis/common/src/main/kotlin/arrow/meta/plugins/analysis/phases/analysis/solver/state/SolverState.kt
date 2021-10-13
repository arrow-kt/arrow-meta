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
import arrow.meta.plugins.analysis.phases.analysis.solver.isField
import arrow.meta.plugins.analysis.phases.analysis.solver.overriddenDescriptors
import arrow.meta.plugins.analysis.smt.Solver
import arrow.meta.plugins.analysis.smt.fieldNames
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import arrow.meta.plugins.analysis.smt.utils.ReferencedElement
import org.sosy_lab.java_smt.api.Formula
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

  /**
   * Introduces the field names as constants.
   * Do not forget to call before starting the check.
   */
  fun introduceFieldNamesInSolver() {
    val basicNames = mutableSetOf<String>()
    val overriddenNames = mutableMapOf<String, String>()

    fun doOne(descriptor: DeclarationDescriptor) {
      val name = descriptor.fqNameSafe.name
      val overridden = descriptor.overriddenDescriptors()
      if (overridden.isNullOrEmpty()) {
        basicNames.add(name)
      } else {
        overridden.forEach(::doOne)
        val topOverriden = overridden.filter {
          it.overriddenDescriptors().isNullOrEmpty()
        }
        when (topOverriden.size) {
          0 -> basicNames.add(name)
          1 -> overriddenNames[name] = topOverriden.get(0).fqNameSafe.name
          // weird case, we have more than one "top most" elements
          // in this case, we just override *none*
          else -> basicNames.add(name)
        }
      }
    }

    callableConstraints.forEach { (_, decls) ->
      decls.forEach { decl ->
        val descriptor = decl.descriptor
        // add this field and its parents
        if (descriptor.isField()) doOne(descriptor)
        // add any other fields which may be mentioned here
        val cstrs: Iterable<Formula> = (decl.pre + decl.post).map { it.formula }
        basicNames.addAll(solver.formulaManager.fieldNames(cstrs).map { it.first })
      }
    }

    (basicNames - overriddenNames.keys).forEachIndexed { fieldIndex, fieldName ->
      val constraint = solver.ints {
        NamedConstraint("[auto-generated] $fieldName == $fieldIndex", equal(makeVariable(fieldName), makeNumber(fieldIndex.toLong())))
      }
      addConstraintWithoutTrace(constraint)
    }

    overriddenNames.forEach { (thisField, parentField) ->
      val constraint = solver.ints {
        NamedConstraint("[auto-generated] $thisField == $parentField", equal(makeVariable(thisField), makeVariable(parentField)))
      }
      addConstraintWithoutTrace(constraint)
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

package arrow.meta.plugins.liquid.phases.solver.state

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.errors.MetaErrors.*
import arrow.meta.plugins.liquid.phases.solver.Solver
import arrow.meta.plugins.liquid.phases.solver.collector.*
import arrow.meta.plugins.liquid.phases.solver.collector.constraints
import arrow.meta.plugins.liquid.phases.solver.collector.postCall
import arrow.meta.plugins.liquid.phases.solver.collector.preCall
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.*
import java.util.concurrent.atomic.AtomicReference

data class SolverState(
  val solver: Solver = Solver(),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf(),
  val names: NameProvider = NameProvider()
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

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
  }
}

class NameProvider {
  private val counter = AtomicReference(0)

  fun newName(prefix: String): String {
    val n = counter.getAndUpdate { it + 1 }
    return "${prefix}${n}"
  }
}

internal fun Meta.solverStateAnalysis(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          ensureSolverStateInitialization(module)
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          finalizeConstraintsCollection(module, bindingTrace)
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        collectDeclarationsConstraints(context, declaration, descriptor)
      },
      declarationChecker { declaration, descriptor, context ->
        checkDeclarationConstraints(context, declaration, descriptor)
      }
    )
  )

internal fun SolverState.constraintsFromSolverState(resolvedCall: ResolvedCall<*>): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    resolvedCall.resultingDescriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

internal fun SolverState.constraintsFromSolverState(descriptor: DeclarationDescriptor): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    descriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

private fun CompilerContext.collectDeclarationsConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  if (solverState != null && (solverState.isIn(SolverState.Stage.Init) || solverState.isIn(SolverState.Stage.CollectConstraints))) {
    solverState.collecting()
    val solver = solverState.solver
    val constraints = declaration.constraints(solver, context.trace.bindingContext)
    if (constraints.isNotEmpty()) {
      val preConstraints = arrayListOf<BooleanFormula>()
      val postConstraints = arrayListOf<BooleanFormula>()
      constraints.forEach { (call, formula) ->
        if (call.preCall()) preConstraints.add(formula)
        if (call.postCall()) postConstraints.add(formula)
      }
      solverState.callableConstraints.add(
        DeclarationConstraints(descriptor, declaration, preConstraints, postConstraints)
      )
    }
  }
}

private fun CompilerContext.finalizeConstraintsCollection(
  module: ModuleDescriptor,
  bindingTrace: BindingTrace
): AnalysisResult? {
  val solverState = get<SolverState>(SolverState.key(module))
  return if (solverState != null && solverState.isIn(SolverState.Stage.CollectConstraints)) {
    solverState.collectionEnds()
    AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), emptyList())
  } else null
}

private fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState()
    set(SolverState.key(module), state)
  }
}

private fun CompilerContext.checkDeclarationConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  val constraints = solverState?.constraintsFromSolverState(descriptor)
  if (solverState != null && solverState.isIn(SolverState.Stage.Prove)) {
    if (declaration is KtFunction) {
      solverState.bracket {
        // assert preconditions (if available)
        val inconsistentPreconditions = constraints?.pre?.let {
          solverState.addAndCheckConsistency(it) { unsatCore ->
            context.trace.report(
              InconsistentBodyPre.on(declaration.psiOrParent, declaration, unsatCore.filterIsInstance<Formula>()))
          }
        } ?: false  // if there are no preconditions, they are consistent
        // if we are inconsistent, there's no point in going on, just stop early
        if (inconsistentPreconditions) return@bracket
        // go check the body
        val resultName = solverState.names.newName("result")
        // TODO: rename the result
        // TODO: remove the pre- and post-conditions
        solverState.checkExpressionConstraints(resultName, declaration.body(), context)
        // check the post-conditions
        constraints?.post?.forEach { postCondition ->
          solverState.checkImplicationOf(postCondition) {
            context.trace.report(
              UnsatBodyPost.on(declaration.psiOrParent, declaration, listOf(postCondition)))
          }
        }
      }
    }
  }
}

private fun SolverState.checkExpressionConstraints(
  associatedVarName: String,
  expression: KtExpression?,
  context: DeclarationCheckerContext
) {
  when (expression) {
    is KtCallExpression ->
      checkCallExpression(associatedVarName, expression, context)
    is KtBlockExpression -> expression.statements.forEach {
      checkExpressionConstraints(associatedVarName, it, context)
    }
    is KtConstantExpression ->
      checkConstantExpression(associatedVarName, expression, context)
    else -> expression?.getChildrenOfType<KtExpression>()?.forEach {
      checkExpressionConstraints(associatedVarName, it, context)
    }
  }
}

private fun SolverState.checkCallExpression(
  associatedVarName: String,
  expression: KtCallExpression,
  context: DeclarationCheckerContext
) {
  expression.getResolvedCall(context.trace.bindingContext).let { resolvedCall ->
    // recursively perform check on arguments
    // including extension receiver and dispatch receiver
    //
    // [NOTE: argument renaming]
    //   this function creates a new name for each argument,
    //   based on the formal parameter name;
    //   this creates a renaming for the original constraints
    val argVars = resolvedCall?.allArgumentExpressions()?.associate { (name, ty, expr) ->
      val argUniqueName = names.newName(name)
      checkExpressionConstraints(argUniqueName, expr, context)
      Pair(name, argUniqueName)
    } ?: emptyMap()
    // obtain and rename the pre- and post-conditions
    // TODO: rename the result variable using the associatedVar
    val callConstraints = resolvedCall?.let { constraintsFromSolverState(it) }?.let {
      solver.renameDeclarationConstraints(it, argVars)
    }
    // check pre-conditions
    callConstraints?.pre?.forEach { callPreCondition ->
      checkImplicationOf(callPreCondition) {
        context.trace.report(
          UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition))
        )
      }
    }
    // assert post-conditions (inconsistent means unreachable code)
    callConstraints?.post?.let {
      addAndCheckConsistency(it) { unsatCore ->
        context.trace.report(
          InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore.filterIsInstance<Formula>())
        )
      }
    }
  }
}

// this function makes the desired variable name
// equal to the value encoded in the constant
private fun SolverState.checkConstantExpression(
  associatedVarName: String,
  expression: KtConstantExpression,
  context: DeclarationCheckerContext
) {
  solver.formulae {
    val mayBoolean  = expression.text.toBooleanStrictOrNull()
    val mayInteger  = expression.text.toBigIntegerOrNull()
    val mayRational = expression.text.toBigDecimalOrNull()
    when {
      mayBoolean == true ->
        makeVariable(FormulaType.BooleanType, associatedVarName)
      mayBoolean == false ->
        solver.booleans { not(makeVariable(FormulaType.BooleanType, associatedVarName)) }
      mayInteger != null ->
        solver.ints {
          equal(
            makeVariable(FormulaType.IntegerType, associatedVarName),
            makeNumber(mayInteger)
          )
        }
      mayRational != null ->
        solver.rationals {
          equal(
            makeVariable(FormulaType.RationalType, associatedVarName),
            makeNumber(mayRational)
          )
        }
      else -> null
    }?.let {
      prover.addConstraint(it)
    }
  }
}

// SOLVER INTERACTION
// these two functions ultimately call the SMT solver,
// and report errors as desired

private fun SolverState.addAndCheckConsistency(
  constraints: Iterable<BooleanFormula>,
  message: (unsatCore: List<BooleanFormula>) -> Unit
): Boolean {
  constraints.forEach { prover.addConstraint(it) }
  val unsat = prover.isUnsat
  if (unsat) { message(prover.unsatCore) }
  return unsat
}

private fun SolverState.checkImplicationOf(
  constraint: BooleanFormula,
  message: (model: Model) -> Unit
): Boolean =
  bracket {
    solver.booleans { prover.addConstraint(not(constraint)) }
    val unsat = prover.isUnsat
    if (!unsat) { message(prover.model) }
    !unsat
  }

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
      /* callChecker { resolvedCall, reportOn, context ->
        proveInCallChecker(context, resolvedCall, reportOn)
      }, */
      declarationChecker { declaration, descriptor, context ->
        checkDeclarationConstraints(context, declaration, descriptor)
      }
    )
  )

/*
private fun CompilerContext.proveInCallChecker(
  context: CallCheckerContext,
  resolvedCall: ResolvedCall<*>,
  reportOn: PsiElement
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  if (solverState != null && (solverState.currentStage() == SolverState.Stage.Prove)) {
    val callConstraints = solverState.constraintsFromSolverState(resolvedCall)
    if (callConstraints?.pre?.isNotEmpty() == true) {
      println("prove pre conditions for ${resolvedCall.call.callElement.text}")
      val formula = solverState.resolvedCallProveFormula(resolvedCall)
      if (formula != null) {
        solverState.prover.addConstraint(formula)
        if (!solverState.prover.isUnsat) {
          println(solverState.prover.model)
        } else {
          context.trace.report(UnsatCall.on(reportOn, resolvedCall, solverState.prover.unsatCore.filterIsInstance<Formula>()))
        }
      }
    }

    if (callConstraints?.post?.isNotEmpty() == true) {
      println("prove post conditions for ${resolvedCall.call.callElement.text}")
    }

    //TODO here we can push and pop solving context as tree of calls is traversed

  }
  println("callChecker: $resolvedCall, $reportOn, $context")
}
*/

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
    if (this is KtFunction) {
      solverState.bracket {
        // assert preconditions (if available)
        constraints?.pre?.let {
          solverState.addAndCheckConsistency(it) { unsatCore ->
            context.trace.report(
              InconsistentBodyPre.on(psiOrParent, this, unsatCore.filterIsInstance<Formula>()))
          }
        }
        // go check the body
        // TODO: remove the pre- and post-conditions
        solverState.checkExpressionConstraints(body(), context)
        // check the post-conditions
        constraints?.post?.forEach { postCondition ->
          solverState.checkImplicationOf(postCondition) {
            context.trace.report(
              UnsatBodyPost.on(psiOrParent, this, listOf(postCondition)))
          }
        }
      }
    }
  }
}

private fun SolverState.checkExpressionConstraints(
  expression: KtExpression?,
  context: DeclarationCheckerContext
) {
  when (expression) {
    is KtCallExpression -> expression.getResolvedCall(context.trace.bindingContext).let { resolvedCall ->
      // recursively perform check on arguments
      // including extension receiver and dispatch receiver
      resolvedCall?.allArgumentExpressions()?.forEach { (name, ty, expr) ->
        checkExpressionConstraints(expr, context)
      }
      // perform the actual check
      val callConstraints = resolvedCall?.let { constraintsFromSolverState(it) }
      // check preconditions
      callConstraints?.pre?.forEach { callPreCondition ->
        checkImplicationOf(callPreCondition) {
          context.trace.report(
            UnsatCallPre.on(expression.psiOrParent, resolvedCall, listOf(callPreCondition)))
        }
      }
      // assert postconditions, inconsistent means unreachable code
      callConstraints?.post?.let {
        addAndCheckConsistency(it) { unsatCore ->
          context.trace.report(
            InconsistentCallPost.on(expression.psiOrParent, resolvedCall, unsatCore.filterIsInstance<Formula>()))
        }
      }
    }
    is KtBlockExpression -> expression.statements.forEach {
      checkExpressionConstraints(it, context)
    }
    else -> return
  }
}

private fun SolverState.addAndCheckConsistency(
  constraints: Iterable<BooleanFormula>,
  message: (unsatCore: List<BooleanFormula>) -> Unit
) {
  constraints.forEach { prover.addConstraint(it) }
  if (prover.isUnsat) { message(prover.unsatCore) }
}

private fun SolverState.checkImplicationOf(
  constraint: BooleanFormula,
  message: (model: Model) -> Unit
) {
  bracket {
    solver.booleans {
      prover.addConstraint(not(constraint))
    }
    if (!prover.isUnsat) { message(prover.model) }
  }
}
package arrow.meta.plugins.liquid.phases.solver.state

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.liquid.errors.MetaErrors.UnsatCall
import arrow.meta.plugins.liquid.phases.solver.Solver
import arrow.meta.plugins.liquid.phases.solver.collector.DeclarationConstraints
import arrow.meta.plugins.liquid.phases.solver.collector.constraints
import arrow.meta.plugins.liquid.phases.solver.collector.postCall
import arrow.meta.plugins.liquid.phases.solver.collector.preCall
import arrow.meta.plugins.liquid.phases.solver.prover.resolvedCallProveFormula
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.ProverEnvironment
import org.sosy_lab.java_smt.api.SolverContext

data class SolverState(
  val solver: Solver = Solver(),
  val prover: ProverEnvironment = solver.newProverEnvironment(
    SolverContext.ProverOptions.GENERATE_MODELS,
    SolverContext.ProverOptions.GENERATE_UNSAT_CORE),
  val callableConstraints: MutableList<DeclarationConstraints> = mutableListOf()
) {

  private var stage = Stage.Init

  fun currentStage(): Stage = stage

  fun collecting(): Unit {
    stage = Stage.CollectConstraints
  }

  fun collectionEnds(): Unit {
    stage = Stage.Prove
  }

  companion object {

    fun key(moduleDescriptor: ModuleDescriptor): String =
      "SolverState-${moduleDescriptor.name}"
  }

  enum class Stage {
    Init, CollectConstraints, Prove
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
      callChecker { resolvedCall, reportOn, context ->
        proveInCallChecker(context, resolvedCall, reportOn)
      }
    )
  )

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

internal fun SolverState.constraintsFromSolverState(resolvedCall: ResolvedCall<*>): DeclarationConstraints? =
  callableConstraints.firstOrNull {
    resolvedCall.resultingDescriptor.fqNameSafe == it.descriptor.fqNameSafe
  }

private fun CompilerContext.collectDeclarationsConstraints(
  context: DeclarationCheckerContext,
  declaration: KtDeclaration,
  descriptor: DeclarationDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(context.moduleDescriptor))
  if (solverState != null && (solverState.currentStage() == SolverState.Stage.Init || solverState.currentStage() == SolverState.Stage.CollectConstraints)) {
    solverState.collecting()
    val solver = solverState.solver
    val constraints = declaration.constraints(solver, context.trace.bindingContext)
    if (constraints.isNotEmpty()) {
      val preConstraints = arrayListOf<Formula>()
      val postConstraints = arrayListOf<Formula>()
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
  return if (solverState != null && solverState.currentStage() == SolverState.Stage.CollectConstraints) {
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
package arrow.meta.plugins.analysis.phases

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnalysisResult
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.check.checkDeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectDeclarationsConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.finalizeConstraintsCollection
import arrow.meta.plugins.analysis.phases.ir.annotateWithConstraints
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import org.jetbrains.kotlin.psi.KtDeclaration

internal fun Meta.analysisPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          val kotlinModule: KotlinModuleDescriptor = module.model()
          ensureSolverStateInitialization(kotlinModule)
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val kotlinModule: KotlinModuleDescriptor = module.model()
          val context = KotlinResolutionContext(bindingTrace, module)
          when (finalizeConstraintsCollection(kotlinModule, context)) {
            AnalysisResult.Retry ->
              org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(
                bindingTrace.bindingContext, module, emptyList(), emptyList()
              )
            AnalysisResult.ParsingError ->
              org.jetbrains.kotlin.analyzer.AnalysisResult.compilationError(
                bindingTrace.bindingContext
              )
            AnalysisResult.Completed -> null
          }
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
        (declaration.model<KtDeclaration, Declaration>() as? Declaration)?.let {
          collectDeclarationsConstraints(kotlinContext, it, descriptor.model())
        }
      },
      declarationChecker { declaration, descriptor, context ->
        val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
        (declaration.model<KtDeclaration, Declaration>() as? Declaration)?.let {
          checkDeclarationConstraints(kotlinContext, it, descriptor.model())
        }
      },
      irFunction { fn ->
        annotateWithConstraints(fn)
        null
      },
      irDumpKotlinLike()
    )
  )

internal fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState(NameProvider())
    set(SolverState.key(module), state)
  }
}

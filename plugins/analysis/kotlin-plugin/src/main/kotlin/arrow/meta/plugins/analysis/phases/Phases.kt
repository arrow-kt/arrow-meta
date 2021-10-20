package arrow.meta.plugins.analysis.phases

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.getOrCreateBaseDirectory
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnalysisResult
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.check.checkDeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromDSL
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromClasspath
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.ir.HintState
import arrow.meta.plugins.analysis.phases.ir.annotateWithConstraints
import arrow.meta.plugins.analysis.phases.ir.hintsFile
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

internal fun Meta.analysisPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { _, module, _, _, bindingTrace, _ ->
          val kotlinModule: KotlinModuleDescriptor = module.model()
          initialize(kotlinModule)
          when (get<HintState>(Keys.hints(kotlinModule))) {
            HintState.NeedsProcessing -> {
              setHintsAs(kotlinModule, HintState.Processed)
              val path = getOrCreateBaseDirectory(null)
              org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), listOfNotNull(path))
            }
            else -> null
          }
        },
        analysisCompleted = { _, module, bindingTrace, files ->
          if (isInStage(module, Stage.CollectConstraints)) {
            val kotlinModule: KotlinModuleDescriptor = module.model()
            val context = KotlinResolutionContext(bindingTrace, module)
            val solverState = solverState(module)
            if (solverState != null) {
              val locals = files.declarationDescriptors(context)
              val (result, interesting) =
                solverState.collectConstraintsFromClasspath(locals, kotlinModule, context)
              setStageAs(module, Stage.Prove) // we end the CollectConstraints phase
              when (result) {
                AnalysisResult.Retry -> {
                  // 1. generate the additional file with hints
                  val path = getOrCreateBaseDirectory(null)
                  hintsFile(path.absolutePath, module, interesting)
                  setHintsAs(kotlinModule, HintState.NeedsProcessing)
                  // 2. retry with all the gathered information
                  org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(
                    bindingTrace.bindingContext, module,
                    emptyList(),
                    listOf(path.absoluteFile)
                  )
                }
                AnalysisResult.ParsingError ->
                  org.jetbrains.kotlin.analyzer.AnalysisResult.compilationError(
                    bindingTrace.bindingContext
                  )
                AnalysisResult.Completed -> null
              }
            } else null
          } else null
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        if (isInStage(context.moduleDescriptor, Stage.Init) || isInStage(context.moduleDescriptor, Stage.CollectConstraints)) {
          setStageAs(context.moduleDescriptor, Stage.CollectConstraints)
          val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
          val decl = declaration.model<KtDeclaration, Declaration>() as? Declaration
          val solverState = solverState(context)
          if (decl != null && solverState != null) {
            decl.collectConstraintsFromDSL(solverState, kotlinContext, descriptor.model())
          }
        }
      },
      declarationChecker { declaration, descriptor, context ->
        if (isInStage(context.moduleDescriptor, Stage.Prove)) {
          val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
          val decl = declaration.model<KtDeclaration, Declaration>() as? Declaration
          val solverState = solverState(context)
          if (decl != null && solverState != null && !solverState.hadParseErrors()) {
            solverState.checkDeclarationConstraints(kotlinContext, decl, descriptor.model())
          }
        }
      },
      irFunction { fn ->
        compilerContext.solverState(moduleFragment.descriptor)?.let {
          annotateWithConstraints(it, fn)
        }
        null
      },
      irDumpKotlinLike()
    )
  )

enum class Stage {
  Init, CollectConstraints, Prove
}

object Keys {
  fun stage(moduleDescriptor: ModuleDescriptor): String =
    "Stage-${moduleDescriptor.name}"

  fun stage(moduleDescriptor: org.jetbrains.kotlin.descriptors.ModuleDescriptor): String =
    "Stage-${moduleDescriptor.name}"

  fun solverState(moduleDescriptor: ModuleDescriptor): String =
    "SolverState-${moduleDescriptor.name}"

  fun hints(module: ModuleDescriptor): String =
    "arrow-analysis-hint-${module.name}"
}

internal fun CompilerContext.solverState(
  context: DeclarationCheckerContext
): SolverState? = solverState(context.moduleDescriptor)

internal fun CompilerContext.solverState(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor
): SolverState? = get(Keys.solverState(KotlinModuleDescriptor(module)))

internal fun CompilerContext.initialize(
  module: ModuleDescriptor
) {
  ensureInitialized(Keys.solverState(module)) {
    SolverState(NameProvider())
  }
  ensureInitialized(Keys.hints(module)) {
    HintState.NeedsProcessing
  }
  ensureInitialized(Keys.stage(module)) {
    Stage.Init
  }
}

internal inline fun <reified A : Any> CompilerContext.ensureInitialized(key: String, acquire: () -> A) {
  val thing: A? = get(key)
  if (thing == null) {
    set(key, acquire())
  }
}

fun Collection<KtFile>.declarationDescriptors(
  context: ResolutionContext
): List<DeclarationDescriptor> =
  this.flatMap { file ->
    file.declarations.mapNotNull { decl ->
      context.descriptorFor(decl.model())
    }
  }

private fun CompilerContext.setHintsAs(module: ModuleDescriptor, state: HintState) {
  set(Keys.hints(module), state)
}

private fun CompilerContext.isInStage(module: org.jetbrains.kotlin.descriptors.ModuleDescriptor, stage: Stage) =
  get<Stage>(Keys.stage(module)) == stage

private fun CompilerContext.setStageAs(module: org.jetbrains.kotlin.descriptors.ModuleDescriptor, stage: Stage) {
  set(Keys.stage(module), stage)
}

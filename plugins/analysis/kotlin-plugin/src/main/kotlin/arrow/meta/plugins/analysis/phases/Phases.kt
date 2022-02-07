package arrow.meta.plugins.analysis.phases

import arrow.meta.ArrowMetaConfigurationKeys
import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.getOrCreateBaseDirectory
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnalysisResult
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.check.checkDeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromAnnotations
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromDSL
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.ir.HintState
import arrow.meta.plugins.analysis.phases.ir.annotateWithConstraints
import arrow.meta.plugins.analysis.phases.ir.hintsFile
import kotlin.io.path.Path
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

internal fun Meta.analysisPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { _, module, _, _, bindingTrace, _ ->
          val kotlinModule: KotlinModuleDescriptor = KotlinModuleDescriptor(module)
          initialize(kotlinModule)
          when (get<HintState>(Keys.hints(kotlinModule))) {
            HintState.NeedsProcessing -> {
              setHintsAs(kotlinModule, HintState.Processed)
              val path = getOrCreateBaseDirectory(configuration)
              org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(
                bindingTrace.bindingContext,
                module,
                emptyList(),
                listOfNotNull(path)
              )
            }
            else -> null
          }
        },
        analysisCompleted = { _, module, bindingTrace, files ->
          if (isInStage(module, Stage.CollectConstraints)) {
            val kotlinModule: KotlinModuleDescriptor = KotlinModuleDescriptor(module)
            val solverState = solverState(module)
            val context = KotlinResolutionContext(solverState, bindingTrace, module)
            if (solverState != null) {
              val locals = files.declarationDescriptors(context)
              val (result, interesting) =
                solverState.collectConstraintsFromAnnotations(locals, kotlinModule, context)
              val currentParseWarnings = bindingTrace.bindingContext.diagnostics.toList()
              setParseWarningsAs(module, currentParseWarnings)
              setStageAs(module, Stage.Prove) // we end the CollectConstraints phase
              when (result) {
                AnalysisResult.Retry -> {
                  if (interesting.isNotEmpty()) {
                    // 1. generate the additional file with hints
                    val path = getOrCreateBaseDirectory(configuration)
                    hintsFile(path.absolutePath, module, interesting)
                    setHintsAs(kotlinModule, HintState.NeedsProcessing)
                    // 2. retry with all the gathered information
                    org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(
                      bindingTrace.bindingContext,
                      module,
                      emptyList(),
                      listOf(path.absoluteFile)
                    )
                  } else {
                    // no need to create the hints file
                    setHintsAs(kotlinModule, HintState.Processed)
                    org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(
                      bindingTrace.bindingContext,
                      module,
                      emptyList(),
                      emptyList()
                    )
                  }
                }
                AnalysisResult.ParsingError -> {
                  solverState.notifyModuleProcessed(kotlinModule)
                  org.jetbrains.kotlin.analyzer.AnalysisResult.compilationError(
                    bindingTrace.bindingContext
                  )
                }
                AnalysisResult.Completed -> {
                  // TODO this is never reached
                  solverState.notifyModuleProcessed(kotlinModule)
                  null
                }
              }
            } else {
              null
            }
          } else {
            // HACK: because diagnostics are cleared after retrying
            //       we keep them in a temporary place and re-issue them
            if (isInStage(module, Stage.Prove)) {
              getParseWarnings(module)?.forEach { bindingTrace.report(it) }
            }
            val solverState = solverState(module)
            solverState?.notifyModuleProcessed(KotlinModuleDescriptor(module))
            null
          }
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        if (isInStage(context.moduleDescriptor, Stage.Init) ||
            isInStage(context.moduleDescriptor, Stage.CollectConstraints)
        ) {
          setStageAs(context.moduleDescriptor, Stage.CollectConstraints)
          val solverState = solverState(context)
          val kotlinContext =
            KotlinResolutionContext(solverState, context.trace, context.moduleDescriptor)
          val decl = declaration.model<KtDeclaration, Declaration>() as? Declaration
          if (decl != null && solverState != null) {
            decl.collectConstraintsFromDSL(solverState, kotlinContext, descriptor.model())
          }
        }
      },
      declarationChecker { declaration, descriptor, context ->
        if (isInStage(context.moduleDescriptor, Stage.Prove)) {
          val solverState = solverState(context)
          val kotlinContext =
            KotlinResolutionContext(solverState, context.trace, context.moduleDescriptor)
          val decl = declaration.model<KtDeclaration, Declaration>() as? Declaration
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
  Init,
  CollectConstraints,
  Prove
}

object Keys {
  fun stage(moduleDescriptor: ModuleDescriptor): String = "Stage-${moduleDescriptor.name}"

  fun stage(moduleDescriptor: org.jetbrains.kotlin.descriptors.ModuleDescriptor): String =
    "Stage-${moduleDescriptor.name}"

  fun solverState(moduleDescriptor: ModuleDescriptor): String =
    "SolverState-${moduleDescriptor.name}"

  fun hints(module: ModuleDescriptor): String = "arrow-analysis-hint-${module.name}"

  fun parseWarnings(module: ModuleDescriptor): String =
    "arrow-analysis-parse-warnings-${module.name}"

  fun parseWarnings(module: org.jetbrains.kotlin.descriptors.ModuleDescriptor): String =
    "arrow-analysis-parse-warnings-${module.name}"
}

internal fun CompilerContext.solverState(context: DeclarationCheckerContext): SolverState? =
  solverState(context.moduleDescriptor)

internal fun CompilerContext.solverState(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor
): SolverState? = get(Keys.solverState(KotlinModuleDescriptor(module)))

internal fun CompilerContext.initialize(module: ModuleDescriptor) {
  ensureInitialized(Keys.solverState(module)) {
    val baseDir =
      configuration?.get(ArrowMetaConfigurationKeys.BASE_DIR)?.firstOrNull()
        ?: System.getProperty("user.dir")
    val createOutputFile = { s: String ->
      val buildDir = getOrCreateBaseDirectory(configuration)
      val path = Path(buildDir.path, s)
      path.parent.toFile().mkdirs()
      path.toFile().writer()
    }
    SolverState(baseDir, createOutputFile)
  }
  ensureInitialized(Keys.hints(module)) { HintState.NeedsProcessing }
  ensureInitialized(Keys.stage(module)) { Stage.Init }
  ensureInitialized(Keys.parseWarnings(module)) { emptyList<Diagnostic>() }
}

internal inline fun <reified A : Any> CompilerContext.ensureInitialized(
  key: String,
  acquire: () -> A
) {
  val thing: A? = get(key)
  if (thing == null) {
    set(key, acquire())
  }
}

fun Collection<KtFile>.declarationDescriptors(
  context: ResolutionContext
): List<DeclarationDescriptor> =
  this.flatMap { file ->
    file.declarations.mapNotNull { decl -> context.descriptorFor(decl.model()) }
  }

private fun CompilerContext.setHintsAs(module: ModuleDescriptor, state: HintState) {
  set(Keys.hints(module), state)
}

private fun CompilerContext.isInStage(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor,
  stage: Stage
) = get<Stage>(Keys.stage(module)) == stage

private fun CompilerContext.setStageAs(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor,
  stage: Stage
) {
  set(Keys.stage(module), stage)
}

private fun CompilerContext.setParseWarningsAs(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor,
  warnings: List<Diagnostic>
) {
  set(Keys.parseWarnings(module), warnings)
}

private fun CompilerContext.getParseWarnings(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor
) = get<List<Diagnostic>>(Keys.parseWarnings(module))

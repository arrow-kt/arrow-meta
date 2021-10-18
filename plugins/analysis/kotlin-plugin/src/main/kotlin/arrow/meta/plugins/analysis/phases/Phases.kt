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
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectDeclarationsConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.finalizeConstraintsCollection
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.ir.HintState
import arrow.meta.plugins.analysis.phases.ir.NeedsProcessing
import arrow.meta.plugins.analysis.phases.ir.Processed
import arrow.meta.plugins.analysis.phases.ir.annotateWithConstraints
import arrow.meta.plugins.analysis.phases.ir.hintGenKey
import arrow.meta.plugins.analysis.phases.ir.hintsFile
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

internal fun Meta.analysisPhases(): ExtensionPhase =
  Composite(
    listOf(
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          val kotlinModule: KotlinModuleDescriptor = module.model()
          val hintsKey = hintGenKey(kotlinModule)
          ensureSolverStateInitialization(kotlinModule)
          when (get<HintState>(hintsKey) ?: NeedsProcessing) {
            NeedsProcessing -> {
              set(hintsKey, Processed)
              val path = getOrCreateBaseDirectory(null)
              org.jetbrains.kotlin.analyzer.AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), listOfNotNull(path))
            }
            Processed -> {
              set(hintsKey, Processed)
              null
            }
          }
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val kotlinModule: KotlinModuleDescriptor = module.model()
          val context = KotlinResolutionContext(bindingTrace, module)
          val locals = files.declarationDescriptors(context)
          val (result, interesting) = finalizeConstraintsCollection(solverState(module), locals, kotlinModule, context)
          when (result) {
            AnalysisResult.Retry -> {
              // 1. generate the additional file with hints
              //val parentPath = files.firstParentPath()?.let { java.io.File(it) }
              val path = getOrCreateBaseDirectory(null)
              hintsFile(path.absolutePath, module, interesting)
              set(hintGenKey(kotlinModule), NeedsProcessing)
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
        },
      ),
      declarationChecker { declaration, descriptor, context ->
        val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
        (declaration.model<KtDeclaration, Declaration>() as? Declaration)?.let { decl ->
          collectDeclarationsConstraints(solverState(context), kotlinContext, decl, descriptor.model())
        }
      },
      declarationChecker { declaration, descriptor, context ->
        val kotlinContext = KotlinResolutionContext(context.trace, context.moduleDescriptor)
        (declaration.model<KtDeclaration, Declaration>() as? Declaration)?.let { decl ->
          checkDeclarationConstraints(solverState(context), kotlinContext, decl, descriptor.model())
        }
      },
      irFunction { fn ->
        annotateWithConstraints(fn)
        null
      },
      irDumpKotlinLike()
    )
  )

internal fun CompilerContext.solverState(
  context: DeclarationCheckerContext
): SolverState? = solverState(context.moduleDescriptor)

internal fun CompilerContext.solverState(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor
): SolverState? = get(SolverState.key(KotlinModuleDescriptor(module)))

internal fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  // ensure that the solver state is initialized
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState(NameProvider())
    set(SolverState.key(module), state)
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

private fun Iterable<KtFile>.firstParentPath(): String? =
  firstOrNull()?.virtualFilePath?.let { java.io.File(it).parentFile.absolutePath }

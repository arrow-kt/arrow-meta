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
import arrow.meta.plugins.analysis.phases.ir.hintsFile
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import arrow.meta.quotes.Transform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

typealias KotlinFqName = org.jetbrains.kotlin.name.FqName

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
          val locals = files.flatMap { file ->
            file.declarations.mapNotNull { decl ->
              context.descriptorFor(decl.model())
            }
          }
          when (finalizeConstraintsCollection(solverState(module), locals, kotlinModule, context)) {
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
        compilerContext.run {
          module?.let { recordedNames(it) }
            ?.let { annotateWithConstraints(it, fn) }
        }
        null
      },
      IrGeneration { compilerContext, moduleFragment, _ ->
        compilerContext.run {
          module?.let { recordedNames(it) }
            ?.let { recordedNames ->
              Transform.newSources<KtFile>(hintsFile(moduleFragment.descriptor, recordedNames))
            }
        }
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

internal fun CompilerContext.recordedNames(
  module: org.jetbrains.kotlin.descriptors.ModuleDescriptor
): MutableSet<KotlinFqName>? = get(recordedNamesFor(KotlinModuleDescriptor(module)))

internal fun CompilerContext.ensureSolverStateInitialization(
  module: ModuleDescriptor
) {
  // ensure that the solver state is initialized
  val solverState = get<SolverState>(SolverState.key(module))
  if (solverState == null) {
    val state = SolverState(NameProvider())
    set(SolverState.key(module), state)
  }
  // and do the same for the set of recorded names
  val recordedNames = get<MutableSet<KotlinFqName>>(recordedNamesFor(module))
  if (recordedNames == null) {
    val newRecordedNames = mutableSetOf<KotlinFqName>()
    set(recordedNamesFor(module), newRecordedNames)
  }
}

fun recordedNamesFor(moduleDescriptor: ModuleDescriptor): String =
  "RecordedNames-${moduleDescriptor.name}"

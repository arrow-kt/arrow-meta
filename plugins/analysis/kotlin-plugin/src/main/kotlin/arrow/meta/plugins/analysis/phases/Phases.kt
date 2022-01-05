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
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocationWithRange
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import kotlin.io.path.Path
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallAtom
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.NewResolutionOldInference
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope

internal fun Meta.analysisPhases(): ExtensionPhase =
  Composite(
    listOf(

      typeResolution(
        interceptFunctionLiteralDescriptor = { expression, context, descriptor ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptFunctionLiteralDescriptor:\t ${expression.text}"
          )
          descriptor
        },
        interceptType = { element, context, resultType ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptType:\t ${element.text}"
          )
          resultType
        }
      ),

      callResolution(
        interceptCandidates = { candidates, context, candidateResolver, callResolver, name, kind, tracing ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptCandidates:\t $name. Candidates: ${candidates.kotlinLike()}"
          )
          candidates
        },
        interceptFunctionCandidates = { candidates, scopeTower, resolutionContext, resolutionScope, callResolver, name, location ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptFunctionCandidates:\t $name. Candidates: ${candidates.kotlinLike()}"
          )
          candidates
        },
        interceptFunctionCandidatesWithReceivers = { candidates, scopeTower, resolutionContext, resolutionScope, callResolver, name, location, dispatchReceiver, extensionReceiver ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptFunctionCandidatesWithReceivers:\t $name. Candidates: ${candidates.kotlinLike()}"
          )
          candidates
        },
        interceptResolvedCallAtomCandidate = { candidateDescriptor: CallableDescriptor, completedCallAtom: ResolvedCallAtom, trace: BindingTrace?, resultSubstitutor: NewTypeSubstitutor?, diagnostics: Collection<KotlinCallDiagnostic> ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptResolvedCallAtomCandidate:\t Candidates: ${candidateDescriptor.name}"
          )
          candidateDescriptor
        },
        interceptVariableCandidates = { candidates: Collection<VariableDescriptor>, scopeTower: ImplicitScopeTower, resolutionContext: BasicCallResolutionContext, resolutionScope: ResolutionScope, callResolver: CallResolver, name: Name, location: LookupLocation ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptVariableCandidates:\t $name. Candidates: ${candidates.kotlinLike()}"
          )
          candidates
        },
        interceptVariableCandidatesWithReceivers = { candidates, scopeTower, resolutionContext, resolutionScope, callResolver, name, location, dispatchReceiver, extensionReceiver ->
          messageCollector?.report(CompilerMessageSeverity.WARNING,
            "interceptVariableCandidatesWithReceivers:\t $name. Candidates: ${candidates.kotlinLike()}"
          )
          candidates
        }
      ),


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

private fun Collection<org.jetbrains.kotlin.descriptors.DeclarationDescriptor>.kotlinLike(): String =
  joinToString { it.name.toString() }

private fun Collection<NewResolutionOldInference.MyCandidate>.kotlinLike(unit: Unit = Unit): String =
  joinToString { it.resolvedCall.call.callElement.text }

object Keys {
  fun stage(moduleDescriptor: ModuleDescriptor): String = "Stage-${moduleDescriptor.name}"

  fun stage(moduleDescriptor: org.jetbrains.kotlin.descriptors.ModuleDescriptor): String =
    "Stage-${moduleDescriptor.name}"

  fun solverState(moduleDescriptor: ModuleDescriptor): String =
    "SolverState-${moduleDescriptor.name}"

  fun hints(module: ModuleDescriptor): String = "arrow-analysis-hint-${module.name}"
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

package arrow.meta.dsl.analysis

import arrow.meta.dsl.platform.cli
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CallResolutionInterceptor
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.phases.analysis.TypeResolutionInterceptor
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.CandidateResolver
import org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallAtom
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.NewResolutionOldInference
import org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver
import org.jetbrains.kotlin.resolve.diagnostics.MutableDiagnosticsWithSuppression
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingContext

/**
 * The Analysis phase determines if the parsed AST type checks and resolves properly. As part of
 * this phase, we have access to events happening before and after resolution. Before resolution, we
 * are given the chance to modify the compiler trees in the form of `KtElement` via the [Quote
 * Template System].
 */
interface AnalysisSyntax {

  /**
   * [additionalSources] is a function that is invoked before resolution and allows us to provide an
   * additional set of [KtFile] files. These files will be considered part of the compilation unit
   * alongside the user sources.
   */
  fun additionalSources(
    collectAdditionalSourcesAndUpdateConfiguration:
    CompilerContext.(
      knownSources: Collection<KtFile>,
      configuration: CompilerConfiguration,
      project: Project
    ) -> Collection<KtFile>
  ): CollectAdditionalSources =
    object : CollectAdditionalSources {
      override fun CompilerContext.collectAdditionalSourcesAndUpdateConfiguration(
        knownSources: Collection<KtFile>,
        configuration: CompilerConfiguration,
        project: Project
      ): Collection<KtFile> =
        collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
    }

  fun typeResolution(
    interceptFunctionLiteralDescriptor: CompilerContext.(
      expression: KtLambdaExpression,
      context: ExpressionTypingContext,
      descriptor: AnonymousFunctionDescriptor
    ) -> AnonymousFunctionDescriptor,
    interceptType: CompilerContext.(
      element: KtElement,
      context: ExpressionTypingContext,
      resultType: KotlinType
    ) -> KotlinType
  ): TypeResolutionInterceptor =
    object : TypeResolutionInterceptor {
      override fun CompilerContext.interceptFunctionLiteralDescriptor(
        expression: KtLambdaExpression,
        context: ExpressionTypingContext,
        descriptor: AnonymousFunctionDescriptor
      ): AnonymousFunctionDescriptor =
        interceptFunctionLiteralDescriptor(expression, context, descriptor)


      override fun CompilerContext.interceptType(
        element: KtElement,
        context: ExpressionTypingContext,
        resultType: KotlinType
      ): KotlinType =
        interceptType(element, context, resultType)
    }

  fun callResolution(
    interceptResolvedCallAtomCandidate: CompilerContext.(
      candidateDescriptor: CallableDescriptor,
      completedCallAtom: ResolvedCallAtom,
      trace: BindingTrace?,
      resultSubstitutor: NewTypeSubstitutor?,
      diagnostics: Collection<KotlinCallDiagnostic>
    ) -> CallableDescriptor,
    interceptCandidates: CompilerContext.(
      candidates: Collection<NewResolutionOldInference.MyCandidate>,
      context: BasicCallResolutionContext,
      candidateResolver: CandidateResolver,
      callResolver: CallResolver,
      name: Name,
      kind: NewResolutionOldInference.ResolutionKind,
      tracing: TracingStrategy
    ) -> Collection<NewResolutionOldInference.MyCandidate>,
    interceptFunctionCandidates: CompilerContext.(
      candidates: Collection<FunctionDescriptor>,
      scopeTower: ImplicitScopeTower,
      resolutionContext: BasicCallResolutionContext,
      resolutionScope: ResolutionScope,
      callResolver: CallResolver,
      name: Name,
      location: LookupLocation
    ) -> Collection<FunctionDescriptor>,
    interceptFunctionCandidatesWithReceivers: CompilerContext.(
      candidates: Collection<FunctionDescriptor>,
      scopeTower: ImplicitScopeTower,
      resolutionContext: BasicCallResolutionContext,
      resolutionScope: ResolutionScope,
      callResolver: PSICallResolver,
      name: Name,
      location: LookupLocation,
      dispatchReceiver: ReceiverValueWithSmartCastInfo?,
      extensionReceiver: ReceiverValueWithSmartCastInfo?
    ) -> Collection<FunctionDescriptor>,
    interceptVariableCandidates: CompilerContext.(
      candidates: Collection<VariableDescriptor>,
      scopeTower: ImplicitScopeTower,
      resolutionContext: BasicCallResolutionContext,
      resolutionScope: ResolutionScope,
      callResolver: CallResolver,
      name: Name,
      location: LookupLocation
    ) -> Collection<VariableDescriptor>,
    interceptVariableCandidatesWithReceivers: CompilerContext.(
      candidates: Collection<VariableDescriptor>,
      scopeTower: ImplicitScopeTower,
      resolutionContext: BasicCallResolutionContext,
      resolutionScope: ResolutionScope,
      callResolver: PSICallResolver,
      name: Name,
      location: LookupLocation,
      dispatchReceiver: ReceiverValueWithSmartCastInfo?,
      extensionReceiver: ReceiverValueWithSmartCastInfo?
    ) -> Collection<VariableDescriptor>
  ): CallResolutionInterceptor =
    object : CallResolutionInterceptor {
      override fun CompilerContext.interceptResolvedCallAtomCandidate(
        candidateDescriptor: CallableDescriptor,
        completedCallAtom: ResolvedCallAtom,
        trace: BindingTrace?,
        resultSubstitutor: NewTypeSubstitutor?,
        diagnostics: Collection<KotlinCallDiagnostic>
      ): CallableDescriptor =
        interceptResolvedCallAtomCandidate(
          candidateDescriptor,
          completedCallAtom,
          trace,
          resultSubstitutor,
          diagnostics
        )

      override fun CompilerContext.interceptCandidates(
        candidates: Collection<NewResolutionOldInference.MyCandidate>,
        context: BasicCallResolutionContext,
        candidateResolver: CandidateResolver,
        callResolver: CallResolver,
        name: Name,
        kind: NewResolutionOldInference.ResolutionKind,
        tracing: TracingStrategy
      ): Collection<NewResolutionOldInference.MyCandidate> =
        interceptCandidates(candidates, context, candidateResolver, callResolver, name, kind, tracing)

      override fun CompilerContext.interceptFunctionCandidates(
        candidates: Collection<FunctionDescriptor>,
        scopeTower: ImplicitScopeTower,
        resolutionContext: BasicCallResolutionContext,
        resolutionScope: ResolutionScope,
        callResolver: CallResolver,
        name: Name,
        location: LookupLocation
      ): Collection<FunctionDescriptor> =
        interceptFunctionCandidates(
          candidates,
          scopeTower,
          resolutionContext,
          resolutionScope,
          callResolver,
          name,
          location
        )

      override fun CompilerContext.interceptFunctionCandidates(
        candidates: Collection<FunctionDescriptor>,
        scopeTower: ImplicitScopeTower,
        resolutionContext: BasicCallResolutionContext,
        resolutionScope: ResolutionScope,
        callResolver: PSICallResolver,
        name: Name,
        location: LookupLocation,
        dispatchReceiver: ReceiverValueWithSmartCastInfo?,
        extensionReceiver: ReceiverValueWithSmartCastInfo?
      ): Collection<FunctionDescriptor> =
        interceptFunctionCandidatesWithReceivers(
          candidates,
          scopeTower,
          resolutionContext,
          resolutionScope,
          callResolver,
          name,
          location,
          dispatchReceiver,
          extensionReceiver
        )

      override fun CompilerContext.interceptVariableCandidates(
        candidates: Collection<VariableDescriptor>,
        scopeTower: ImplicitScopeTower,
        resolutionContext: BasicCallResolutionContext,
        resolutionScope: ResolutionScope,
        callResolver: CallResolver,
        name: Name,
        location: LookupLocation
      ): Collection<VariableDescriptor> =
        interceptVariableCandidates(
          candidates,
          scopeTower,
          resolutionContext,
          resolutionScope,
          callResolver,
          name,
          location
        );

      override fun CompilerContext.interceptVariableCandidates(
        candidates: Collection<VariableDescriptor>,
        scopeTower: ImplicitScopeTower,
        resolutionContext: BasicCallResolutionContext,
        resolutionScope: ResolutionScope,
        callResolver: PSICallResolver,
        name: Name,
        location: LookupLocation,
        dispatchReceiver: ReceiverValueWithSmartCastInfo?,
        extensionReceiver: ReceiverValueWithSmartCastInfo?
      ): Collection<VariableDescriptor> =
        interceptVariableCandidatesWithReceivers(
          candidates,
          scopeTower,
          resolutionContext,
          resolutionScope,
          callResolver,
          name,
          location,
          dispatchReceiver,
          extensionReceiver
        )

    }

  /**
   * The [analysis] function allows us to intercept analysis before and after it happens, altering
   * the analysis inputs and outputs. Altering the inputs on @doAnalysis allows us to modify the
   * compiler trees in the AST before they are considered for resolution. This allows us to build
   * the [Quote] in this phase, which is Arrow Meta's higher level API. Altering the output with
   * [analysisCompleted] allows us to modify the binding trace and all elements resulting from
   * analysis.
   */
  fun analysis(
    doAnalysis:
    CompilerContext.(
      project: Project,
      module: ModuleDescriptor,
      projectContext: ProjectContext,
      files: Collection<KtFile>,
      bindingTrace: BindingTrace,
      componentProvider: ComponentProvider
    ) -> AnalysisResult?,
    analysisCompleted:
    CompilerContext.(
      project: Project,
      module: ModuleDescriptor,
      bindingTrace: BindingTrace,
      files: Collection<KtFile>
    ) -> AnalysisResult? =
      Noop.nullable5()
  ): AnalysisHandler =
    object : AnalysisHandler {
      override fun CompilerContext.doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
      ): AnalysisResult? {
        return doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
      }

      override fun CompilerContext.analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
      ): AnalysisResult? = analysisCompleted(project, module, bindingTrace, files)
    }

  fun preprocessedVirtualFileFactory(
    createPreprocessedFile: CompilerContext.(file: VirtualFile?) -> VirtualFile?,
    createPreprocessedLightFile: CompilerContext.(file: LightVirtualFile?) -> LightVirtualFile? =
      Noop.nullable2()
  ): PreprocessedVirtualFileFactory =
    object : PreprocessedVirtualFileFactory {
      override fun CompilerContext.isPassThrough(): Boolean = false

      override fun CompilerContext.createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        createPreprocessedFile(file)

      override fun CompilerContext.createPreprocessedLightFile(
        file: LightVirtualFile?
      ): LightVirtualFile? = createPreprocessedLightFile(file)
    }

  /**
   * The [extraImports] function allows the user to provide an additional set of
   * [org.jetbrains.kotlin.psi.KtImportInfo] imports for each individual [KtFile] considered as
   * sources. This additional set of imports are taken into account when resolving symbols in the
   * resolution phase of a [KtFile].
   */
  fun extraImports(
    extraImports: CompilerContext.(ktFile: KtFile) -> Collection<KtImportInfo>
  ): ExtraImports =
    object : ExtraImports {
      override fun CompilerContext.extraImports(ktFile: KtFile): Collection<KtImportInfo> =
        extraImports(ktFile)
    }

  /**
   * The [suppressDiagnostic] function allows selectively determining whether a diagnostic emitted
   * by the compiler affects compilation. As the compiler performs resolution, it will generate
   * diagnostic of type [Diagnostic] with different [Severity] levels: [Severity.INFO],
   * [Severity.ERROR], and [Severity.WARNING]. When the [suppressDiagnostic] returns [true], the
   * emitted diagnostic is suppressed and removed from the [BindingTrace]. This will cause the
   * [Diagnostic] to not be considered in further compilation phases.
   */
  @Suppress("UNCHECKED_CAST")
  fun suppressDiagnostic(f: (Diagnostic) -> Boolean): ExtensionPhase =
    cli {
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val diagnostics: MutableDiagnosticsWithSuppression =
            BindingTraceContext::class
              .java
              .getDeclaredField("mutableDiagnostics")
              .also { it.isAccessible = true }
              .get(bindingTrace) as
              MutableDiagnosticsWithSuppression
          val mutableDiagnostics = diagnostics.getOwnDiagnostics() as ArrayList<Diagnostic>
          mutableDiagnostics.removeIf(f)
          null
        }
      )
    }
      ?: ExtensionPhase.Empty

  /** @see [suppressDiagnostic] including access to the [BindingTrace] */
  @Suppress("UNCHECKED_CAST")
  fun suppressDiagnosticWithTrace(f: BindingTrace.(Diagnostic) -> Boolean): ExtensionPhase =
    cli {
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val diagnostics: MutableDiagnosticsWithSuppression =
            BindingTraceContext::class
              .java
              .getDeclaredField("mutableDiagnostics")
              .also { it.isAccessible = true }
              .get(bindingTrace) as
              MutableDiagnosticsWithSuppression
          val mutableDiagnostics = diagnostics.getOwnDiagnostics() as ArrayList<Diagnostic>
          mutableDiagnostics.removeIf { f(bindingTrace, it) }
          null
        }
      )
    }
      ?: ExtensionPhase.Empty
}

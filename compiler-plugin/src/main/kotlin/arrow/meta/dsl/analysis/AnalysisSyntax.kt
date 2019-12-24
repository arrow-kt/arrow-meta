package arrow.meta.dsl.analysis

import arrow.meta.dsl.platform.cli
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.applySmartCast
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.Proof
import arrow.meta.proofs.dump
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.diagnostics.MutableDiagnosticsWithSuppression
import java.util.ArrayList
import java.util.Collections.addAll

/**
 * The Analysis phase determines if the parsed AST type checks and resolves properly.
 * As part of this phase, we have access to events happening before and after resolution.
 * Before resolution, we are given the chance to modify the compiler trees in the form of `KtElement` via
 * the [Quote Template System].
 */
interface AnalysisSyntax {

  /**
   * [additionalSources] is a function that is invoked before resolution and allows us to provide an additional set of [KtFile] files.
   * These files will be considered part of the compilation unit alongside the user sources.
   */
  fun additionalSources(
    collectAdditionalSourcesAndUpdateConfiguration: CompilerContext.(knownSources: Collection<KtFile>, configuration: CompilerConfiguration, project: Project) -> Collection<KtFile>
  ): CollectAdditionalSources =
    object : CollectAdditionalSources {
      override fun CompilerContext.collectAdditionalSourcesAndUpdateConfiguration(knownSources: Collection<KtFile>, configuration: CompilerConfiguration, project: Project): Collection<KtFile> =
        collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
    }

  /**
   * The [analysis] function allows us to intercept analysis before and after it happens, altering the analysis inputs and outputs.
   * Altering the inputs on @doAnalysis allows us to modify the compiler trees in the AST before they are considered for resolution.
   * This allows us to build the [Quote] in this phase, which is Arrow Meta's higher level API.
   * Altering the output with [analysisCompleted] allows us to modify the binding trace and all elements resulting from analysis.
   */
  fun analysis(
    doAnalysis: CompilerContext.(project: Project, module: ModuleDescriptor, projectContext: ProjectContext, files: Collection<KtFile>, bindingTrace: BindingTrace, componentProvider: ComponentProvider) -> AnalysisResult?,
    analysisCompleted: CompilerContext.(project: Project, module: ModuleDescriptor, bindingTrace: BindingTrace, files: Collection<KtFile>) -> AnalysisResult? = Noop.nullable5()
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
      ): AnalysisResult? =
        analysisCompleted(project, module, bindingTrace, files)
    }

  fun preprocessedVirtualFileFactory(
    createPreprocessedFile: CompilerContext.(file: VirtualFile?) -> VirtualFile?,
    createPreprocessedLightFile: CompilerContext.(file: LightVirtualFile?) -> LightVirtualFile? = Noop.nullable2()
  ): PreprocessedVirtualFileFactory =
    object : PreprocessedVirtualFileFactory {
      override fun CompilerContext.isPassThrough(): Boolean = false

      override fun CompilerContext.createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        createPreprocessedFile(file)

      override fun CompilerContext.createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
        createPreprocessedLightFile(file)
    }

  /**
   * The [extraImports] function allows the user to provide an additional set of [org.jetbrains.kotlin.psi.KtImportInfo] imports for
   * each individual [KtFile] considered as sources.
   * This additional set of imports are taken into account when resolving symbols in the resolution phase of a [KtFile].
   */
  fun extraImports(extraImports: CompilerContext.(ktFile: KtFile) -> Collection<KtImportInfo>): ExtraImports =
    object : ExtraImports {
      override fun CompilerContext.extraImports(ktFile: KtFile): Collection<KtImportInfo> =
        extraImports(ktFile)
    }

  /**
   * The [suppressDiagnostic] function allows selectively determining whether a diagnostic emitted by the compiler affects compilation.
   * As the compiler performs resolution, it will generate diagnostic of type [Diagnostic] with different [Severity] levels:
   * [Severity.INFO], [Severity.ERROR], and [Severity.WARNING].
   * When the [suppressDiagnostic] returns [true], the emitted diagnostic is suppressed and removed from the [BindingTrace].
   * This will cause the [Diagnostic] to not be considered in further compilation phases.
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
            BindingTraceContext::class.java.getDeclaredField("mutableDiagnostics").also { it.isAccessible = true }.get(bindingTrace) as MutableDiagnosticsWithSuppression
          val mutableDiagnostics = diagnostics.getOwnDiagnostics() as ArrayList<Diagnostic>
          mutableDiagnostics.removeIf(f)
          null
        }
      )
    } ?: ExtensionPhase.Empty

  /**
   * @see [suppressDiagnostic] including access to the [BindingTrace]
   */
  @Suppress("UNCHECKED_CAST")
  fun suppressDiagnosticWithTrace(f: BindingTrace.(Diagnostic) -> Boolean): ExtensionPhase =
    cli {
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          val diagnostics: MutableDiagnosticsWithSuppression =
            BindingTraceContext::class.java.getDeclaredField("mutableDiagnostics").also { it.isAccessible = true }.get(bindingTrace) as MutableDiagnosticsWithSuppression
          val mutableDiagnostics = diagnostics.getOwnDiagnostics() as ArrayList<Diagnostic>
          mutableDiagnostics.removeIf { f(bindingTrace, it) }
          null
        }
      )
    } ?: ExtensionPhase.Empty

}

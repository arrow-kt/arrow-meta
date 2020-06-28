package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.patternMatching.phases.analysis.resolvePatternExpression
import arrow.meta.plugins.patternMatching.phases.analysis.wildcards
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.resolve.lazy.FileScopeProvider
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import org.jetbrains.kotlin.util.slicedMap.Slices

val Meta.patternMatching: CliPlugin
  get() =
    "Pattern Matching Plugin" {
      lateinit var typingService: ExpressionTypingServices
      lateinit var fileScopeProvider: FileScopeProvider

      meta(
        enableIr(),
        suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            typingService = componentProvider.get()
            fileScopeProvider = componentProvider.get()
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            bindingTrace.resolvePatternExpression { it.wildcards(typingService, fileScopeProvider) }
            null
          }
        ),
        irDump()
      )
    }

val CAPTURED_PARAMS = Slices.createCollectiveSetSlice<KtNameReferenceExpression>()

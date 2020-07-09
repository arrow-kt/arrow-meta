package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.patternMatching.phases.analysis.PatternExpression.Param.Captured
import arrow.meta.plugins.patternMatching.phases.analysis.PatternResolutionContext
import arrow.meta.plugins.patternMatching.phases.analysis.fillCapturedParameters
import arrow.meta.plugins.patternMatching.phases.analysis.patternExpressionResolution
import arrow.meta.plugins.patternMatching.phases.analysis.referPlaceholder
import arrow.meta.plugins.patternMatching.phases.analysis.resolvePatternExpression
import arrow.meta.plugins.patternMatching.phases.ir.patchIrWhen
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
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
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            val context = PatternResolutionContext(this)

            val patternExpressions = files.flatMap { file ->
              context.resolvePatternExpression(file) { whenExpr ->
                patternExpressionResolution(whenExpr).map { (entry, expr) ->
                  expr.parameters.forEachIndexed { index, param ->
                    if (param is Captured) {
                      val nameExpression = expr.paramExpression(index) as KtSimpleNameExpression
                      bindingTrace.record(PATTERN_EXPRESSION_CAPTURED_PARAMS, nameExpression)
                      referPlaceholder(nameExpression, index)
                    }
                  }

                  if (expr.parameters.any { it is Captured && !it.isWildcard }) {
                    val params = fillCapturedParameters(entry, expr)
                    params.forEach {
                      bindingTrace.record(PATTERN_EXPRESSION_BODY_PARAMS, it)
                    }
                  }

                  expr
                }
              }
            }
            println("Resolved pattern expressions $patternExpressions")

            null
          }
        ),
        suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
        irWhen { patchIrWhen(it) },
        irDump()
      )
    }

val PATTERN_EXPRESSION_CAPTURED_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()
val PATTERN_EXPRESSION_BODY_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()

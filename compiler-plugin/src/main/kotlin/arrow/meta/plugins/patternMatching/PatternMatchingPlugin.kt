package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.callExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
//      callExpression({ this follows casePatternRules }) { expr ->
//        Transform.replace(expr, expr.desugared.callExpression)
//      }
      analysis(doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
       null
      },
      analysisCompleted = { project, module, bindingTrace, files ->
        // TODO: Note for Matt: This IS analysis. We can get the appropriate information about the structure from here,
        // as opposed to what I was doing before with the callExpression quotes.
        // We're going to use record to map the frontend/backend together. We want to find the _ that is null in the
        // binding context.
        val result = bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
        val result2 = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
        val result3 = bindingTrace.bindingContext.getSliceContents(BindingContext.RESOLVED_CALL)
        // TODO: the null type we need. Still need an extra check for `_`, (in the key).
        val result4: MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>? = result.entries.find { it.value.type == null }
        if (result4 != null) {
          bindingTrace.record(BindingContext.EXPRESSION_TYPE_INFO, result4.key, result4.value)
//          result4.setValue()
          1 == 1
        }
        null
      })
    )
  }

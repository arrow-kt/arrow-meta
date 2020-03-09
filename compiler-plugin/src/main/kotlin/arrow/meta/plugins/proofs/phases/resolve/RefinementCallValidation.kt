package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.internal.Noop
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.quotes.Scope
import arrow.meta.quotes.orEmpty
import arrow.meta.quotes.scope
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import javax.script.ScriptEngineManager

internal fun Meta.cliValidateRefinedCalls(): AnalysisHandler {
  return analysis(
    doAnalysis = Noop.nullable7<AnalysisResult>(),
    analysisCompleted = { project, module, bindingTrace, files ->
      validateRefinedCalls(bindingTrace)
      null
    }
  )
}

internal fun CompilerContext.validateRefinedCalls(bindingTrace: BindingTrace) {
  val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
  calls
    .forEach { (element, call) ->
      call.getResolvedCall(bindingTrace.bindingContext)?.let(::validateConstructorCall)
    }
}

internal fun CompilerContext.validateConstructorCall(call: ResolvedCall<*>) {
  val currentModule = module
  if (currentModule != null) {
    val targetType = call.getReturnType()
    val refinementSource = module.proofs.refinementExpressionFor(targetType)
    val isProof = call.call.calleeExpression?.parents?.any { it is KtNamedFunction && it.isProof() } ?: false
    if (!isProof && refinementSource != null) {
      val refinementExpression = refinementSource.expression.orEmpty().value
      if (refinementExpression != null) {
        val entry = call.valueArguments.entries.firstOrNull()
        if (entry != null) {
          val (parameter, resolved) = entry
          val type = parameter.type.unwrap()
          val argument = resolved as? ExpressionValueArgument
          val argumentExpression = argument?.valueArgument?.getArgumentExpression().scope()
          if (argument != null) {
            val expression =
              """
                {
                  val target: $type = $argumentExpression
                  target
                }()
              """.expression
            validateExpression(argumentExpression, expression, refinementExpression, targetType)
          }
        }
      }
    }
  }
}

val proofAnnotation: Regex = Regex("@(arrow\\.)?Proof\\((.*)\\)")

private fun KtNamedFunction.isProof(): Boolean =
  isAnnotatedWith(proofAnnotation)

@Suppress("UNCHECKED_CAST")
internal fun CompilerContext.validateExpression(
  originalExpression: Scope<KtExpression>,
  source: Scope<KtExpression>,
  refinementExpression: KtExpression,
  targetType: KotlinType
) {
  val constantChecker =
    """
      ${source}.run ${refinementExpression.text}
    """.trimIndent()
  val engine = ScriptEngineManager().getEngineByExtension("kts")

    Log.Verbose({ "eval refinement result [1] : \n$this" }) {
      engine.eval("1")
    }
  Log.Verbose({ "eval refinement result [2] : \n$this" }) {
    engine.eval("2")
  }
  val expressionResult =
    Log.Verbose({ "eval refinement result : \n$this" }) {
      engine.eval(constantChecker) as? Map<Any?, Any?>
    }
  if (expressionResult != null) {
    val validationKeys = expressionResult.keys.filterIsInstance<String>()
    val validation = validationKeys.map {
      it to expressionResult[it] as Boolean
    }.toMap()
    val isValid = validation.all { it.value }
    if (!isValid) {
      reportValidationErrors(validation, targetType, originalExpression.toString())
    }
  }
}

internal fun CompilerContext.reportValidationErrors(validation: Map<String, Boolean>, targetType: KotlinType, source: String?) {
  validation.forEach { (msg, valid) ->
    if (!valid) {
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Predicate for $targetType($source) failed: \n$msg")
    }
  }
}

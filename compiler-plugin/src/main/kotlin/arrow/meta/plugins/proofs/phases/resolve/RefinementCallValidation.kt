package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.internal.Noop
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.phases.evaluateDependsOnRewindableAnalysisPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.orEmpty
import arrow.meta.quotes.scope
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
//import org.jetbrains.kotlin.scripting.compiler.plugin.repl.ReplInterpreter
//import org.jetbrains.kotlin.scripting.compiler.plugin.repl.configuration.ConsoleReplConfiguration
import org.jetbrains.kotlin.types.KotlinType
import javax.script.ScriptEngineManager
import javax.script.ScriptException

typealias Validation = Map<String, Boolean>

internal fun Meta.cliValidateRefinedCalls(): AnalysisHandler =
  analysis(
    doAnalysis = Noop.nullable7<AnalysisResult>(),
    analysisCompleted = { project, module, bindingTrace, files ->
      evaluateDependsOnRewindableAnalysisPhase { validateRefinedCalls(bindingTrace) }
      null
    }
  )

internal fun CompilerContext.validateRefinedCalls(bindingTrace: BindingTrace) {
  val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
  calls.forEach { (element, call) ->
      val resolvedCall = call.getResolvedCall(bindingTrace.bindingContext)
      val validation = resolvedCall?.let(::validateConstructorCall).orEmpty()
      resolvedCall?.let {
        reportValidationErrors(validation, element)
      }
    }
}

fun CompilerContext.validateConstructorCall(call: ResolvedCall<*>): Validation {
  val currentModule = module
  return when {
    currentModule != null -> {
      val targetType = call.getReturnType()
      val refinementSource = module.proofs.refinementExpressionFromAnnotation(targetType)
        ?: module.proofs.refinementExpressionFromPsi(targetType)
      val isProof = call.call.calleeExpression?.parents?.any { it is KtNamedFunction && it.isProof() } ?: false
      when {
        !isProof && refinementSource != null -> {
          val refinementExpression = refinementSource.expression.orEmpty().value
          when {
            refinementExpression != null -> {
              val entry = call.valueArguments.entries.firstOrNull()
              when {
                entry != null -> {
                  val (parameter, resolved) = entry
                  val type = parameter.type.unwrap()
                  val argument = resolved as? ExpressionValueArgument
                  val argumentExpression = argument?.valueArgument?.getArgumentExpression().scope()
                  when {
                    argument != null -> {
                      val expression =
                        """
                        {
                        val target: $type = $argumentExpression
                        target
                        }()
                        """.expression
                      validateExpression(targetType, argumentExpression, expression, refinementExpression)
                    }
                    else -> emptyMap()
                  }
                }
                else -> emptyMap()
              }
            }
            else -> emptyMap()
          }
        }
        else -> emptyMap()
      }
    }
    else -> emptyMap()
  }
}

val proofAnnotation: Regex = Regex("@(arrow\\.)?Proof\\((.*)\\)")

private fun KtNamedFunction.isProof(): Boolean =
  isAnnotatedWith(proofAnnotation)

@Suppress("UNCHECKED_CAST")
internal fun CompilerContext.validateExpression(
  targetType: KotlinType,
  argumentExpression: Scope<KtExpression>,
  source: Scope<KtExpression>,
  refinementExpression: KtExpression
): Validation {
  val constantChecker =
    """
      ${source}.run ${refinementExpression.text}
    """.trimIndent()
  val newConfig = configuration?.copy() ?: CompilerConfiguration()
  newConfig.put(JVMConfigurationKeys.IR, false)
  //val interpreter = ReplInterpreter(Disposable { println("refinement interpreter disposed") }, newConfig, ConsoleReplConfiguration())
  //val interpreter = ScriptEngineManager().getEngineByExtension("kts")

  val expressionResult =
    Log.Verbose({ "eval refinement result : \n$this" }) {
      evalConstantExpression(constantChecker, argumentExpression, targetType)
    }
  return if (expressionResult != null) {
    val validationKeys = expressionResult.keys.filterIsInstance<String>()
    validationKeys.map {
      it to expressionResult[it] as Boolean
    }.toMap()
  } else emptyMap()
}

private fun CompilerContext.evalConstantExpression(constantChecker: String, argumentExpression: Scope<KtExpression>, targetType: KotlinType): Map<out Any?, Any?>? =
  try {
    eval(constantChecker) as? Map<Any?, Any?>
  } catch (e: Throwable) {
    when {
      e is ScriptException && e.message?.contains("error: unresolved reference: $argumentExpression") == true ->
        mapOf("""
      `$argumentExpression` is a runtime value disallowed in $targetType's constructor.
      Replace with a constant value or use the safe constructor:
      `$targetType.from($argumentExpression)`
    """.trimIndent() to false)
      else -> throw e
    }
  }

internal fun CompilerContext.reportValidationErrors(validation: Map<String, Boolean>, element: KtElement) {
  validation.forEach { (msg, valid) ->
    if (!valid) {
      messageCollector?.report(
        CompilerMessageSeverity.ERROR,
        "Predicate for ${element.text} failed: \n$msg",
        MessageUtil.psiElementToMessageLocation(element)
      )
    }
  }
}

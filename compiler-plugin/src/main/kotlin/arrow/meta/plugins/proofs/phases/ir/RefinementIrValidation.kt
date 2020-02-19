package arrow.meta.plugins.proofs.phases.ir

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.resolve.refinementExpressionFor
import arrow.meta.quotes.orEmpty
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.types.KotlinType
import javax.script.ScriptEngineManager

internal fun CompilerContext.validateConstructorCall(call: IrConstructorCall) {
  Log.Verbose({ "validateConstructorCall: ${call.dump()}" }) {
    val currentModule = module
    when {
      currentModule != null -> {
        val maybeConstant = call.getValueArgument(0) as? IrConst<*>
        val targetType = call.descriptor.returnType
        val refinementSource = module.proofs.refinementExpressionFor(targetType)
        val refinementExpression = refinementSource?.expression.orEmpty().value
        validateRefinementExpression(refinementExpression, maybeConstant, targetType)
      }
    }
  }
}

internal fun CompilerContext.validateRefinementExpression(refinementExpression: KtExpression?, maybeConstant: IrConst<*>?, targetType: KotlinType) {
  when {
    refinementExpression != null && maybeConstant != null -> {
      val source: String? = constantValueExpression(maybeConstant)
      if (source != null) {
        validateConstant(source, refinementExpression, targetType)
      }
    }
  }
}

internal fun constantValueExpression(maybeConstant: IrConst<*>): String? =
  when (maybeConstant.value) {
    is String -> """"${maybeConstant.value}""""
    else -> null
  }

internal fun CompilerContext.validateConstant(source: String?, refinementExpression: KtExpression, targetType: KotlinType) {
  val constantChecker =
    """
                        ${source}.run ${refinementExpression.text}
                        """.trimIndent()
  val engine = ScriptEngineManager().getEngineByExtension("kts")
  val expressionResult = engine.eval(constantChecker) as? Map<Any?, Any?>
  if (expressionResult != null) {
    val validationKeys = expressionResult.keys.filterIsInstance<String>()
    val validation = validationKeys.map {
      it to expressionResult[it] as Boolean
    }.toMap()
    val isValid = validation.all { it.value }
    if (!isValid) {
      reportValidationErrors(validation, targetType, source)
    }
  }
}

internal fun CompilerContext.reportValidationErrors(validation: Map<String, Boolean>, targetType: KotlinType, source: String?) {
  validation.forEach { (msg, valid) ->
    if (!valid) {
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Predicate for $targetType(`$source`) failed: \n$msg")
    }
  }
}

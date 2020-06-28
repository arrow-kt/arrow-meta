package arrow.meta.plugins.patternMatching.phases.resolve.diagnostics

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.patternMatching.CAPTURED_PARAMS
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CompilerContext.suppressUnresolvedReference(diagnostic: Diagnostic): Boolean =
  diagnostic.factory == Errors.UNRESOLVED_REFERENCE &&
    diagnostic.safeAs<DiagnosticWithParameters1<KtNameReferenceExpression, KtNameReferenceExpression>>()?.let { diagnosticWithParameters ->
      Log.Verbose({ "suppressUnresolvedReference: $this" }) {
        val expr = diagnosticWithParameters.psiElement
        val bindingTrace = ctx.componentProvider!!.get<BindingTrace>()
        val bindingContext = bindingTrace.bindingContext

        // captured param in constructor ...
        val condition = expr.getParentOfType<KtWhenCondition>(true, KtWhenExpression::class.java)
        val argument = condition.findConstructorParam(bindingContext) {
          it.getArgumentExpression() == expr
        }
        if (argument != null) {
          bindingTrace.record(CAPTURED_PARAMS, expr)
          return@Verbose true
        }

        // .. or reference to constructor in when body
        val entry = expr.getParentOfType<KtWhenEntry>(true, KtWhenExpression::class.java)
        if (entry.isReferenceToConstuctorParam(bindingContext, expr)) {
          bindingTrace.record(CAPTURED_PARAMS, expr)
          return@Verbose true
        }

        // probably not our case
        false
      }
    } == true

private fun KtWhenCondition?.findConstructorParam(
  bindingContext: BindingContext,
  filter: (KtValueArgument) -> Boolean
): KtValueArgument? {
  if (this == null) return null

  val constructorCall = getChildOfType<KtCallExpression>() ?: return null
  val constructorDescriptor = constructorCall.getResolvedCall(bindingContext)?.resultingDescriptor

  return if (constructorDescriptor is ConstructorDescriptor) {
    constructorCall.valueArguments.find(filter)
  } else {
    null
  }
}

private fun KtWhenEntry?.isReferenceToConstuctorParam(bindingContext: BindingContext, expr: KtNameReferenceExpression): Boolean {
  if (this == null) return false
  if (expr.getReferencedName() == "_") return false

  val conditions = conditions

  return conditions.any {
    it.findConstructorParam(bindingContext) { arg ->
      val argumentExpression = arg.getArgumentExpression()
      argumentExpression is KtNameReferenceExpression && argumentExpression.getReferencedName() == expr.getReferencedName()
    } != null
  }
}

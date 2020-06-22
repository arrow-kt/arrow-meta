package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

fun BindingTrace.resolvePatternExpression(resolution: (BindingTrace) -> Unit) =
  resolution(this)

val BindingTrace.wildcards
  get() = wildcardTypeInfoEntries.forEach { entry ->
    recordType(entry.key, bindingContext.targetType?.type)
    record<KtExpression, CompileTimeConstant<*>>(
      BindingContext.COMPILE_TIME_VALUE,
      entry.key,
      bindingContext.targetExpression
    )
  }

private val BindingTrace.wildcardTypeInfoEntries: List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>>
  get() = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && it.key.text == "_" }

private val BindingContext.targetType
  get() = getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .find { it.key.textMatches(""""Matt"""") }?.value

private val BindingContext.targetExpression
  get() = getSliceContents(BindingContext.COMPILE_TIME_VALUE).entries
    .find { it.key.text == """"Matt"""" }?.value

package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

fun BindingTrace.resolveExpression(resolution: (BindingTrace) -> Unit) =
  resolution(this)

fun wildcards(project: Project, bindingTrace: BindingTrace) =
  bindingTrace.wildcardTypeInfoEntries.forEach { entry ->
    constructorArgTypeInfo(bindingTrace).let { replacementType ->
      bindingTrace.recordType(entry.key, replacementType?.type)
    }
  }

val BindingTrace.wildcardTypeInfoEntries: List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>>
  get() = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { it.value.type == null && it.key.text == "_" }

private fun constructorArgTypeInfo(bindingTrace: BindingTrace) =
  bindingTrace.bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
    .entries
    .find { it.key.textMatches(""""Matt"""") }
    ?.value

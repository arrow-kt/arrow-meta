package arrow.meta.plugins.patternMatching.phases.resolve.diagnostics

import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

infix fun KtExpression.follows(pattern: List<(KtExpression) -> Boolean>): Boolean =
  pattern.all { it(this) }

infix fun BindingTrace.expressionTypeInfoEntriesFollowing(pattern: List<(MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>) -> Boolean>): List<MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>> =
  bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { entry -> pattern.all { it(entry) } }

infix fun BindingTrace.callEntriesFollowing(pattern: List<(MutableMap.MutableEntry<KtExpression, Call>) -> Boolean>): List<MutableMap.MutableEntry<KtElement, Call>> =
  bindingContext.getSliceContents(BindingContext.CALL).entries
    .filter { entry -> pattern.all { it(entry as MutableMap.MutableEntry<KtExpression, Call>) } }

val casePatternRules: List<(KtExpression) -> Boolean> = listOf(
  { expr -> expr is KtCallExpression },
  { expr -> expr.firstChild is KtReferenceExpression },
  { expr -> expr.firstChild.textMatches("case") },
  { expr -> expr.firstChild.nextSibling is KtValueArgumentList }
)

val analysisTypeInfoPatternRules: List<(MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>) -> Boolean> = listOf(
  { entry -> entry.value.type == null },
  { entry -> entry.key.textMatches("_") }
)

val analysisCallPatternRules: List<(MutableMap.MutableEntry<KtExpression, Call>) -> Boolean> = listOf(
  { entry -> entry.key.textMatches("_") }
)

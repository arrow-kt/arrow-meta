package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace

infix fun KtExpression.follows(pattern: ExpressionPattern): Boolean =
  pattern.all { it(this) }

infix fun BindingTrace.entriesFollowing(pattern: BindingTracePattern): List<BindingTraceEntry> =
  bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO).entries
    .filter { entry -> pattern.all { rule -> rule(entry) } }

val casePatternRules: ExpressionPattern = listOf(
  { expr -> expr is KtCallExpression },
  { expr -> expr.firstChild is KtReferenceExpression },
  { expr -> expr.firstChild.textMatches("case") },
  { expr -> expr.firstChild.nextSibling is KtValueArgumentList }
)

val analysisPatternRules: BindingTracePattern = listOf(
  { entry -> entry.value.type == null },
  { entry -> entry.key.textMatches("_") }
)
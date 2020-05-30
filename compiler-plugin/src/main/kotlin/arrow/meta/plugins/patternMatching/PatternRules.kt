package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

typealias ExpressionPattern = List<(KtExpression) -> Boolean>

infix fun KtExpression.follows(pattern: ExpressionPattern): Boolean =
  pattern.all { it(this) }

val casePatternRules: ExpressionPattern = listOf(
  { expr -> expr is KtCallExpression },
  { expr -> expr.firstChild is KtReferenceExpression },
  { expr -> expr.firstChild.textMatches("case") },
  { expr -> expr.firstChild.nextSibling is KtValueArgumentList }
)

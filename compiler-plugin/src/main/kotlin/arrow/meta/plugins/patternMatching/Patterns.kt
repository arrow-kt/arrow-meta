package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

typealias ExpressionPattern = List<(KtExpression) -> Boolean>

infix fun KtExpression.matches(pattern: ExpressionPattern): Boolean =
  pattern.all { it(this) }

val constructorPattern: ExpressionPattern = listOf(
  { expr -> expr is KtCallExpression },
  { expr -> expr.firstChild is KtReferenceExpression },
  { expr -> expr.text.first().isUpperCase() },
  { expr -> expr.firstChild.nextSibling is KtValueArgumentList }
)

val wildcardPattern: ExpressionPattern =
  listOf { expr -> (expr.firstChild.nextSibling as KtValueArgumentList).arguments.any { it.textMatches("_") } }

val constructorWithWildcardPattern = constructorPattern + wildcardPattern

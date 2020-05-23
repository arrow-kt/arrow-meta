package arrow.meta.plugins.patternmatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.whenExpression
import org.jetbrains.kotlin.psi.*

/**
 * Keyword that sets apart a match. It cannot be `is` or `in`
 */
const val matchKeyword = "match"

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      whenExpression(KtWhenExpression::containsPMatching) { whenExpr ->
        TODO()
      },
      TODO()
    )
  }

fun KtWhenExpression.containsPMatching() = entries.any(KtWhenEntry::isPMCondition)

/**
 * Whether an entry follows the EBNF grammar:
 * PM -> [matchKeyword] `Type`? '[' PM+ ']' ?
 * which corresponds to a pattern match.
 *
 * Initially, we will not consider disjunctions
 */
private fun KtWhenEntry.isPMCondition() =
  conditions.size == 1 && conditions[0].isPM()

fun KtWhenCondition.isPM() = this is KtWhenConditionWithExpression
  && this.expression.let {
  it ?: return@let false
  it is KtBinaryExpression
    && it.right is KtCollectionLiteralExpression
    && it.left.let { leftExpr ->
    leftExpr is KtReferenceExpression && leftExpr.text == "match"
  }
}

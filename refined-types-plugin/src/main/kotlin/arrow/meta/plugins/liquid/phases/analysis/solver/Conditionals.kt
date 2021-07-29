package arrow.meta.plugins.liquid.phases.analysis.solver

import org.jetbrains.kotlin.psi.*

/**
 * Represents a condition which may appear in an `if` or a `when`.
 * It allows us to code uniformly over both elements.
 */
interface Condition {}
data class BooleanCondition(val expression: KtExpression): Condition
data class NonBooleanCondition(val whenCondition: KtWhenCondition): Condition

data class ConditionalBranch(val condition: List<Condition>?, val body: KtExpression, val whole: KtElement)

data class ConditionalBranches(val subject: KtExpression?, val branches: List<ConditionalBranch>)

/**
 * Bridges between Kotlin's [KtWhenCondition]
 * and our own [Condition].
 */
fun KtWhenCondition.toCondition(): Condition = when (this) {
  // TODO: this is not really correct, we need to check whether
  // we have a real Boolean condition, or otherwise generate an equality
  // (for example, the case when (x) { 0 -> ... })
  is KtWhenConditionWithExpression -> BooleanCondition(expression!!)
  else -> NonBooleanCondition(this)
}

/**
 * Obtains the information for conditional expressions, if possible.
 * It uniformizes everything into a [ConditionalBranches].
 */
fun KtExpression.conditionalBranches(): ConditionalBranches? = when (this) {
  is KtWhenExpression -> conditionalBranches()
  is KtIfExpression -> conditionalBranches()
  else -> null
}

fun KtExpression.isConditional(): Boolean =
  this is KtWhenExpression || this is KtIfExpression

fun KtWhenExpression.conditionalBranches(): ConditionalBranches {
  val branches = entries.map { entry ->
    ConditionalBranch(
      if (entry.isElse) null else entry.conditions.map { it.toCondition() },
      entry.expression!!,
      entry
    )
  }
  return ConditionalBranches(subjectExpression, branches)
}

fun KtIfExpression.conditionalBranches(): ConditionalBranches {
  val branches = listOf(
    ConditionalBranch(listOf(BooleanCondition(condition!!)), `then`!!, `then`!!),
    ConditionalBranch(null, `else`!!, `else`!!)
  )
  return ConditionalBranches(null, branches)
}
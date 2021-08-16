package arrow.meta.plugins.liquid.phases.analysis.solver

import org.jetbrains.kotlin.psi.*

data class ConditionalBranch(val condition: List<KtExpression>?, val body: KtExpression, val whole: KtElement)
data class ConditionalBranches(val subject: KtExpression?, val branches: List<ConditionalBranch>)

/**
 * Obtains the corresponding expression when the `when` has no subject.
 */
fun KtWhenCondition.subjectlessCondition(): KtExpression = when (this) {
  is KtWhenConditionWithExpression -> expression!!
  else -> throw IllegalArgumentException("no subject in pattern")
}

fun KtWhenCondition.subjectfulCondition(subject: KtExpression): KtExpression? =
  when (this) {
    is KtWhenConditionWithExpression ->
      expression?.let {
        // TODO: the idea is to build here
        //       an expression $subject == $it
        null
      }
    else -> null
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
      when {
        entry.isElse -> null
        subjectExpression != null ->
          entry.conditions.mapNotNull { it.subjectfulCondition(subjectExpression!!) }
        else -> entry.conditions.map { it.subjectlessCondition() }
      },
      entry.expression!!,
      entry
    )
  }
  return ConditionalBranches(subjectExpression, branches)
}

fun KtIfExpression.conditionalBranches(): ConditionalBranches {
  val branches = listOf(
    ConditionalBranch(listOf(condition!!), then!!, then!!),
    ConditionalBranch(null, `else`!!, `else`!!)
  )
  return ConditionalBranches(null, branches)
}
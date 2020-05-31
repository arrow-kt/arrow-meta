package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace

val KtCallExpression.desugar: String
  get() =
    firstChild.nextSibling
      .firstChild.nextSibling
      .text.replace("_", "person.firstName")

val BindingTrace.desugar
  get() =
    entriesFollowing(analysisPatternRules).forEach { entry ->
      // TODO Write replacement entry
      val parentCaseExpression = entry.key.parent.parent.parent.parent.parent.parent
      record(BindingContext.EXPRESSION_TYPE_INFO, entry.key, entry.value)
    }

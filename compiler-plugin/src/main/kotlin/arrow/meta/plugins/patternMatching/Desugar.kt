package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtCallExpression

val KtCallExpression.desugar: String
  get() =
    firstChild.nextSibling
      .firstChild.nextSibling
      .text.replace("_", "person.firstName")

package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.callExpression
import org.jetbrains.kotlin.psi.KtCallExpression

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      callExpression({ isPatternMatchExpression() }) { expr ->
        Transform.replace(expr, expr.desugar.whenEntry)
      }
    )
  }

private fun KtCallExpression.isPatternMatchExpression(): Boolean =
  text.contains("_")

package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenEntry

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      whenEntry({ text.startsWith("case") }) { expr ->
        Transform.replace(expr, expr.desugar.whenEntry)
      }
    )
  }

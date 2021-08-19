package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenCondition

open class WhenConditionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    whenConditionPlugin
  )
}

val Meta.whenConditionPlugin
  get() = "When Condition Scope Plugin" {
    meta(
      whenCondition(this, { true }) { c ->
        Transform.replace(
          replacing = c,
          newDeclaration = identity()
        )
      }
    )
  }

package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenCondition

open class WhenConditionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    whenConditionPlugin
  )
}

val Meta.whenConditionPlugin
  get() = "When Condition Scope Plugin" {
    meta(
      whenCondition({ true }) { c ->
        Transform.replace(
          replacing = c,
          newDeclaration = identity()
        )
      }
    )
  }
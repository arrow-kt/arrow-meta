package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classBody

open class ClassBodyPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    classBody
  )
}

// TODO remove after splitting elements apart
open class EnumBodyPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    enumBody
  )
}

private val Meta.classBody: CliPlugin
  get() =
    "Class Body Scope Plugin" {
      meta(
        classBody(this, { true }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration = identity()
          )
        }
      )
    }

// TODO remove after splitting elements apart
private val Meta.enumBody: CliPlugin
  get() =
    "Enum Body Scope Plugin" {
      meta(
        classBody(this, { true }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration = identity()
          )
        }
      )
    }

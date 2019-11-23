package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class TransformMetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> = (
    transformRemove
      + transformReplace
    )
}
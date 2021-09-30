package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.phases.CompilerContext

open class TransformMetaPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = (
    transformRemove +
    transformReplace +
    transformMany +
    transformNewSource
  )
}

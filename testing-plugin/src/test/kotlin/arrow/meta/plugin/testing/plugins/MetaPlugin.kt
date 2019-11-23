package arrow.meta.plugin.testing.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.testing.plugins.transform.transformRemove
import arrow.meta.plugin.testing.plugins.transform.transformReplace
import kotlin.contracts.ExperimentalContracts

open class MetaPlugin : Meta {
    @ExperimentalContracts
    override fun intercept(ctx: CompilerContext): List<Plugin> = (
        transformRemove
        + transformReplace
        + helloWorld
    )
}
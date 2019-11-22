package arrow.meta.plugin.testing.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class MetaPlugin : Meta {
    @ExperimentalContracts
    override fun intercept(ctx: CompilerContext): List<Plugin> =
        listOf(
            helloWorld
        )
}
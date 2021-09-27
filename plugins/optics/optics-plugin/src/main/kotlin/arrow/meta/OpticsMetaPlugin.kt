package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.optics.optics

/**
 * It contains the default meta bundled plugins.
 *
 * Compiler Plugin Authors can create a similar class or override this one to
 * provide their plugins.
 */
open class OpticsMetaPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      // lenses,
      optics
    )
}

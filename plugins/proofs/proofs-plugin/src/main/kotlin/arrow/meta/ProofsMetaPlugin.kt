package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.typeProofs

/**
 * It contains the default meta bundled plugins.
 *
 * Compiler Plugin Authors can create a similar class or override this one to
 * provide their plugins.
 */
open class ProofsMetaPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      typeProofs
    )
}

package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.optics.optics
import arrow.meta.plugins.proofs.typeProofs
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import kotlin.contracts.ExperimentalContracts

/**
 * The Meta Plugin contains the default meta bundled plugins for Arrow
 *
 * Compiler Plugin Authors can create a similar class or override this one to
 * provide their plugins.
 */
open class MetaPlugin : Meta {
  @ObsoleteDescriptorBasedAPI // TODO: remove
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      // higherKindedTypes2,
      // typeClasses,
      // comprehensions,
      // lenses,
      typeProofs,
      optics
    )
}

package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.comprehensions.comprehensions
import arrow.meta.plugins.higherkind.higherKindedTypes2
import arrow.meta.plugins.optics.lenses
import arrow.meta.plugins.patternMatching.patternMatching
import arrow.meta.plugins.proofs.typeProofs
import kotlin.contracts.ExperimentalContracts

/**
 * The Meta Plugin contains the default meta bundled plugins for Arrow
 *
 * Compiler Plugin Authors can create a similar class or override this one to
 * provide their plugins.
 */
open class MetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      higherKindedTypes2,
      //typeClasses,
      //comprehensions,
      //lenses,
      patternMatching
      //typeProofs
    )
}

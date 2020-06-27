package arrow.meta.ide

import arrow.meta.MetaPlugin
import arrow.meta.ide.phases.IdeContext
import arrow.meta.ide.plugins.initial.initialIdeSetUp
import arrow.meta.ide.plugins.patternMatching.patternMatchingIde
import arrow.meta.ide.plugins.proofs.typeProofsIde
import arrow.meta.ide.plugins.quotes.quotes
import kotlin.contracts.ExperimentalContracts

open class IdeMetaPlugin : MetaPlugin(), MetaIde {
  @ExperimentalContracts
  override fun intercept(ctx: IdeContext): List<IdePlugin> =
    listOf(
      initialIdeSetUp,
      patternMatchingIde,
      quotes
      //typeProofsIde
    )
}

package arrow.meta.ide

import arrow.meta.CliPlugin
import arrow.meta.MetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.ide.phases.IdeContext
import arrow.meta.ide.plugins.comprehensions.comprehensionsIdePlugin
import arrow.meta.ide.plugins.higherkinds.higherKindsIdePlugin
import arrow.meta.ide.plugins.initial.initialIdeSetUp
import arrow.meta.ide.plugins.nothing.nothingIdePlugin
import arrow.meta.ide.plugins.optics.opticsIdePlugin
import arrow.meta.ide.plugins.purity.purity
import arrow.meta.ide.plugins.quotes.quotes
import arrow.meta.ide.plugins.quotes.quotesCli
import arrow.meta.ide.plugins.typeclasses.typeclassesIdePlugin
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    super.intercept(ctx) + quotesCli

  @ExperimentalContracts
  override fun intercept(ctx: IdeContext): List<IdePlugin> =
    listOf(initialIdeSetUp) +
      quotes +
      purity +
      higherKindsIdePlugin +
      typeclassesIdePlugin +
      comprehensionsIdePlugin +
      opticsIdePlugin +
      nothingIdePlugin
}

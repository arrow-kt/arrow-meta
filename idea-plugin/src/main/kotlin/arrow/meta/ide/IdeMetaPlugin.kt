package arrow.meta.ide

import arrow.meta.CliPlugin
import arrow.meta.MetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.ide.plugins.proofs.typeProofsIde
import arrow.meta.ide.plugins.quotes.quotes
import arrow.meta.ide.plugins.quotes.quotesCli
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    super.intercept(ctx) + quotesCli

  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<IdePlugin> =
    (super.intercept(ctx) +
      //purity +
      //comprehensionsIdePlugin +
      //opticsIdePlugin +
      quotes +
      typeProofsIde)
}

package arrow.meta.ide

import arrow.meta.CliPlugin
import arrow.meta.MetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.ide.phases.IdeContext
import arrow.meta.ide.plugins.initial.initialIdeSetUp
import arrow.meta.ide.plugins.proofs.typeProofsIde
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    super.intercept(ctx) //+ quotesCli

  @ExperimentalContracts
  override fun intercept(ctx: IdeContext): List<IdePlugin> =
  //purity +
  //comprehensionsIdePlugin +
    //opticsIdePlugin +
    listOf(initialIdeSetUp, //quotes,
      typeProofsIde)
}

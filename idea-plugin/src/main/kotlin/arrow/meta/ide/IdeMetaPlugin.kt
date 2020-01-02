package arrow.meta.ide

import arrow.meta.MetaPlugin
import arrow.meta.Plugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.internal.registry.IdeInternalRegistry
import arrow.meta.ide.plugins.proofs.typeProofsIde
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> =
    super.intercept(ctx) +
      //initialIdeSetUp +
      //purity +
      //higherKindsIdePlugin +
      //typeclassesIdePlugin +
      //comprehensionsIdePlugin +
      //opticsIdePlugin +
      //nothingIdePlugin +
      typeProofsIde
}

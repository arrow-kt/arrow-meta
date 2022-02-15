package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.typeProofs

open class ProofsMetaPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(typeProofs)
}

class ProofsMetaCliProcessor : MetaCliProcessor("proofs")

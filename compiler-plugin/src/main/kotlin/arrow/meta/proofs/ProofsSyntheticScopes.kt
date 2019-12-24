package arrow.meta.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScopes
import org.jetbrains.kotlin.synthetic.JavaSyntheticScopes

class ProofsSyntheticScopes(delegate: JavaSyntheticScopes? = null, proofs: () -> List<Proof>) : SyntheticScopes {
  override val scopes: Collection<SyntheticScope> =
    Log.Verbose({ "ProofsSyntheticScopes.scopes $this" }) {
      delegate?.scopes.orEmpty() + listOf(ProofsSyntheticScope(proofs))
    }
}
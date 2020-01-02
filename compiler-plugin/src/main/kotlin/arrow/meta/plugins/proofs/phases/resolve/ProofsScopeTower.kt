package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.backend.common.SimpleMemberScope
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.scopes.ImportingScope
import org.jetbrains.kotlin.resolve.scopes.LexicalChainedScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.LocalRedeclarationChecker
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScopes
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.resolve.scopes.utils.addImportingScope
import org.jetbrains.kotlin.resolve.scopes.utils.memberScopeAsImportingScope
import org.jetbrains.kotlin.synthetic.JavaSyntheticScopes
import org.jetbrains.kotlin.types.TypeApproximator

class ProofsScopeTower(
  module: ModuleDescriptor,
  val proofs: List<Proof>
) : ImplicitScopeTower {
  val memberScope = SimpleMemberScope(proofs.map { it.through })
  val scopeOwner = module
  val importingScope = LexicalScopeImpl(ImportingScope.Empty, scopeOwner, false, null, LexicalScopeKind.SYNTHETIC, LocalRedeclarationChecker.DO_NOTHING) {}
  override val dynamicScope: MemberScope = SimpleMemberScope(proofs.map { it.through })
  override val isDebuggerContext: Boolean = false
  override val isNewInferenceEnabled: Boolean = false
  override val lexicalScope: LexicalScope = LexicalChainedScope(
    parent = importingScope,
    ownerDescriptor = scopeOwner,
    isOwnerDescriptorAccessibleByLabel = false,
    implicitReceiver = null,
    kind = LexicalScopeKind.SYNTHETIC,
    memberScopes = listOf({ proofs }.memberScope())
  ).addImportingScope(memberScope.memberScopeAsImportingScope())
  override val location: LookupLocation = NoLookupLocation.FROM_BACKEND
  override val syntheticScopes: SyntheticScopes = ProofsSyntheticScopes { proofs }
  override val typeApproximator: TypeApproximator = TypeApproximator(module.builtIns)
  override fun getImplicitReceiver(scope: LexicalScope): ReceiverValueWithSmartCastInfo? = null

  override fun interceptCandidates(resolutionScope: ResolutionScope, name: Name, initialResults: Collection<FunctionDescriptor>, location: LookupLocation): Collection<FunctionDescriptor> =
    Log.Verbose({"ProofsScopeTower.interceptCandidates: $resolutionScope, name: $name, initialResults: $initialResults, $location"}) {
      emptyList()
    }
}

class ProofsSyntheticScopes(delegate: JavaSyntheticScopes? = null, proofs: () -> List<Proof>) : SyntheticScopes {
  override val scopes: Collection<SyntheticScope> =
    Log.Verbose({ "ProofsSyntheticScopes.scopes $this" }) {
      delegate?.scopes.orEmpty() + listOf(ProofsSyntheticScope(proofs))
    }
}
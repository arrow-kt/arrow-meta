package arrow.meta.plugins.proofs.phases.resolve.scopes

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.backend.common.SimpleMemberScope
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitScopeTower
import org.jetbrains.kotlin.resolve.calls.tower.ImplicitsExtensionsResolutionFilter
import org.jetbrains.kotlin.resolve.scopes.ImportingScope
import org.jetbrains.kotlin.resolve.scopes.LexicalChainedScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.LocalRedeclarationChecker
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScopes
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.TypeApproximator

class ProofsScopeTower(
  module: ModuleDescriptor,
  val proofs: List<Proof>,
  compilerContext: CompilerContext
) : ImplicitScopeTower {
  val scopeOwner = module
  val importingScope =
    LexicalScopeImpl(
      ImportingScope.Empty,
      scopeOwner,
      false,
      null,
      LexicalScopeKind.SYNTHETIC,
      LocalRedeclarationChecker.DO_NOTHING
    ) {}
  override val dynamicScope: MemberScope = SimpleMemberScope(proofs.map { it.through })
  override val implicitsResolutionFilter: ImplicitsExtensionsResolutionFilter =
    ImplicitsExtensionsResolutionFilter.Default
  override val isDebuggerContext: Boolean = false
  override val isNewInferenceEnabled: Boolean = false
  override val lexicalScope: LexicalScope =
    LexicalChainedScope.create(
      parent = importingScope,
      ownerDescriptor = scopeOwner,
      isOwnerDescriptorAccessibleByLabel = false,
      implicitReceiver = null,
      kind = LexicalScopeKind.SYNTHETIC,
      memberScopes = arrayOf({ proofs }.memberScope())
    ) // .addImportingScope(memberScope.memberScopeAsImportingScope())
  override val location: LookupLocation = NoLookupLocation.FROM_BACKEND
  override val syntheticScopes: SyntheticScopes = SyntheticScopes.Empty
  override val typeApproximator: TypeApproximator =
    TypeApproximator(
      module.builtIns,
      compilerContext.configuration?.languageVersionSettings ?: LanguageVersionSettingsImpl.DEFAULT
    )
  override fun getImplicitReceiver(scope: LexicalScope): ReceiverValueWithSmartCastInfo? = null
  override fun interceptFunctionCandidates(
    resolutionScope: ResolutionScope,
    name: Name,
    initialResults: Collection<FunctionDescriptor>,
    location: LookupLocation,
    dispatchReceiver: ReceiverValueWithSmartCastInfo?,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): Collection<FunctionDescriptor> = emptyList()

  override fun interceptVariableCandidates(
    resolutionScope: ResolutionScope,
    name: Name,
    initialResults: Collection<VariableDescriptor>,
    location: LookupLocation,
    dispatchReceiver: ReceiverValueWithSmartCastInfo?,
    extensionReceiver: ReceiverValueWithSmartCastInfo?
  ): Collection<VariableDescriptor> = emptyList()
}

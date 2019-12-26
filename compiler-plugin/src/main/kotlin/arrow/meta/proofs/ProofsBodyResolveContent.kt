package arrow.meta.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.plugins.proofs.ProofsDataFlowInfo
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptorWithResolutionScopes
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtScript
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.resolve.BodiesResolveContext
import org.jetbrains.kotlin.resolve.TopDownAnalysisContext
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfoFactory
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.descriptors.findPackageFragmentForFile
import org.jetbrains.kotlin.resolve.scopes.LexicalScope

class ProofsBodyResolveContent(
  val session: ResolveSession,
  val delegate: TopDownAnalysisContext
) : BodiesResolveContext by delegate {

  override fun getFiles(): Collection<KtFile> =
    Log.Verbose({ "ProofsBodyResolveContent.getFiles: $this" }) {
      delegate.files
    }

  override fun getTypeAliases(): MutableMap<KtTypeAlias, TypeAliasDescriptor> =
    Log.Verbose({ "ProofsBodyResolveContent.getTypeAliases: $this" }) {
      delegate.typeAliases
    }

  override fun getScripts(): MutableMap<KtScript, ClassDescriptorWithResolutionScopes> =
    Log.Verbose({ "ProofsBodyResolveContent.getScripts: $this" }) {
      delegate.scripts
    }

  override fun getOuterDataFlowInfo(): DataFlowInfo =
    Log.Verbose({ "ProofsBodyResolveContent.getOuterDataFlowInfo: $this" }) {
      delegate.outerDataFlowInfo.and(ProofsDataFlowInfo())
    }

  override fun getDeclaredClasses(): MutableMap<KtClassOrObject, ClassDescriptorWithResolutionScopes> =
    Log.Verbose({ "ProofsBodyResolveContent.getDeclaredClasses: $this" }) {
      delegate.declaredClasses
    }

  override fun getSecondaryConstructors(): MutableMap<KtSecondaryConstructor, ClassConstructorDescriptor> =
    Log.Verbose({ "ProofsBodyResolveContent.getSecondaryConstructors: $this" }) {
      delegate.secondaryConstructors
    }

  override fun getProperties(): MutableMap<KtProperty, PropertyDescriptor> =
    Log.Verbose({ "ProofsBodyResolveContent.getProperties: $this" }) {
      delegate.properties
    }

  override fun getDestructuringDeclarationEntries(): MutableMap<KtDestructuringDeclarationEntry, PropertyDescriptor> =
    Log.Verbose({ "ProofsBodyResolveContent.getDestructuringDeclarationEntries: $this" }) {
      delegate.destructuringDeclarationEntries
    }

  override fun getTopDownAnalysisMode(): TopDownAnalysisMode =
    Log.Verbose({ "ProofsBodyResolveContent.getTopDownAnalysisMode: $this" }) {
      delegate.topDownAnalysisMode
    }

  override fun getFunctions(): MutableMap<KtNamedFunction, SimpleFunctionDescriptor> =
    Log.Verbose({ "ProofsBodyResolveContent.getFunctions: $this" }) {
      delegate.functions
    }

  override fun getAnonymousInitializers(): MutableMap<KtAnonymousInitializer, ClassDescriptorWithResolutionScopes> =
    Log.Verbose({ "ProofsBodyResolveContent.getAnonymousInitializers: $this" }) {
      delegate.anonymousInitializers
    }

  override fun getDeclaringScope(p0: KtDeclaration): LexicalScope? =
    session.moduleDescriptor.run {
      val currentScope = session.declarationScopeProvider.getResolutionScopeForDeclaration(p0)
      findPackageFragmentForFile(p0.containingKtFile)?.let {
        typeProofs.lexicalScope(currentScope, it)
      }
    }
}
package arrow.meta.ide.plugins.quotes.synthetic

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.resolve.LOG
import arrow.meta.ide.plugins.quotes.cache.QuoteCache
import arrow.meta.ide.plugins.quotes.utils.descriptors
import arrow.meta.ide.plugins.quotes.utils.ktClassOrObject
import arrow.meta.ide.plugins.quotes.utils.synthetic
import arrow.meta.ide.plugins.quotes.utils.syntheticMembersOf
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.get
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.idea.stubindex.resolve.StubBasedPackageMemberDeclarationProvider
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError

val IdeMetaPlugin.quoteSyntheticResolver: ExtensionPhase
  get() = syntheticResolver { project: Project ->
    project.getService(QuoteCache::class.java)?.let { quoteCache ->
      syntheticResolver(
        generatePackageSyntheticClasses = { thisDescriptor: PackageFragmentDescriptor, name: Name, _: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor> ->
          thisDescriptor
            .takeIf { !it.isMetaSynthetic() }
            ?.run {
              result.replaceWithSynthetics(quoteCache, fqName, declarationProvider)
              quoteCache
                .descriptors<LazyClassDescriptor>(fqName)
                .filter { !it.isMetaSynthetic() }
                .map { it.synthetic(declarationProvider) }
                .run {
                  LOG.debug("generatePackageSyntheticClasses: $fqName: $this, $name, result: $result")
                  result.addAll(this)
                }
            }
        },
        generateSyntheticClasses = { thisDescriptor: ClassDescriptor, _: Name, _: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor> ->
          thisDescriptor.synthetic { packageName ->
            result.replaceWithSynthetics(quoteCache, packageName, declarationProvider)
            quoteCache
              .descriptors<LazyClassDescriptor>(packageName)
              .syntheticMembersOf<LazyClassDescriptor, LazyClassDescriptor>(this)
              .map { it.synthetic(declarationProvider) }
              .apply {
                LOG.debug("generateNestedSyntheticClasses: ${thisDescriptor.name}: $this")
                result.addAll(this)
              }
          }
        },
        generateSyntheticMethods = { thisDescriptor: ClassDescriptor, _: Name, _: BindingContext, _: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor> ->
          thisDescriptor.synthetic { packageName ->
            // TODO: result.replaceWithSynthetics(packageName)
            quoteCache
              .syntheticMembersOf<ClassDescriptor, SimpleFunctionDescriptor>(packageName, this)
              .map { it.synthetic() }
              .apply {
                LOG.debug("generateSyntheticMethods: ${thisDescriptor.name}: $this")
                result.addAll(this)
              }
          }
        },
        generateSyntheticProperties = { thisDescriptor: ClassDescriptor, _: Name, _: BindingContext, _: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor> ->
          thisDescriptor.synthetic { packageName ->
            quoteCache
              .syntheticMembersOf<ClassDescriptor, PropertyDescriptor>(packageName, this)
              .apply {
                LOG.debug("generateSyntheticProperties: $name: $this")
                result.addAll(this)
              }
          }
        },
        generateSyntheticSecondaryConstructors = { thisDescriptor: ClassDescriptor, _: BindingContext, result: MutableCollection<ClassConstructorDescriptor> ->
          thisDescriptor.synthetic { packageName ->
            quoteCache
              .syntheticMembersOf<ClassDescriptor, ClassConstructorDescriptor>(packageName, this)
              .filter { !it.isPrimary }
              .apply {
                LOG.debug("generateSyntheticSecondaryConstructors: $name: $this")
                result.addAll(this)
              }
          }
        },
        getSyntheticCompanionObjectNameIfNeeded = { thisDescriptor ->
          thisDescriptor.synthetic { packageName: FqName ->
            quoteCache
              .descriptors<ClassDescriptor>(packageName)
              .find { it.fqNameSafe == fqNameSafe }
              ?.companionObjectDescriptor
              ?.name
              ?.apply {
                LOG.debug("getSyntheticCompanionObjectNameIfNeeded: $name: $this")
              }
          }
        },
        getSyntheticFunctionNames = { thisDescriptor: ClassDescriptor ->
          thisDescriptor.synthetic { packageName: FqName ->
            quoteCache
              .syntheticMembersOf<ClassDescriptor, SimpleFunctionDescriptor>(packageName, this)
              .map { it.name }
              .apply {
                LOG.debug("getSyntheticFunctionNames: $name: $this")
              }
          }.orEmpty()
        },
        getSyntheticNestedClassNames = { thisDescriptor: ClassDescriptor ->
          thisDescriptor.synthetic { packageName ->
            quoteCache
              .syntheticMembersOf<ClassDescriptor, ClassDescriptor>(packageName, this)
              .map { it.name }
              .apply {
                LOG.debug("SyntheticNestedClassNames: $name: $this")
              }
          }.orEmpty()
        },
        addSyntheticSupertypes = { thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType> ->
          thisDescriptor.synthetic { packageName ->
            quoteCache.descriptors<ClassDescriptor>(packageName)
              .find { it.fqNameSafe == fqNameSafe }
              ?.typeConstructor
              ?.supertypes
              .orEmpty()
              .filterNotNull()
              .filterNot { it.isError }
              .apply {
                //supertypes.clear()
                LOG.debug("Found synth supertypes: $this")
                supertypes.addAll(this)
              }
          }
        }
      )
    }
  }

private fun MutableSet<ClassDescriptor>.replaceWithSynthetics(quoteCache: QuoteCache, packageName: FqName, declarationProvider: DeclarationProvider): Unit {
  val replacements = map { existing ->
    val synthDescriptors = quoteCache.descriptors(packageName).filterIsInstance<LazyClassDescriptor>()
    val replacement = synthDescriptors.find { synth -> synth.fqNameOrNull() == existing.fqNameOrNull() }
    existing to replacement
  }.toMap()
  removeIf { replacements[it] != null }
  addAll(replacements.values.filterNotNull().map { it.synthetic(declarationProvider) })
}

private fun SimpleFunctionDescriptor.synthetic(): SimpleFunctionDescriptor =
  copy(
    containingDeclaration,
    modality,
    if (visibility == DescriptorVisibilities.INHERITED) DescriptorVisibilities.PUBLIC else visibility,
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    true
  )

private fun LazyClassDescriptor.synthetic(declarationProvider: DeclarationProvider): SyntheticClassOrObjectDescriptor {
  val ktDeclaration = this.ktClassOrObject()
  return SyntheticClassOrObjectDescriptor(
    c = this["c"],
    parentClassOrObject = when (declarationProvider) {
      is ClassMemberDeclarationProvider -> declarationProvider.correspondingClassOrObject

      is StubBasedPackageMemberDeclarationProvider -> declarationProvider.getPackageFiles().firstOrNull {
        it.declarations.any { declaration -> declaration == ktDeclaration }
      }?.ktPureClassOrObject()

      is PackageMemberDeclarationProvider -> declarationProvider.getPackageFiles().firstOrNull {
        it.declarations.any { declaration -> declaration == ktDeclaration }
      }?.ktPureClassOrObject()

      else -> null
    } ?: ktDeclaration /*fixme!*/ ?: TODO("Unknown declaration provider ${declarationProvider.javaClass}"),
    containingDeclaration = containingDeclaration,
    name = name,
    source = SourceElement.NO_SOURCE,
    outerScope = scopeForClassHeaderResolution,
    modality = if (modality == Modality.SEALED) Modality.ABSTRACT else modality,
    visibility = if (visibility == DescriptorVisibilities.INHERITED) DescriptorVisibilities.PUBLIC else visibility,
    annotations = annotations,
    constructorVisibility = DescriptorVisibilities.PUBLIC,
    kind = kind,
    isCompanionObject = isCompanionObject
  ).apply {
    initialize()
  }
}

private fun KtFile.ktPureClassOrObject(): KtPureClassOrObject =
  object : KtPureClassOrObject {
    override fun hasExplicitPrimaryConstructor(): Boolean = false
    override fun getParent(): PsiElement = this@ktPureClassOrObject.psiOrParent
    override fun getName(): String? = this@ktPureClassOrObject.name
    override fun getPrimaryConstructorParameters(): List<KtParameter> = emptyList()
    override fun getSecondaryConstructors(): List<KtSecondaryConstructor> = emptyList()
    override fun hasPrimaryConstructor(): Boolean = false
    override fun getContainingKtFile(): KtFile = this@ktPureClassOrObject
    override fun getPrimaryConstructor(): KtPrimaryConstructor? = null
    override fun getSuperTypeListEntries(): List<KtSuperTypeListEntry> = emptyList()
    override fun getPsiOrParent(): KtElement = this@ktPureClassOrObject.psiOrParent
    override fun getPrimaryConstructorModifierList(): KtModifierList? = null
    override fun isLocal(): Boolean = false
    override fun getCompanionObjects(): List<KtObjectDeclaration> = emptyList()
    override fun getDeclarations(): List<KtDeclaration> = this@ktPureClassOrObject.declarations
    override fun getBody(): KtClassBody? = null
  }

/*
private fun MutableCollection<SimpleFunctionDescriptor>.replaceWithSynthetics(packageName: FqName): Unit {
  val replacements = mapNotNull { existing ->
    val synthDescriptors = cache.resolved(packageName)?.filterIsInstance<LazyClassDescriptor>() ?: emptyList()
    val replacement = synthDescriptors.findWithTransform { synth ->
      val fn = synth.unsubstitutedMemberScope.getContributedFunctions(existing.name, NoLookupLocation.FROM_BACKEND).firstOrNull()
      (fn != null) to fn
    }
    existing to replacement
  }.toMap()
  removeIf { replacements[it] != null }
  addAll(replacements.values.filterNotNull())
  val allSynths = map { it.synthetic() }
  clear()
  addAll(allSynths)
}*/


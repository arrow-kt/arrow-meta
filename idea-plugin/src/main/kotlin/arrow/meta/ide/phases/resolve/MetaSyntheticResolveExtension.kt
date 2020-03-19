package arrow.meta.ide.phases.resolve

import arrow.meta.ide.plugins.quotes.QuoteCache
import arrow.meta.ide.plugins.quotes.isMetaSynthetic
import arrow.meta.quotes.get
import arrow.meta.quotes.ktClassOrObject
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.idea.stubindex.resolve.StubBasedPackageMemberDeclarationProvider
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
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
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

open class MetaSyntheticResolveExtension(project: Project) : SyntheticResolveExtension {
  val quoteCache: QuoteCache? = project.getService(QuoteCache::class.java)

  override fun generateSyntheticClasses(thisDescriptor: PackageFragmentDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      result.replaceWithSynthetics(thisDescriptor.fqName, declarationProvider)
      quoteCache?.descriptors(thisDescriptor.fqName)?.filterIsInstance<LazyClassDescriptor>()?.filter { !it.isMetaSynthetic() }?.let {
        LOG.debug("generatePackageSyntheticClasses: ${thisDescriptor.fqName}: $it, $name, result: $result")
        result.addAll(it.toSynthetic(declarationProvider))
      }
    }
  }

  override fun generateSyntheticClasses(thisDescriptor: ClassDescriptor, name: Name, ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider, result: MutableSet<ClassDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        result.replaceWithSynthetics(packageName, declarationProvider)
        quoteCache?.descriptors(packageName)?.filterIsInstance<LazyClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthNestedClasses = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<LazyClassDescriptor>()
          LOG.debug("generateNestedSyntheticClasses: ${thisDescriptor.name}: $synthNestedClasses")
          result.addAll(synthNestedClasses.toSynthetic(declarationProvider))
        }
      }
    }
  }

  override fun generateSyntheticMethods(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: List<SimpleFunctionDescriptor>, result: MutableCollection<SimpleFunctionDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        //result.replaceWithSynthetics(packageName)
        quoteCache?.descriptors(packageName)?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthMemberFunctions = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<SimpleFunctionDescriptor>()
          LOG.debug("generateSyntheticMethods: ${thisDescriptor.name}: $synthMemberFunctions")
          result.addAll(synthMemberFunctions.toSynthetic())
        }
      }
    }
  }

  override fun generateSyntheticProperties(thisDescriptor: ClassDescriptor, name: Name, bindingContext: BindingContext, fromSupertypes: ArrayList<PropertyDescriptor>, result: MutableSet<PropertyDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        quoteCache?.descriptors(packageName)?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val syntMemberProperties = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filter { it.isMetaSynthetic() }.filterIsInstance<PropertyDescriptor>()
          LOG.debug("generateSyntheticProperties: ${thisDescriptor.name}: $syntMemberProperties")
          result.addAll(syntMemberProperties)
        }
      }
    }
  }

  override fun generateSyntheticSecondaryConstructors(thisDescriptor: ClassDescriptor, bindingContext: BindingContext, result: MutableCollection<ClassConstructorDescriptor>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        quoteCache?.descriptors(packageName)?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthSecConstructors = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<ClassConstructorDescriptor>().filter { !it.isPrimary && it.isMetaSynthetic() }
          LOG.debug("generateSyntheticSecondaryConstructors: ${thisDescriptor.name}: $synthSecConstructors")
          result.addAll(synthSecConstructors)
        }
      }
    }
  }

  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
    thisDescriptor
      .takeIf { !it.isMetaSynthetic() }?.findPsi()?.safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName: FqName ->
      quoteCache?.descriptors(packageName)?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor: ClassDescriptor ->
        val name = classDescriptor.companionObjectDescriptor?.name
        LOG.debug("getSyntheticCompanionObjectNameIfNeeded: ${thisDescriptor.name}: $name")
        name
      }
    }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    thisDescriptor
      .takeIf { !it.isMetaSynthetic() }
      ?.findPsi()
      ?.safeAs<KtClassOrObject>()
      ?.containingKtFile?.packageFqName?.let { packageName: FqName ->
      quoteCache?.descriptors(packageName)
        ?.filterIsInstance<ClassDescriptor>()
        ?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { descriptor: ClassDescriptor ->
          val synthFunctionNames = descriptor.unsubstitutedMemberScope.getContributedDescriptors { true }
            .filterIsInstance<SimpleFunctionDescriptor>()
            .filter { it.isMetaSynthetic() }
            .map { it.name }
          LOG.debug("getSyntheticFunctionNames: ${thisDescriptor.name}: $synthFunctionNames")
          synthFunctionNames
        }
    } ?: emptyList()

  override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.findPsi().safeAs<KtClassOrObject>()?.containingKtFile?.packageFqName?.let { packageName ->
        quoteCache?.descriptors(packageName)?.filterIsInstance<ClassDescriptor>()?.find { it.fqNameSafe == thisDescriptor.fqNameSafe }?.let { classDescriptor ->
          val synthClassNames = classDescriptor.unsubstitutedMemberScope.getContributedDescriptors { true }.filterIsInstance<ClassDescriptor>().filter { it.isMetaSynthetic() }.map { it.name }
          LOG.debug("getSyntheticFunctionNames: ${thisDescriptor.name}: $synthClassNames")
          synthClassNames
        }
      } ?: emptyList()
    } else emptyList()

  override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
    if (!thisDescriptor.isMetaSynthetic()) {
      thisDescriptor.ktClassOrObject()?.containingKtFile?.packageFqName?.let { packageName ->
        quoteCache?.descriptors(packageName)
          ?.filterIsInstance<ClassDescriptor>()
          ?.find { thisDescriptor.fqNameSafe == it.fqNameSafe }
          ?.typeConstructor?.supertypes?.let { collection ->
          //supertypes.clear()
          val result = collection.filterNot { it.isError }
          LOG.debug("Found synth supertypes: $result")
          supertypes.addAll(result)
        }
      }
    }
  }

  private fun MutableSet<ClassDescriptor>.replaceWithSynthetics(packageName: FqName, declarationProvider: DeclarationProvider): Unit {
    val replacements = map { existing ->
      val synthDescriptors = quoteCache?.descriptors(packageName)?.filterIsInstance<LazyClassDescriptor>()
      val replacement = synthDescriptors?.find { synth -> synth.fqNameOrNull() == existing.fqNameOrNull() }
      existing to replacement
    }.toMap()
    removeIf { replacements[it] != null }
    addAll(replacements.values.filterNotNull().toSynthetic(declarationProvider))
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
  }
*/

  private fun SimpleFunctionDescriptor.synthetic(): SimpleFunctionDescriptor =
    copy(
      containingDeclaration,
      modality,
      if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
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
      visibility = if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
      annotations = annotations,
      constructorVisibility = Visibilities.PUBLIC,
      kind = kind,
      isCompanionObject = isCompanionObject
    ).also {
      it.initialize(declaredTypeParameters)
    }
  }

  private fun List<LazyClassDescriptor>.toSynthetic(declarationProvider: DeclarationProvider): List<ClassDescriptor> =
    map { it.synthetic(declarationProvider) }

  private fun List<SimpleFunctionDescriptor>.toSynthetic(): List<SimpleFunctionDescriptor> =
    map { it.synthetic() }

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
}
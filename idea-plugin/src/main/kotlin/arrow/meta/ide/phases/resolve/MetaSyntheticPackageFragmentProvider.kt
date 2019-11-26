package arrow.meta.ide.phases.resolve

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotations
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.Printer

class MetaSyntheticPackageFragmentProvider(project: Project) : PackageFragmentProviderExtension, Disposable {
  companion object {
    fun getInstance(project: Project): MetaSyntheticPackageFragmentProvider? =
      PackageFragmentProviderExtension.getInstances(project)
        .filterIsInstance<MetaSyntheticPackageFragmentProvider>()
        .firstOrNull()
  }

  private val cache = QuoteSystemCache.getInstance(project)

  override fun getPackageFragmentProvider(
    project: Project,
    module: ModuleDescriptor,
    storageManager: StorageManager,
    trace: BindingTrace,
    moduleInfo: ModuleInfo?,
    lookupTracker: LookupTracker
  ): PackageFragmentProvider? =
    DescriptorCachePackageFragmentProvider(module)

  inner class DescriptorCachePackageFragmentProvider(private val module: ModuleDescriptor) : PackageFragmentProvider {
    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
      cache.packageList().map { packageName ->
        // fixme return only elements which are in package fqName?
        BuildCachePackageFragmentDescriptor(module, packageName)
      }

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
      getPackageFragments(fqName).map { it.fqName }
  }

  inner class BuildCachePackageFragmentDescriptor(module: ModuleDescriptor, fqName: FqName) : PackageFragmentDescriptorImpl(module, fqName) {
    override fun getMemberScope(): MemberScope = scope

    private val scope = Scope()

    inner class Scope : MemberScope {
      override fun getClassifierNames(): Set<Name>? =
        cache.resolved(fqName)?.filterIsInstance<ClassifierDescriptor>()?.map { it.name }?.toSet()

      override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
        cache.resolved(fqName)?.filterIsInstance<ClassifierDescriptor>()?.firstOrNull { it.name == name }

      override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
        cache.resolved(fqName)?.filter { it.name == name } ?: emptyList()

      override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
        cache.resolved(fqName)?.filterIsInstance<SimpleFunctionDescriptor>() ?: emptyList()

      override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
        cache.resolved(fqName)?.filterIsInstance<PropertyDescriptor>() ?: emptyList()

      override fun getFunctionNames(): Set<Name> =
        cache.resolved(fqName)?.filterIsInstance<SimpleFunctionDescriptor>()?.map { it.name }?.toSet() ?: emptySet()

      override fun getVariableNames(): Set<Name> =
        cache.resolved(fqName)?.filterIsInstance<PropertyDescriptor>()?.map { it.name }?.toSet() ?: emptySet()

      override fun printScopeStructure(p: Printer) {
      }
    }
  }

  override fun dispose() {}
}

internal fun DeclarationDescriptor.isMetaSynthetic(): Boolean {
  return this.annotations.any {
    when (it) {
      is LazyAnnotationDescriptor -> it.annotationEntry.textMatches("@arrow.synthetic")
      else -> it.fqName == FqName("arrow.synthetic")
    }
  }
}


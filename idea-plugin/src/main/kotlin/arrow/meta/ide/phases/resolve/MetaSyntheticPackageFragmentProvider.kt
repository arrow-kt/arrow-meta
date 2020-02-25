package arrow.meta.ide.phases.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyAnnotationDescriptor
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.utils.Printer

val IdeMetaPlugin.metaSyntheticPackageFragmentProvider: ExtensionPhase
  get() = packageFragmentProvider { project, module, _, _, _, _ ->
    descriptorCachePackageFragmentProvider(module, project)
  }

private val descriptorCachePackageFragmentProvider: (ModuleDescriptor, Project) -> PackageFragmentProvider
  get() = { module, project ->
    val cache = QuoteSystemCache.getInstance(project)
    object : PackageFragmentProvider {
      override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
        // fixme always provide a value or only when a cached value exists?
        return listOf(buildCachePackageFragmentDescriptor(module, fqName, cache))
      }

      override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> {
        //fixme optimize this
        return cache.packages().filter { it.parent() == fqName }
      }
    }
  }


private val buildCachePackageFragmentDescriptor: (ModuleDescriptor, FqName, QuoteSystemCache) -> PackageFragmentDescriptorImpl
  get() = { module, fqName, cache ->
    object : PackageFragmentDescriptorImpl(module, fqName) {
      override fun getMemberScope(): MemberScope =
        object : MemberScope {
          override fun getClassifierNames(): Set<Name> =
            cache.resolved(fqName).filterIsInstance<ClassifierDescriptor>().map { it.name }.toSet()

          override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
            cache.resolved(fqName).filterIsInstance<ClassifierDescriptor>().firstOrNull { it.name == name }

          override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
            cache.resolved(fqName).filter { it.name == name }

          override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
            cache.resolved(fqName).filterIsInstance<SimpleFunctionDescriptor>()

          override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
            cache.resolved(fqName).filterIsInstance<PropertyDescriptor>()

          override fun getFunctionNames(): Set<Name> =
            cache.resolved(fqName).filterIsInstance<SimpleFunctionDescriptor>().map { it.name }.toSet()

          override fun getVariableNames(): Set<Name> =
            cache.resolved(fqName).filterIsInstance<PropertyDescriptor>().map { it.name }.toSet()

          override fun printScopeStructure(p: Printer) {
          }
        }
    }
  }


internal fun DeclarationDescriptor.isMetaSynthetic(): Boolean {
  return this.annotations.any {
    when (it) {
      is LazyAnnotationDescriptor -> it.annotationEntry.textMatches("@arrow.synthetic")
      else -> it.fqName == FqName("arrow.synthetic")
    }
  }
}


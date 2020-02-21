package arrow.meta.ide.phases.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.Disposable
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
  get() = addPackageFragmentProvider(
    provider = { project, module, _, _, _, _ ->
      descriptorCachePackageFragmentProvider(module, project)
    }
  )

private val descriptorCachePackageFragmentProvider: (ModuleDescriptor, Project) -> PackageFragmentProvider
  get() = { module, project ->
    object : PackageFragmentProvider, Disposable {
      override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
        // fixme always provide a value or only when a cached value exists?
        return listOf(BuildCachePackageFragmentDescriptor(module, fqName, project))
      }

      override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> {
        //fixme optimize this
        return project.quoteCache()?.packages()?.filter { it.parent() == fqName } ?: emptyList()
      }

      override fun dispose() {}
    }
  }

private class BuildCachePackageFragmentDescriptor(module: ModuleDescriptor, fqName: FqName, project: Project) : PackageFragmentDescriptorImpl(module, fqName) {
  override fun getMemberScope(): MemberScope = scope

  private val scope = Scope(project)

  inner class Scope(val project: Project) : MemberScope {
    override fun getClassifierNames(): Set<Name>? =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<ClassifierDescriptor>()?.map { it.name }?.toSet()

    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<ClassifierDescriptor>()?.firstOrNull { it.name == name }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
      project.quoteCache()?.descriptors(fqName)?.filter { it.name == name } ?: emptyList()

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<SimpleFunctionDescriptor>() ?: emptyList()

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<PropertyDescriptor>() ?: emptyList()

    override fun getFunctionNames(): Set<Name> =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<SimpleFunctionDescriptor>()?.map { it.name }?.toSet()
        ?: emptySet()

    override fun getVariableNames(): Set<Name> =
      project.quoteCache()?.descriptors(fqName)?.filterIsInstance<PropertyDescriptor>()?.map { it.name }?.toSet()
        ?: emptySet()

    override fun printScopeStructure(p: Printer) {
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

private fun Project.quoteCache(): QuoteCache? =
  getService(QuoteSystemService::class.java)?.cache
package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.resolve.QuoteSystemComponent
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
    val quoteSystem: QuoteSystemComponent? = project.getComponent(QuoteSystemComponent::class.java)
    object : PackageFragmentProvider {
      // fixme always provide a value or only when a cached value exists?
      override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
        quoteSystem?.run { listOf(buildCachePackageFragmentDescriptor(module, fqName, this)) }.orEmpty()

      //fixme optimize this
      override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
        quoteSystem?.cache?.packages()?.filter { it.parent() == fqName }.orEmpty()
    }
  }


private val buildCachePackageFragmentDescriptor: (ModuleDescriptor, FqName, QuoteSystemComponent) -> PackageFragmentDescriptorImpl
  get() = { module, fqName, quoteSystem ->
    object : PackageFragmentDescriptorImpl(module, fqName) {
      override fun getMemberScope(): MemberScope =
        object : MemberScope {
          override fun getClassifierNames(): Set<Name> =
            quoteSystem.descriptors(fqName).filterIsInstance<ClassifierDescriptor>().map { it.name }.toSet()

          override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
            quoteSystem.descriptors(fqName).filterIsInstance<ClassifierDescriptor>().firstOrNull { it.name == name }

          override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> =
            quoteSystem.descriptors(fqName).filter { it.name == name }

          override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> =
            quoteSystem.descriptors(fqName).filterIsInstance<SimpleFunctionDescriptor>()

          override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
            quoteSystem.descriptors(fqName).filterIsInstance<PropertyDescriptor>()

          override fun getFunctionNames(): Set<Name> =
            quoteSystem.descriptors(fqName).filterIsInstance<SimpleFunctionDescriptor>().map { it.name }.toSet()

          override fun getVariableNames(): Set<Name> =
            quoteSystem.descriptors(fqName).filterIsInstance<PropertyDescriptor>().map { it.name }.toSet()

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


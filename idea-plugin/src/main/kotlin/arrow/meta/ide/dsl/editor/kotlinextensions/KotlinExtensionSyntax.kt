package arrow.meta.ide.dsl.editor.kotlinextensions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType

interface KotlinExtensionSyntax {
  fun IdeMetaPlugin.addKotlinIndicesHelper(
    appendExtensionCallables: (consumer: MutableList<in CallableDescriptor>,
                               moduleDescriptor: ModuleDescriptor,
                               receiverTypes: Collection<KotlinType>,
                               nameFilter: (String) -> Boolean,
                               lookupLocation: LookupLocation) -> Unit = Noop.effect5
  ): ExtensionPhase =
    extensionProvider(
      KotlinIndicesHelperExtension.extensionPointName,
      ktIndicesHelperExtension(appendExtensionCallables)
    )

  fun KotlinExtensionSyntax.ktIndicesHelperExtension(
    appendExtensionCallables: (consumer: MutableList<in CallableDescriptor>,
                               moduleDescriptor: ModuleDescriptor,
                               receiverTypes: Collection<KotlinType>,
                               nameFilter: (String) -> Boolean,
                               lookupLocation: LookupLocation) -> Unit = Noop.effect5
  ): KotlinIndicesHelperExtension =
    object : KotlinIndicesHelperExtension {
      /**
       * This method is deprecated, even though it is required to be implemented, and won't be called at RunTime
       */
      override fun appendExtensionCallables(consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean): Unit =
        Unit

      override fun appendExtensionCallables(consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean, lookupLocation: LookupLocation): Unit =
        appendExtensionCallables(consumer, moduleDescriptor, receiverTypes, nameFilter, lookupLocation)
    }

  fun IdeMetaPlugin.registerPackageFragmentProvider(packageFragmentProvider: PackageFragmentProviderExtension): ExtensionPhase =
    extensionProvider(PackageFragmentProviderExtension.extensionPointName, packageFragmentProvider)

  fun IdeMetaPlugin.addPackageFragmentProvider(
    provider: (project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker) -> PackageFragmentProvider?,
    dispose: () -> Unit = Noop.effect0
  ): ExtensionPhase =
    registerPackageFragmentProvider(packageFragmentProviderExtension(provider, dispose))

  fun KotlinExtensionSyntax.packageFragmentProviderExtension(
    provider: (project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker) -> PackageFragmentProvider?,
    dispose: () -> Unit = Noop.effect0
  ): PackageFragmentProviderExtension =
    object : PackageFragmentProviderExtension, Disposable {
      override fun getPackageFragmentProvider(project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker): PackageFragmentProvider? =
        provider(project, module, storageManager, trace, moduleInfo, lookupTracker)

      override fun dispose(): Unit =
        dispose()
    }

  fun KotlinExtensionSyntax.packageFragmentProvider(
    packageFragments: (FqName) -> List<PackageFragmentDescriptor>,
    subPackagesOf: (fqName: FqName, nameFilter: (Name) -> Boolean) -> List<FqName>
  ): PackageFragmentProvider =
    object : PackageFragmentProvider {
      override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
        packageFragments(fqName)

      override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): List<FqName> =
        subPackagesOf(fqName, nameFilter)
    }
}
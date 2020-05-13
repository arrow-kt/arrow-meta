package arrow.meta.ide.dsl.editor.kotlinextensions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.incremental.components.LookupLocation
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
}
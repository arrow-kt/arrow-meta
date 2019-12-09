package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.toSynthetic
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.extensionCallables
import arrow.meta.proofs.extensions
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.types.KotlinType

class ProofsKotlinIndicesHelperExtension : KotlinIndicesHelperExtension {
  override fun appendExtensionCallables(
    consumer: MutableList<in CallableDescriptor>,
    moduleDescriptor: ModuleDescriptor,
    receiverTypes: Collection<KotlinType>,
    nameFilter: (String) -> Boolean
  ) {
    TODO()
  }

  override fun appendExtensionCallables(
    consumer: MutableList<in CallableDescriptor>,
    moduleDescriptor: ModuleDescriptor,
    receiverTypes: Collection<KotlinType>,
    nameFilter: (String) -> Boolean,
    lookupLocation: LookupLocation
  ) {
    Log.Verbose({ "ProofsKotlinIndicesHelperExtension.appendExtensionCallables($receiverTypes), result: $this" }) {
      val extensions = moduleDescriptor.typeProofs.extensions(receiverTypes)
      val extensionCallables = extensions.flatMap {
        it.extensionCallables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
          .filter { it.isExtension }
          .mapNotNull { it.newCopyBuilder()
            .setModality(Modality.FINAL)
            .build() }
          .toSynthetic()
      }
      consumer.addAll(extensionCallables)
      extensionCallables
    }
  }
}
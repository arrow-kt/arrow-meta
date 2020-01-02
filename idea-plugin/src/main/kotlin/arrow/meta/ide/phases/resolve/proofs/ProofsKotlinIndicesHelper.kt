package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.resolve.scopes.toSynthetic
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.callables
import arrow.meta.plugins.proofs.phases.extending
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.idea.core.extension.KotlinIndicesHelperExtension
import org.jetbrains.kotlin.incremental.components.LookupLocation
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
      val extensions = moduleDescriptor.proofs.extending(receiverTypes)
      val extensionCallables = extensions.flatMap {
        it.callables { true }
          .filterIsInstance<SimpleFunctionDescriptor>()
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
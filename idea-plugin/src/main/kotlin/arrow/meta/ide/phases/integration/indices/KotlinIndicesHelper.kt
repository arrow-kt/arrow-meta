package arrow.meta.ide.phases.integration.indices

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.types.KotlinType

interface KotlinIndicesHelper : ExtensionPhase {
  fun CompilerContext.appendExtensionCallables(
    project: Project,
    consumer: MutableList<in CallableDescriptor>,
    moduleDescriptor: ModuleDescriptor,
    receiverTypes: Collection<KotlinType>,
    nameFilter: (String) -> Boolean,
    lookupLocation: LookupLocation
  ): Unit
}
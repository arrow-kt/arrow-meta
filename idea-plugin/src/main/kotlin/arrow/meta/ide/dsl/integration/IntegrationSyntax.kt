package arrow.meta.ide.dsl.integration

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.integration.PackageProvider
import arrow.meta.ide.phases.integration.SyntheticResolver
import arrow.meta.ide.phases.integration.indices.KotlinIndicesHelper
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.types.KotlinType

/**
 * [IntegrationSyntax] servers as an interoperability algebra to register compiler extensions and kotlin extensions regarding the Ide.
 */
interface IntegrationSyntax {
  fun IdeMetaPlugin.syntheticResolver(
    f: (Project) -> arrow.meta.phases.resolve.synthetics.SyntheticResolver?
  ): ExtensionPhase =
    object : SyntheticResolver {
      override fun syntheticResolver(project: Project): arrow.meta.phases.resolve.synthetics.SyntheticResolver? =
        f(project)
    }

  fun IdeMetaPlugin.packageFragmentProvider(
    f: (Project) -> arrow.meta.phases.resolve.PackageProvider?
  ): ExtensionPhase =
    object : PackageProvider {
      override fun packageFragmentProvider(project: Project): arrow.meta.phases.resolve.PackageProvider? =
        f(project)
    }

  fun IdeMetaPlugin.kotlinIndices(
    appendExtensionCallables:
    CompilerContext.(
      project: Project,
      consumer: MutableList<in CallableDescriptor>,
      moduleDescriptor: ModuleDescriptor,
      receiverTypes: Collection<KotlinType>,
      nameFilter: (String) -> Boolean,
      lookupLocation: LookupLocation
    ) -> Unit
  ): ExtensionPhase =
    object : KotlinIndicesHelper {
      override fun CompilerContext.appendExtensionCallables(project: Project, consumer: MutableList<in CallableDescriptor>, moduleDescriptor: ModuleDescriptor, receiverTypes: Collection<KotlinType>, nameFilter: (String) -> Boolean, lookupLocation: LookupLocation): Unit =
        appendExtensionCallables(project, consumer, moduleDescriptor, receiverTypes, nameFilter, lookupLocation)
    }
}
package arrow.meta.dsl.config

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.config.Config
import arrow.meta.dsl.platform.cli
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

interface ConfigSyntax {

  /**
   * The [updateConfig] function provides access to the [CompilerConfiguration] which contains the map of properties used
   * to enable/disable the different features in compilation.
   *
   * @updateConfiguration enables a DSL using [CompilerContext] and it can update [CompilerConfiguration] through it mutable API.
   * @return [Config] [ExtensionPhase].
   */
  fun updateConfig(updateConfiguration: CompilerContext.(configuration: CompilerConfiguration) -> Unit): Config =
    object : Config {
      override fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration) {
        updateConfiguration(configuration)
      }
    }

  /**
   * The [storageComponent] function allows access to the [StorageComponentContributor].
   * This is the Dependency Injector and service registry the compiler uses in all phases.
   * In this function you can register new services or modify existing ones before the container is composed and sealed prior to compilation.
   *
   * @registerModuleComponents enables a DSL using [CompilerContext] and it can update the [StorageComponentContainer] and [ModuleDescriptor] to
   * update the DI configuration of the compiler.
   */
  fun storageComponent(
    registerModuleComponents: CompilerContext.(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) -> Unit,
    check: CompilerContext.(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) -> Unit
  ): arrow.meta.phases.config.StorageComponentContainer =
    object : arrow.meta.phases.config.StorageComponentContainer {
      override fun CompilerContext.check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
      ) {
        check(declaration, descriptor, context)
      }

      override fun CompilerContext.registerModuleComponents(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) {
        registerModuleComponents(container, moduleDescriptor)
      }
    }

  fun enableIr(): ExtensionPhase =
    cli {
      updateConfig { configuration ->
        configuration.put(JVMConfigurationKeys.IR, true)
      }
    } ?: ExtensionPhase.Empty

}

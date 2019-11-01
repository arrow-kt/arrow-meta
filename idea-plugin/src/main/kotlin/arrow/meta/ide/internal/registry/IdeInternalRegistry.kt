package arrow.meta.ide.internal.registry

import arrow.meta.dsl.platform.ide
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.ExtensionPhase
import arrow.meta.ide.phases.analysis.MetaIdeAnalyzer
import org.jetbrains.kotlin.container.useImpl

internal interface IdeInternalRegistry : InternalRegistry {

  override fun registerMetaAnalyzer(): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          //println("Registering meta analyzer")
          container.useImpl<MetaIdeAnalyzer>()
          //
        },
        check = { declaration, descriptor, context ->
        }
      )
    } ?: ExtensionPhase.Empty

}

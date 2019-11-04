package arrow.meta.ide.internal.registry

import arrow.meta.dsl.platform.ide
import arrow.meta.ide.phases.analysis.MetaIdeAnalyzer
import arrow.meta.ide.phases.editor.ExtensionProvider2
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.extensions.Extensions
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

  override fun CompilerContext.registerIdeExclusivePhase(currentPhase: ExtensionPhase) =
    when (currentPhase) {
      is ExtensionProvider2 -> registerExtensionProvider2
    }


  fun registerExtensionProvider2(phase: ExtensionProvider2): Unit =
    Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(phase.addExtension()
}

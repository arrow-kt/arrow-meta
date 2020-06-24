package arrow.meta.plugins.proofs.phases.config

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.resolve.ProofsCallResolver
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.container.useInstance

fun Meta.enableProofCallResolver(): ExtensionPhase =
  storageComponent(
    registerModuleComponents = { container, moduleDescriptor ->
      println("Replacing ${ctx.module} for $moduleDescriptor")
      ctx.module = moduleDescriptor
      container.useInstance(container)
      println("Replacing ${ctx.componentProvider} for $container if ctx.componentProvider is null: $ctx.componentProvider")
      cli {
        // TODO: should we check for ctx.componentProvider == null ?
        //if (ctx.componentProvider == null) {
          println("Replacing ${ctx.componentProvider} for $container")
          ctx.componentProvider = container
          container.useImpl<ProofsCallResolver>()
       // }
      }
      ide {
        if (ctx.componentProvider == null) {
          println("Replacing ${ctx.componentProvider} for $container")
          ctx.componentProvider = container
          container.useImpl<ProofsCallResolver>()
        }
      }
    }
  )
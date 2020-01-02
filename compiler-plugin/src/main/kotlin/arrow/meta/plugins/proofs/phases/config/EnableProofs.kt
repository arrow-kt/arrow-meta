package arrow.meta.plugins.proofs.phases.config

import arrow.meta.Meta
import arrow.meta.dsl.platform.cli
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.resolve.ProofsCallResolver
import org.jetbrains.kotlin.container.useImpl

fun Meta.enableProofCallResolver(): ExtensionPhase =
  storageComponent(
    registerModuleComponents = { container, moduleDescriptor ->
      println("Replacing ${ctx.module} for $moduleDescriptor")
      ctx.module = moduleDescriptor
      println("Replacing ${ctx.componentProvider} for $container")
      if (cli { true } == true || ctx.componentProvider == null) {
        ctx.componentProvider = container
        container.useImpl<ProofsCallResolver>()
      }
    }
  )
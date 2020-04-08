package arrow.meta.ide.plugins.proofs.lifecycle

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.resolve.ProofsKotlinCacheServiceHelper
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.resolve.cache.disposeProofCache
import com.intellij.openapi.project.Project
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.proofsLifecycle: ExtensionPhase
  get() = addProjectLifecycle(
    initialize = { project ->
      Log.Verbose({ "MetaKotlinCacheService.initComponent" }) {
        project.replaceKotlinCacheService {
          ProofsKotlinCacheServiceHelper(KotlinCacheService.getInstance(project))
        }
      }
    },
    afterProjectClosed = { project ->
      Log.Verbose({ "MetaKotlinCacheService.disposeComponent" }) {
        disposeProofCache()
        //project.replaceKotlinCacheService { KotlinCacheService.getInstance(project) } no need to replace the cache as this Listener will be disposed automatically
      }
    }
  )


private inline fun Project.replaceKotlinCacheService(f: (KotlinCacheService) -> KotlinCacheService): Unit {
  picoContainer.safeAs<DefaultPicoContainer>()?.apply {
    getComponentAdapterOfType(KotlinCacheService::class.java)?.apply {
      val instance = getComponentInstance(componentKey) as? KotlinCacheService
      if (instance != null) {
        val newInstance = f(instance)
        unregisterComponent(componentKey)
        registerServiceInstance(KotlinCacheService::class.java, newInstance)
      }
    }
  }
}

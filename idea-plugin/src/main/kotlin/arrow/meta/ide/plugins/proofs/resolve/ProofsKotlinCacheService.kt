package arrow.meta.ide.plugins.proofs.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.resolve.cache.disposeProofCache
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

private class ProofsKotlinCacheServiceHelper(private val delegate: KotlinCacheService) : KotlinCacheService by delegate {
  override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade =
    Log.Silent({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $this" }) {
      delegate.getResolutionFacade(elements).metaResolutionFacade()
    }

  private fun ResolutionFacade.metaResolutionFacade(): ResolutionFacade {
    return MetaResolutionFacade(this)
  }

  override fun getResolutionFacade(elements: List<KtElement>, platform: TargetPlatform): ResolutionFacade =
    Log.Silent({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $platform $this" }) {
      delegate.getResolutionFacade(elements, platform).metaResolutionFacade()
    }

  override fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade? =
    Log.Silent({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByFile $file $platform $this" }) {
      delegate.getResolutionFacadeByFile(file, platform)?.metaResolutionFacade()
    }

  override fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade? =
    Log.Silent({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByModuleInfo $moduleInfo $platform $this" }) {
      delegate.getResolutionFacadeByModuleInfo(moduleInfo, platform)?.metaResolutionFacade()
    }

  override fun getSuppressionCache(): KotlinSuppressCache =
    delegate.getSuppressionCache()

}

class ProofsKotlinCacheService(val project: Project) : ProjectComponent {

  val delegate: KotlinCacheService = KotlinCacheService.getInstance(project)

  override fun initComponent() {
    Log.Verbose({ "MetaKotlinCacheService.initComponent" }) {
      project.replaceKotlinCacheService {
        ProofsKotlinCacheServiceHelper(delegate)
      }
    }
  }

  override fun disposeComponent() {
    Log.Verbose({ "MetaKotlinCacheService.disposeComponent" }) {
      disposeProofCache()
      project.replaceKotlinCacheService { delegate }
    }
  }

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

}

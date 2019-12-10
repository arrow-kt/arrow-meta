package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.disposeProofCache
import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.phases.resolve.proofCache
import arrow.meta.phases.resolve.typeProofs
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

private class MetaKotlinCacheServiceHelper(private val delegate: KotlinCacheService) : KotlinCacheService by delegate {
  override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $this" }) {
      val resolutionFacade = delegate.getResolutionFacade(elements)
      val module = resolutionFacade.moduleDescriptor
      val cachedModule = proofCache[module.name]?.first
      if (cachedModule == null || cachedModule != module) {
        Log.Verbose({ "MetaKotlinCacheServiceHelper.initializeProofCache ${resolutionFacade.moduleDescriptor} ${this.size}" }) {
          resolutionFacade.moduleDescriptor.initializeProofCache()
        }
      }
      resolutionFacade
    }

  override fun getResolutionFacade(elements: List<KtElement>, platform: TargetPlatform): ResolutionFacade =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $platform $this" }) {
      delegate.getResolutionFacade(elements, platform)
    }

  override fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade? =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByFile $file $platform $this" }) {
      delegate.getResolutionFacadeByFile(file, platform)
    }

  override fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade? =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByModuleInfo $moduleInfo $platform $this" }) {
      delegate.getResolutionFacadeByModuleInfo(moduleInfo, platform)
    }

  override fun getSuppressionCache(): KotlinSuppressCache =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getSuppressionCache $this" }) {
      delegate.getSuppressionCache()
    }

}

class MetaKotlinCacheService(val project: Project) : ModuleComponent {

  val delegate: KotlinCacheService = KotlinCacheService.getInstance(project)

  override fun initComponent() {
    Log.Verbose({ "MetaKotlinCacheService.initComponent" }) {
      project.replaceKotlinCacheService { MetaKotlinCacheServiceHelper(delegate) }
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

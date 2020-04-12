package arrow.meta.ide.plugins.purity

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache

val IdeMetaPlugin.kotlinCacheLogger: IdePlugin
  get() = "KotlinCacheLogger" {
    meta(
      addProjectService(KotlinCacheService::class.java) { project, kotlinCache ->
        kotlinCache?.let(::logKotlinCache)
      }
    )
  }

//sampleStart
fun logKotlinCache(delegate: KotlinCacheService): KotlinCacheService =
  object : KotlinCacheService by delegate {
    override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade {
      println("Meaningful Log message for $elements")
      return delegate.getResolutionFacade(elements)
    }

    override fun getResolutionFacade(elements: List<KtElement>, platform: TargetPlatform): ResolutionFacade {
      println("Meaningful Log message for $elements based on target:$platform")
      return delegate.getResolutionFacade(elements, platform)
    }

    override fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade? {
      println("Meaningful Log message for $file based on target:$platform")
      return delegate.getResolutionFacadeByFile(file, platform)
    }

    override fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade? {
      println("Meaningful Log message for module ${moduleInfo.name} based on target:$platform")
      return delegate.getResolutionFacadeByModuleInfo(moduleInfo, platform)
    }

    override fun getSuppressionCache(): KotlinSuppressCache {
      println("Meaningful Log message for KotlinSuppressCache")
      return delegate.getSuppressionCache()
    }
  }

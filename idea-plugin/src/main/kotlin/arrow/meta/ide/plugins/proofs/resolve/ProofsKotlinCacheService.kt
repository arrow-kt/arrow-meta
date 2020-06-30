package arrow.meta.ide.plugins.proofs.resolve

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache

val IdeMetaPlugin.proofsKotlinCache: ExtensionPhase
  get() = addProjectService(KotlinCacheService::class.java) { p, service ->
    p.getService(CompilerContext::class.java)?.run {
      service?.let(::proofsKotlinCacheService)
    }
  }

private fun CompilerContext.proofsKotlinCacheService(delegate: KotlinCacheService): KotlinCacheService =
  object : KotlinCacheService by delegate {
    override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade =
      Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $this, Proof cache initialized in ide" }) {
        val facade = delegate.getResolutionFacade(elements)
        Log.Verbose({ "Initialized proof cache in IDE: $this" }) {
          if (facade.moduleDescriptor.proofs.isEmpty())
            facade.moduleDescriptor.initializeProofCache(this@proofsKotlinCacheService)
        }
        facade.metaResolutionFacade()
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

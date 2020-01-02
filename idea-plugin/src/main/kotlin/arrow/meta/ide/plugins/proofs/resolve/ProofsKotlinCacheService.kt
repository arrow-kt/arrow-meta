package arrow.meta.ide.plugins.proofs.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.resolve.cache.disposeProofCache
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import arrow.meta.plugins.proofs.phases.resolve.cache.proofCache
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.diagnostics.KotlinSuppressCache
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

private class ProofsKotlinCacheServiceHelper(private val delegate: KotlinCacheService) : KotlinCacheService by delegate {
  override fun getResolutionFacade(elements: List<KtElement>): ResolutionFacade =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $this" }) {
      delegate.getResolutionFacade(elements).initializeProofsIfNeeded()
    }

  private fun ResolutionFacade.initializeProofsIfNeeded(): ResolutionFacade {
    if (moduleDescriptor.proofs.isEmpty()) {
      Log.Verbose({ "MetaKotlinCacheServiceHelper.initializeProofCache $moduleDescriptor ${this.size}" }) {
        println("Current cache size: ${proofCache.size}")
        moduleDescriptor.initializeProofCache()
      }
    }
    return MetaResolutionFacade(this)
  }

  override fun getResolutionFacade(elements: List<KtElement>, platform: TargetPlatform): ResolutionFacade =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacade $elements $platform $this" }) {
      delegate.getResolutionFacade(elements, platform).initializeProofsIfNeeded()
    }

  override fun getResolutionFacadeByFile(file: PsiFile, platform: TargetPlatform): ResolutionFacade? =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByFile $file $platform $this" }) {
      delegate.getResolutionFacadeByFile(file, platform)?.initializeProofsIfNeeded()
    }

  override fun getResolutionFacadeByModuleInfo(moduleInfo: ModuleInfo, platform: TargetPlatform): ResolutionFacade? =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getResolutionFacadeByModuleInfo $moduleInfo $platform $this" }) {
      delegate.getResolutionFacadeByModuleInfo(moduleInfo, platform)?.initializeProofsIfNeeded()
    }

  override fun getSuppressionCache(): KotlinSuppressCache =
    Log.Verbose({ "MetaKotlinCacheServiceHelper.getSuppressionCache $this" }) {
      delegate.getSuppressionCache()
    }

}

class MetaResolutionFacade(val delegate: ResolutionFacade) : ResolutionFacade by delegate {

  override fun analyze(elements: Collection<KtElement>, bodyResolveMode: BodyResolveMode): BindingContext =
    Log.Verbose({ "MetaResolutionFacade.analyze" }) {
      delegate.analyze(elements, bodyResolveMode)
    }

  override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext =
    Log.Verbose({ "MetaResolutionFacade.analyze" }) {
      delegate.analyze(element, bodyResolveMode)
    }

  override fun analyzeWithAllCompilerChecks(elements: Collection<KtElement>): AnalysisResult =
    Log.Verbose({ "MetaResolutionFacade.analyzeWithAllCompilerChecks" }) {
      delegate.analyzeWithAllCompilerChecks(elements)
    }

  override fun resolveToDescriptor(declaration: KtDeclaration, bodyResolveMode: BodyResolveMode): DeclarationDescriptor =
    Log.Verbose({ "MetaResolutionFacade.resolveToDescriptor" }) {
      delegate.resolveToDescriptor(declaration, bodyResolveMode)
    }
}

class ProofsKotlinCacheService(val project: Project) : ProjectComponent {

  val delegate: KotlinCacheService = KotlinCacheService.getInstance(project)

  override fun initComponent() {
    Log.Verbose({ "MetaKotlinCacheService.initComponent" }) {
      project.replaceKotlinCacheService { ProofsKotlinCacheServiceHelper(delegate) }
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

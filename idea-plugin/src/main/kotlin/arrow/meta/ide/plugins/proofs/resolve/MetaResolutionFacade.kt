package arrow.meta.ide.plugins.proofs.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.idea.FrontendInternals
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

class MetaResolutionFacade(val delegate: ResolutionFacade) : ResolutionFacade by delegate {

  override fun analyze(elements: Collection<KtElement>, bodyResolveMode: BodyResolveMode): BindingContext =
    delegate.analyze(elements, bodyResolveMode)

  override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext =
    delegate.analyze(element, bodyResolveMode)

  override fun analyzeWithAllCompilerChecks(elements: Collection<KtElement>): AnalysisResult =
    delegate.analyzeWithAllCompilerChecks(elements)

  override fun resolveToDescriptor(declaration: KtDeclaration, bodyResolveMode: BodyResolveMode): DeclarationDescriptor =
    delegate.resolveToDescriptor(declaration, bodyResolveMode)

  @FrontendInternals
  override fun <T : Any> getFrontendService(element: PsiElement, serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $element $serviceClass" }) {
      delegate.getFrontendService(element, serviceClass)
    }

  @FrontendInternals
  override fun <T : Any> getFrontendService(serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $serviceClass" }) {
      delegate.getFrontendService(serviceClass)
    }

  @FrontendInternals
  override fun <T : Any> getFrontendService(moduleDescriptor: ModuleDescriptor, serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $moduleDescriptor $serviceClass" }) {
      delegate.getFrontendService(moduleDescriptor, serviceClass)
    }

  override fun <T : Any> getIdeService(serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getIdeService $serviceClass" }) {
      delegate.getIdeService(serviceClass)
    }

  @FrontendInternals
  override fun <T : Any> tryGetFrontendService(element: PsiElement, serviceClass: Class<T>): T? =
    Log.Verbose({ "MetaResolutionFacade.tryGetFrontendService $element $serviceClass" }) {
      delegate.tryGetFrontendService(element, serviceClass)
    }
}

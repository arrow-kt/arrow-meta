package arrow.meta.ide.plugins.proofs.resolve

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.provenIntersection
import arrow.meta.plugins.proofs.phases.resolve.ProofTypeChecker
import arrow.meta.plugins.proofs.phases.resolve.replaceTypeChecker
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.resolve.frontendService
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.calls.CallCompleter
import org.jetbrains.kotlin.resolve.calls.CallResolver
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.smartcasts.SmartCastManager
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker

class MetaResolutionFacade(val delegate: ResolutionFacade) : ResolutionFacade by delegate {

  override fun analyze(elements: Collection<KtElement>, bodyResolveMode: BodyResolveMode): BindingContext =
    delegate.analyze(elements, bodyResolveMode)

  override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext =
    delegate.analyze(element, bodyResolveMode)

  override fun analyzeWithAllCompilerChecks(elements: Collection<KtElement>): AnalysisResult =
    delegate.analyzeWithAllCompilerChecks(elements)

  override fun resolveToDescriptor(declaration: KtDeclaration, bodyResolveMode: BodyResolveMode): DeclarationDescriptor =
    delegate.resolveToDescriptor(declaration, bodyResolveMode).proofService(delegate)

  override fun <T : Any> getFrontendService(element: PsiElement, serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $element $serviceClass" }) {
      delegate.getFrontendService(element, serviceClass).proofService(delegate)
    }

  override fun <T : Any> getFrontendService(serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $serviceClass" }) {
      delegate.getFrontendService(serviceClass).proofService(delegate)
    }

  override fun <T : Any> getFrontendService(moduleDescriptor: ModuleDescriptor, serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getFrontendService $moduleDescriptor $serviceClass" }) {
      delegate.getFrontendService(moduleDescriptor, serviceClass).proofService(delegate)
    }

  override fun <T : Any> getIdeService(serviceClass: Class<T>): T =
    Log.Verbose({ "MetaResolutionFacade.getIdeService $serviceClass" }) {
      delegate.getIdeService(serviceClass).proofService(delegate)
    }

  override fun <T : Any> tryGetFrontendService(element: PsiElement, serviceClass: Class<T>): T? =
    Log.Verbose({ "MetaResolutionFacade.tryGetFrontendService $element $serviceClass" }) {
      delegate.tryGetFrontendService(element, serviceClass).proofService(delegate)
    }
}

fun <A> A.proofService(resolutionFacade: ResolutionFacade): A =
  when (this) {
    is ResolveSession -> {
      val typeCheckerField = ResolveSession::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
      val typeChecker = typeCheckerField.get(this) as NewKotlinTypeChecker
      val componentProvider: ComponentProvider = resolutionFacade.frontendService()
      if (typeChecker !is ProofTypeChecker) {
        val ctx: CompilerContext = componentProvider.get()
        typeCheckerField.set(this, ProofTypeChecker(ctx))
      }
      this
    }
    is SmartCastManager -> {
      val argumentTypeResolver = SmartCastManager::class.java.getDeclaredField("argumentTypeResolver").also { it.isAccessible = true }.get(this) as ArgumentTypeResolver
      val componentProvider: ComponentProvider = resolutionFacade.frontendService()
      val ctx: CompilerContext = componentProvider.get()
      ctx.replaceTypeChecker(argumentTypeResolver)
      this
    }
    is CallCompleter -> {
      val argumentTypeResolver = CallCompleter::class.java.getDeclaredField("argumentTypeResolver").also { it.isAccessible = true }.get(this) as ArgumentTypeResolver
      val componentProvider: ComponentProvider = resolutionFacade.frontendService()
      val ctx: CompilerContext = componentProvider.get()
      ctx.replaceTypeChecker(argumentTypeResolver)
      this
    }
    is CallResolver -> {
      val argumentTypeResolver = CallResolver::class.java.getDeclaredField("argumentTypeResolver").also { it.isAccessible = true }.get(this) as ArgumentTypeResolver
      val componentProvider: ComponentProvider = resolutionFacade.frontendService()
      val ctx: CompilerContext = componentProvider.get()
      ctx.replaceTypeChecker(argumentTypeResolver)
      this
    }
    is DataFlowValueFactory -> {
      val componentProvider: ComponentProvider = resolutionFacade.frontendService()
      val ctx: CompilerContext = componentProvider.get()
      ProofsDataFlowValueFactory(ctx, this) as A
    }
    else -> this
  }

class ProofsDataFlowValueFactory(val ctx: CompilerContext, val delegate: DataFlowValueFactory) : DataFlowValueFactory by delegate {
  override fun createDataFlowValue(expression: KtExpression, type: KotlinType, bindingContext: BindingContext, containingDeclarationOrModule: DeclarationDescriptor): DataFlowValue =
    Log.Verbose({ "createDataFlowValue ${expression.text} $type = $this" }) {
     delegate.createDataFlowValue(expression, type, bindingContext, containingDeclarationOrModule).provenDataFlowValue()
    }

  private fun DataFlowValue.provenDataFlowValue(): DataFlowValue =
    DataFlowValue(identifierInfo, ctx.provenIntersection(type), immanentNullability)

  override fun createDataFlowValue(expression: KtExpression, type: KotlinType, resolutionContext: ResolutionContext<*>): DataFlowValue =
    Log.Verbose({ "createDataFlowValue ${expression.text} $type = $this" }) {
      delegate.createDataFlowValue(expression, type, resolutionContext).provenDataFlowValue()
    }


  override fun createDataFlowValue(receiverValue: ReceiverValue, bindingContext: BindingContext, containingDeclarationOrModule: DeclarationDescriptor): DataFlowValue =
    Log.Verbose({ "createDataFlowValue $receiverValue = $this" }) {
      delegate.createDataFlowValue(receiverValue, bindingContext, containingDeclarationOrModule).provenDataFlowValue()
    }

  override fun createDataFlowValue(receiverValue: ReceiverValue, resolutionContext: ResolutionContext<*>): DataFlowValue =
    Log.Verbose({ "createDataFlowValue $receiverValue = $this" }) {
      delegate.createDataFlowValue(receiverValue, resolutionContext).provenDataFlowValue()
    }

  override fun createDataFlowValueForProperty(property: KtProperty, variableDescriptor: VariableDescriptor, bindingContext: BindingContext, usageContainingModule: ModuleDescriptor?): DataFlowValue =
    Log.Verbose({ "createDataFlowValueForProperty ${property.text} = $this" }) {
      delegate.createDataFlowValueForProperty(property, variableDescriptor, bindingContext, usageContainingModule).provenDataFlowValue()
    }

  override fun createDataFlowValueForStableReceiver(receiver: ReceiverValue): DataFlowValue =
    Log.Verbose({ "createDataFlowValueForStableReceiver $receiver = $this" }) {
      delegate.createDataFlowValueForStableReceiver(receiver).provenDataFlowValue()
    }
}

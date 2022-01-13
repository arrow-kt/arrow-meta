package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CompilerMessageSourceLocation
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContextUtils
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

fun interface KotlinElement : Element {
  override fun impl(): KtElement

  override val text: String
    get() = impl().text

  override fun getResolvedCall(context: ResolutionContext): ResolvedCall? =
    (context as? KotlinResolutionContext)?.let { ctx ->
      impl().getResolvedCall(ctx.bindingContext)?.let { KotlinResolvedCall(it) }
    }

  override fun getVariableDescriptor(context: ResolutionContext): VariableDescriptor? =
    (context as? KotlinResolutionContext)?.let { ctx ->
      BindingContextUtils.extractVariableDescriptorFromReference(ctx.bindingContext, impl())
        ?.model()
    }

  override fun parents(): List<Element> =
    impl()
      .parents
      .filter { it !is KtFile && it !is KtStringTemplateEntry }
      .filterIsInstance<KtElement>()
      .toList()
      .map { it.model() }

  override fun location(): CompilerMessageSourceLocation? =
    MessageUtil.psiElementToMessageLocation(impl().psiOrParent)?.let {
      KotlinCompilerMessageSourceLocation(it)
    }

  override val psiOrParent: Element
    get() = impl().psiOrParent.model()
}

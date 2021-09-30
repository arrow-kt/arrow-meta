package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin

import arrow.meta.plugins.liquid.errors.MetaErrors
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Types
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.resolve.BindingTrace

class KotlinResolutionContext(impl: BindingTrace, moduleImpl: org.jetbrains.kotlin.descriptors.ModuleDescriptor) : ResolutionContext, BindingTrace by impl {

  override fun reportLiskovProblem(expression: Element, msg: String) {
    report(
      MetaErrors.LiskovProblem.on(expression.element(), msg)
    )
  }

  override fun reportUnsatInvariants(expression: Element, msg: String) {
    report(
      MetaErrors.UnsatInvariants.on(expression.element(), msg)
    )
  }

  override fun reportInconsistentInvariants(expression: Element, msg: String) {
    report(
      MetaErrors.InconsistentInvariants.on(expression.element(), msg)
    )
  }

  override fun reportInconsistentConditions(expression: Element, msg: String) {
    report(
      MetaErrors.InconsistentConditions.on(expression.element(), msg)
    )
  }

  override fun reportInconsistentCallPost(expression: Element, msg: String) {
    report(
      MetaErrors.InconsistentCallPost.on(expression.element(), msg)
    )
  }

  override fun reportUnsatBodyPost(declaration: Element, msg: String) {
    report(
      MetaErrors.UnsatBodyPost.on(declaration.element(), msg)
    )
  }

  override fun reportInconsistentBodyPre(declaration: Element, msg: String) {
    report(
      MetaErrors.InconsistentBodyPre.on(declaration.element(), msg)
    )
  }

  override fun reportUnsupported(expression: Element, msg: String) {
    report(
      MetaErrors.UnsupportedElement.on(expression.element(), msg)
    )
  }

  override val types: Types
    get() = TODO("Not yet implemented")

  /**
   * Recursively walks [this] element for calls to [arrow.refinement.pre] and [arrow.refinement.post]
   * that hold preconditions
   */
  override fun Element.constraintsDSLElements(): List<Element> {
    val results = hashSetOf<PsiElement>()
    val visitor = callExpressionRecursiveVisitor {
      if (it.calleeExpression?.text == "pre" ||
        it.calleeExpression?.text == "post" ||
        it.calleeExpression?.text == "require"
      ) {
        results.add(it)
      }
    }
    val psi = element<Element, KtElement>()
    psi.accept(visitor)
    psi.acceptChildren(visitor)
    return results.filterIsInstance<KtElement>().map { it.model() }
  }

  override fun type(typeReference: TypeReference?): Type? =
    typeReference?.let { (it.psiOrParent as? KtExpression)?.let { bindingContext.getType(it) } }?.let { KotlinType(it) }

  override fun reportErrorsParsingPredicate(element: Element, msg: String) {
    report(MetaErrors.ErrorParsingPredicate.on(element.element(), msg))
  }

  override fun reportUnsatCallPre(element: Element, msg: String) {
    report(
      MetaErrors.UnsatCallPre.on(element.element(), msg)
    )
  }

  override val module: ModuleDescriptor = moduleImpl.model()
}

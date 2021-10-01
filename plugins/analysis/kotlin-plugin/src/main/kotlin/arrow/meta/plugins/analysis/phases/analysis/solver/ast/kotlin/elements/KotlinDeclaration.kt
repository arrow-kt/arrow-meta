package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parents

fun interface KotlinDeclaration : Declaration, KotlinExpression, KotlinModifierListOwner {
  override fun impl(): KtDeclaration
  override val name: String?
    get() = impl().name
  override val parents: List<Element>
    get() = impl().parents.filter { it !is KtFile }.filterIsInstance<KtElement>().toList().map { it.model() }
}

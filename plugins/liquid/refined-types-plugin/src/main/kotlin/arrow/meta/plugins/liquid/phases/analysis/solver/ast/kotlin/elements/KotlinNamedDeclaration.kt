
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.NamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration

fun interface KotlinNamedDeclaration : NamedDeclaration, KotlinDeclaration {
  override fun impl(): KtNamedDeclaration
  override val nameAsName: Name?
    get() = impl().nameAsName?.let { Name(it.asString()) }
  override val nameAsSafeName: Name
    get() = Name(impl().nameAsSafeName.asString())
  override val fqName: FqName?
    get() = impl().fqName?.let { FqName(it.asString()) }
}

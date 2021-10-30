package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtClassOrObject

fun interface KotlinClassOrObject : ClassOrObject, KotlinNamedDeclaration, KotlinPureClassOrObject {
  override fun impl(): KtClassOrObject

  override fun getAnonymousInitializers(): List<AnonymousInitializer> =
    impl().getAnonymousInitializers().map { it.model() }

  override fun isTopLevel(): Boolean = impl().isTopLevel()

  override fun isAnnotation(): Boolean = impl().isAnnotation()

  override val name: String?
    get() = impl().name

  override val psiOrParent: Element
    get() = impl().psiOrParent.model()
}

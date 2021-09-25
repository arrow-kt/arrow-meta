package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Class
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtClass

fun interface KotlinClass : Class, KotlinClassOrObject {
  override fun impl(): KtClass

  override fun getProperties(): List<Property> =
    impl().getProperties().map { it.model() }

  override fun isInterface(): Boolean =
    impl().isInterface()

  override fun isEnum(): Boolean =
    impl().isEnum()

  override fun isData(): Boolean =
    impl().isData()

  override fun isSealed(): Boolean =
    impl().isSealed()

  override fun isInner(): Boolean =
    impl().isInner()

  override fun isInline(): Boolean =
    impl().isInline()

  override fun isValue(): Boolean =
    impl().isValue()

  override fun getAnonymousInitializers(): List<AnonymousInitializer> =
    impl().getAnonymousInitializers().map { it.model() }

  override fun isTopLevel(): Boolean =
    impl().isTopLevel()

  override fun isAnnotation(): Boolean =
    impl().isAnnotation()
}

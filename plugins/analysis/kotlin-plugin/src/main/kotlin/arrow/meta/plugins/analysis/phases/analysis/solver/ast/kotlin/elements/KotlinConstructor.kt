package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Constructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtConstructor

fun interface KotlinConstructor<T : Constructor<T>> : Constructor<T>,
  KotlinFunction {
  override fun impl(): KtConstructor<*>
  override fun getContainingClassOrObject(): ClassOrObject =
    impl().getContainingClassOrObject().model()
}

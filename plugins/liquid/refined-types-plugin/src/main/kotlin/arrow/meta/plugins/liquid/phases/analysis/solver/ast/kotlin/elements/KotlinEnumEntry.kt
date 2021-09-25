package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.EnumEntry
import org.jetbrains.kotlin.psi.KtEnumEntry

fun interface KotlinEnumEntry : EnumEntry, KotlinClass {
  override fun impl(): KtEnumEntry
  override fun hasInitializer(): Boolean =
    impl().hasInitializer()
}

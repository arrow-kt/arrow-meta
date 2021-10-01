package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import org.jetbrains.kotlin.psi.KtEnumEntry

class KotlinEnumEntry(override val impl: KtEnumEntry) : EnumEntry, KotlinClass(impl) {
  override fun impl(): KtEnumEntry = impl
  override fun hasInitializer(): Boolean =
    impl().hasInitializer()
}

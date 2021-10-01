package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.EnumEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedFunction
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtClassBody

class KotlinClassBody(val impl: KtClassBody) : ClassBody, KotlinDeclarationContainer, KotlinElement {
  override fun impl(): KtClassBody = impl
  override val anonymousInitializers: List<AnonymousInitializer>
    get() = impl().anonymousInitializers.map { it.model() }
  override val properties: List<Property>
    get() = impl().properties.map { it.model() }
  override val functions: List<NamedFunction>
    get() = impl().functions.map { it.model() }
  override val enumEntries: List<EnumEntry>
    get() = impl().enumEntries.map { it.model() }
  override val allCompanionObjects: List<ObjectDeclaration>
    get() = impl().allCompanionObjects.map { it.model() }
}

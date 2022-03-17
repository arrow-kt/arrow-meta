package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ModifierListOwner
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtModifierListOwner

fun interface KotlinModifierListOwner : ModifierListOwner, KotlinAnnotated {
  override fun impl(): KtModifierListOwner

  override val modifierList: ModifierList?
    get() = impl().modifierList?.model()
}

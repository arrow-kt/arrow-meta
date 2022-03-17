package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableDeclaration
import org.jetbrains.kotlin.psi.KtVariableDeclaration

fun interface KotlinVariableDeclaration :
  CallableDeclaration, KotlinCallableDeclaration, KotlinDeclarationWithInitializer {
  override fun impl(): KtVariableDeclaration
}

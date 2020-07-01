package arrow.meta.plugins.proofs.phases.quotes

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.evaluateDependsOn
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classorobject.ObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration

internal fun ObjectDeclaration.objectWithSerializedRefinement(elementScope: ElementScope, ctx: CompilerContext): Transform<KtObjectDeclaration> =
  ctx.evaluateDependsOn(
    noRewindablePhase = { evaluatesRefinementExpression(elementScope); },
    rewindablePhase = { wasRewind -> if (wasRewind) evaluatesRefinementExpression(elementScope) else Transform.empty }
  )

private fun ObjectDeclaration.evaluatesRefinementExpression(elementScope: ElementScope): Transform<KtObjectDeclaration> {
  return elementScope.run {
    val predicateAsExpression = refinementExpression()
    return if (predicateAsExpression == null) Transform.empty
    else Transform.replace(
      value,
      "@arrow.RefinedBy(\"\"\"\n$predicateAsExpression\n\"\"\") ${this@evaluatesRefinementExpression}".`object`
    )
  }
}

fun ObjectDeclaration.refinementExpression(): String? =
  body.properties.value.find { it.name == "validate" }?.delegateExpressionOrInitializer?.text

fun KtObjectDeclaration.isRefined(): Boolean =
  isCompanion() && superTypeListEntries.any { it.text.matches("Refined<(.*?)>".toRegex()) }

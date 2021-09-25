package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors.KotlinFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.psi.KtElement


private fun <A, B> A.repr(): B =
  this as B


fun <A: org.jetbrains.kotlin.descriptors.DeclarationDescriptor,
  B: DeclarationDescriptor> A.model(): B =
  when (this) {
    is SimpleFunctionDescriptor -> KotlinFunctionDescriptor { this }.repr()
    else -> TODO("unsupported $this")
  }

fun <A: KtElement,
  B: Element> A.model(): B =
  when (this) {
    else -> TODO("unsupported $this")
  }

fun <A: Element,
  B: KtElement> A.element(): B =
  when (this) {
    else -> TODO("unsupported $this")
  }


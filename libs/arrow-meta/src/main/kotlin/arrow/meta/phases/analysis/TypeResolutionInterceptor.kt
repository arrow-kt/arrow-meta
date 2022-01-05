package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingContext

interface TypeResolutionInterceptor : ExtensionPhase {
    fun CompilerContext.interceptFunctionLiteralDescriptor(
      expression: KtLambdaExpression,
      context: ExpressionTypingContext,
      descriptor: AnonymousFunctionDescriptor
    ): AnonymousFunctionDescriptor

    fun CompilerContext.interceptType(
      element: KtElement,
      context: ExpressionTypingContext,
      resultType: KotlinType
    ): KotlinType
}

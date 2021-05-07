package arrow.meta.quotes.nameddeclaration.notstubbed

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunctionNotStubbed
import org.jetbrains.kotlin.psi.KtParameter

/**
 * A template destructuring [Scope] for a [KtFunctionNotStubbed]
 */
open class FunctionNotStubbed<out T: KtFunctionNotStubbed>(
  override val value: T,
  val `(params)`: ScopedList<KtParameter> = ScopedList(value = value.valueParameters, postfix = " -> "),
  val bodyExpression: Scope<KtExpression> = Scope(value.bodyExpression)
) : Scope<T>(value)
package arrow.meta.quotes.nameddeclaration.typeparameterlistowner

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtNamedFunction] [Quote] with a custom template destructuring [NamedFunction]
 *
 * @param match designed to to feed in any kind of [KtNamedFunction] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.namedFunction(
  match: KtNamedFunction.() -> Boolean,
  map: NamedFunction.(KtNamedFunction) -> Transform<KtNamedFunction>
): ExtensionPhase =
  quote(match, map) { NamedFunction(it) }

/**
 * A template destructuring [Scope] for a [KtNamedFunction]
 */
class NamedFunction(
  override val value: KtNamedFunction,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.receiverTypeReference), postfix = "."),
  val name: Name? = value.nameAsName,
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.typeReference), prefix = " : "),
  val body: FunctionBody? = value.body()?.let { FunctionBody(it) }
) : Scope<KtNamedFunction>(value)

class FunctionBody(
  override val value: KtExpression
) : Scope<KtExpression>(value) {
  override fun toString(): String =
    value.bodySourceAsExpression() ?: ""
}

package arrow.meta.quotes.expression.expressionwithlabel

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.Quote
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtPropertyAccessor] [Quote] with a custom template destructuring [PropertyAccessor]
 *
 * @param match designed to to feed in any kind of [KtPropertyAccessor] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.propertyAccessor(
  match: KtPropertyAccessor.() -> Boolean,
  map: PropertyAccessor.(KtPropertyAccessor) -> Transform<KtPropertyAccessor>
): ExtensionPhase =
  quote(match, map) { PropertyAccessor(it) }

/**
 * A template destructuring [Scope] for a [KtNamedFunction]
 */
class PropertyAccessor(
  override val value: KtPropertyAccessor?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value?.valueParameters ?: emptyList(),
    postfix = ")",
    forceRenderSurroundings = true
  )
) : Scope<KtPropertyAccessor>(value)


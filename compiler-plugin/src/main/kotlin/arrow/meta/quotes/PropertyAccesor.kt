package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtPropertyAccessor] [Quote] with a custom template destructuring [PropertyAccessorScope]
 *
 * @param match designed to to feed in any kind of [KtPropertyAccessor] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.propertyAccessor(
  match: KtPropertyAccessor.() -> Boolean,
  map: PropertyAccessorScope.(KtPropertyAccessor) -> Transform<KtPropertyAccessor>
): ExtensionPhase =
  quote(match, map) { PropertyAccessorScope(it) }

/**
 * A template destructuring [Scope] for a [KtNamedFunction]
 */
class PropertyAccessorScope(
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


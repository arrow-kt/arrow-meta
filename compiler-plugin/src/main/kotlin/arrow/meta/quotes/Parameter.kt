package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtParameter] [Quote] with a custom template destructuring [Parameter]
 *
 * @param match designed to to feed in any kind of [KtParameter] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.parameter(
  match: KtParameter.() -> Boolean,
  map: Parameter.(KtParameter) -> Transform<KtParameter>
) : ExtensionPhase =
  quote(match, map) { Parameter(it) }

/**
 * A template destructuring [Scope] for a [KtParameter]
 */
class Parameter(
  override val value: KtParameter?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value?.nameAsName,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries ?: listOf()),
  val type: Scope<KtTypeReference> = Scope(value?.typeReference),
  val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters ?: listOf(), postfix = ">"),
  val defaultValue: Scope<KtExpression> = Scope(value?.defaultValue)
) : Scope<KtParameter>(value)
package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtProperty] [Quote] with a custom template destructuring [Property]
 *
 * @param match designed to to feed in any kind of [KtProperty] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.property(
  match: KtProperty.() -> Boolean,
  map: Property.(KtProperty) -> Transform<KtProperty>
): ExtensionPhase =
  quote(match, map) { Property(it) }

/**
 * A template destructuring [Scope] for a [KtProperty]
 */
class Property(
        override val value: KtProperty?,
        val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
        val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
        val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters ?: listOf(), postfix = ">"),
        val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value?.receiverTypeReference), postfix = "."),
        val name: Name? = value?.nameAsName,
        val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value?.valueParameters ?: listOf(),
    postfix = ")",
    forceRenderSurroundings = true
  ),
        val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value?.typeReference), prefix = " : "),
        val getter : PropertyAccessor = PropertyAccessor(value?.getter),
        val setter : PropertyAccessor = PropertyAccessor(value?.setter)
) : Scope<KtProperty>(value)


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
 * A [KtProperty] [Quote] with a custom template destructuring [PropertyScope]
 */
fun Meta.property(
  match: KtProperty.() -> Boolean,
  map: PropertyScope.(KtProperty) -> Transform<KtProperty>
): ExtensionPhase =
  quote(match, map) { PropertyScope(it) }

/**
 * A template destructuring [Scope] for a [KtProperty]
 */
class PropertyScope(
  override val value: KtProperty,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.receiverTypeReference), postfix = "."),
  val name: Name? = value.nameAsName,
  val `(valueParameters)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.typeReference), prefix = " : "),
  val getter : PropertyAccessorScope = PropertyAccessorScope(value.getter),
  val setter : PropertyAccessorScope = PropertyAccessorScope(value.setter)
) : Scope<KtProperty>(value)


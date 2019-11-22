package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * A [KtTypeAlias] [Quote] with a custom template destructuring [TypeAlias]
 *
 * @param match designed to to feed in any kind of [KtTypeAlias] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.typeAlias(
  match: KtTypeAlias.() -> Boolean,
  map: TypeAlias.(KtTypeAlias) -> Transform<KtTypeAlias>
): ExtensionPhase =
  quote(match, map) { TypeAlias(it) }

/**
 * A template destructuring [Scope] for a [KtTypeAlias]
 */
class TypeAlias(
  override val value: KtTypeAlias,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value.nameAsName,
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val type: Scope<KtTypeReference> = Scope(value.getTypeReference())
  ) : Scope<KtTypeAlias>(value)

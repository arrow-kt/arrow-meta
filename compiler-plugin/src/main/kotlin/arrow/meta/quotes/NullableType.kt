package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A [KtNullableType] [Quote] with a custom template destructuring [NullableTypeScope]
 */
fun Meta.nullableType(
  match: KtNullableType.() -> Boolean,
  map: NullableTypeScope.(KtNullableType) -> Transform<KtNullableType>
): ExtensionPhase =
  quote(match, map) { NullableTypeScope(it) }

/**
 * A template destructuring [Scope] for a [KtNullableType]
 */
class NullableTypeScope(
  override val value: KtNullableType,
  val `@annotationEntries`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val typeArgumentsAsTypes: ScopedList<KtTypeReference>? = value.innerType?.let { ScopedList(it.typeArgumentsAsTypes, prefix = " : ") } ?: ScopedList(emptyList()),
  // TODO create scope for KtTypeElement?
  val innerType: Scope<KtTypeElement> = Scope(value.innerType)
) : Scope<KtNullableType>(value)
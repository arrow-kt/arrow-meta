package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * A [KtNullableType] [Quote] with a custom template destructuring [NullableTypeScope]
 *
 * Note: [KtTypeReference] is called [TypeMirror] in the Java annotation processing API.
 *
 * @param match designed to to feed in any kind of [KtNullableType] predicate returning a [Boolean]
 * @param map a function that maps over the resulting action from matching on the transformation at the PSI level.
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
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val typeArgumentsAsTypes: ScopedList<KtTypeReference>? = value.innerType?.let { ScopedList(it.typeArgumentsAsTypes, prefix = " : ") } ?: ScopedList(emptyList()),
  val innerType: Scope<KtTypeElement> = Scope(value.innerType)    // TODO KtTypeElement scope and quote template
) : Scope<KtNullableType>(value)


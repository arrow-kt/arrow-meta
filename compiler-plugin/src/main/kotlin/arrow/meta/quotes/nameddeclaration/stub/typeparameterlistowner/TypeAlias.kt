package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>"""typealias $name = $type""".typeAlias</code>
 *
 *  TODO add wrappers for certain boolean logic so that there are more options to create KtTypeAlias
 * @see [DefaultElementScope::typeAlias]
 *
 * A template destructuring [Scope] for a [KtTypeAlias].
 *
 * * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.typeAlias
 *
 * val Meta.reformatTypeAlias: Plugin
 *  get() =
 *   "ReformatTypeAlias" {
 *    typeAlias(
 *     property({ true }) { dec ->
 *      Transform.replace(
 *       replacing = dec,
 *       newDeclaration = """typealias $name = $type""".typeAlias
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class TypeAlias(
  override val value: KtTypeAlias,
  val `@annotationEntries`: ScopedList<KtAnnotationEntry> = ScopedList(value.annotationEntries),
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value.nameAsName,
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val type: Scope<KtTypeReference> = Scope(value.getTypeReference())
  ) : Scope<KtTypeAlias>(value)

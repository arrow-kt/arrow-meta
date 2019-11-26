package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.expression.expressionwithlabel.PropertyAccessor
import arrow.meta.quotes.modifierlist.TypeReference
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>""" $modifier $visibility val $name: $typeReference = $getter by $delegate""".property</code>
 *
 * TODO add wrappers for certain boolean logic so that there are more options to create KtProperty
 * @see [DefaultElementScope::property]
 *
 * A template destructuring [Scope] for a [KtProperty].
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.property
 *
 * val Meta.reformatProperty: Plugin
 *  get() =
 *   "ReformatProperty" {
 *    meta(
 *     property({ true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """ $modality $visibility val $name: $typeReference = $getter by $delegate""".property
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class Property(
  override val value: KtProperty?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters
    ?: listOf(), postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value?.receiverTypeReference), postfix = "."),
  val name: Name? = value?.nameAsName,
  val typeReference: TypeReference? = TypeReference(value?.typeReference),
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value?.valueParameters ?: listOf(),
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val delegate: Scope<KtPropertyDelegate> = Scope(value?.delegate), // TODO KtPropertyDelegate scope and quote template
  val delegateExpression: Scope<KtExpression> = Scope(value?.delegateExpression), // TODO KtExpression scope and quote template
  val initializer: Scope<KtExpression> = Scope(value?.initializer), // TODO KtExpression scope and quote template
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value?.typeReference), prefix = " : "),
  val getter : PropertyAccessor = PropertyAccessor(value?.getter),
  val setter : PropertyAccessor = PropertyAccessor(value?.setter)
) : Scope<KtProperty>(value)


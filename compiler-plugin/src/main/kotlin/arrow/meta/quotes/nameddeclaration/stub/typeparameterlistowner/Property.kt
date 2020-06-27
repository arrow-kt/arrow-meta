package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.SyntheticElement
import arrow.meta.quotes.TypedScope
import arrow.meta.quotes.declaration.PropertyAccessor
import arrow.meta.quotes.modifierlistowner.TypeReference
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
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
 * <code>""" $modality $visibility $valOrVar $name $returnType $initializer """.property</code>
 * *
 * A template destructuring [Scope] for a [KtProperty].
 *
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.property
 *
 * val Meta.reformatPropertySetter: CliPlugin
 *  get() =
 *   "Reformat Property Setter" {
 *    meta(
 *     property(this, { true }) { (e, d) ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = """$modality $visibility $valOrVar $name $returnType $initializer
 *                              $getter
 *                              $setter
 *                              $delegate""".property(d)
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class Property(
  override val value: KtProperty,
  override val descriptor: PropertyDescriptor?,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters
    ?: listOf(), postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.receiverTypeReference), postfix = "."),
  val name: Name? = value.nameAsName,
  val typeReference: TypeReference? = TypeReference(value.typeReference),
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters ?: listOf(),
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val delegate: Scope<KtPropertyDelegate> = Scope(value.delegate), // TODO KtPropertyDelegate scope and quote template
  val delegateExpressionOrInitializer: Scope<KtExpression> = Scope(value.delegateExpressionOrInitializer), // TODO KtExpression scope and quote template
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.typeReference), prefix = " : "),
  val initializer: ScopedList<KtExpression> = ScopedList(listOfNotNull(value.initializer), prefix = " = "),
  val valOrVar: Name = when {
    value.isVar -> "var"
    else -> "val"
  }.let(Name::identifier),
  val getter : PropertyAccessor = PropertyAccessor(value.getter),
  val setter : PropertyAccessor = PropertyAccessor(value.setter)
) : TypeParameterListOwner<KtProperty, PropertyDescriptor>(value, descriptor), SyntheticElement {
  override fun ElementScope.identity(descriptor: PropertyDescriptor?): TypedScope<KtProperty, PropertyDescriptor> {
    return """$modality $visibility $valOrVar $name $returnType $initializer
                  $getter
                  $setter
                  $delegate""".property(descriptor)
  }
}


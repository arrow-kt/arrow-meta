package arrow.meta.quotes.declaration

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>""" var $name $returnType $initializer $visibility get $`(params)`$bodyExpression" "".propertyAccessorGet </code>
 **
 * A template destructuring [Scope] for a [KtPropertyAccessor]
 *
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.phases.CompilerContext
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.propertyAccessor
 *
 * val Meta.reformatPropertySetter: CliPlugin
 *  get() =
 *   "Reformat Property Setter" {
 *    meta(
 *     property(this, { true }) { e ->
 *      Transform.replace(
 *       replacing = e,
 *       newDeclaration = if (value != null && value.isGetter) {
 *                            """var $name $returnType $initializer
 *                                    $visibility get $`(params)`$bodyExpression""".propertyAccessorGet
 *                         } else if(value != null && value.isSetter) {
 *                            """var  $name $returnType $initializer
 *                                  $visibility set $`(params)` $bodyExpression""".propertyAccessorSet
 *                        } else {
 *                            empty<KtPropertyAccessor>()
 *                        }
 *       )
 *      }
 *     )
 *    }
 * ```
 */
class PropertyAccessor(
  override val value: KtPropertyAccessor?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier) ?: Name.identifier(""),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier) ?: Name.identifier(""),
  val bodyExpression: Scope<KtExpression> = Scope(value?.bodyExpression),
  val name: Name? = value?.property?.nameAsName,
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value?.valueParameters ?: emptyList(),
    postfix = ")",
    forceRenderSurroundings = true),
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value?.property?.typeReference), prefix = " : "),
  val initializer: ScopedList<KtExpression> = ScopedList(listOfNotNull(value?.property?.initializer), prefix = " = ")
) : Scope<KtPropertyAccessor>(value) {
  override fun ElementScope.identity(): Scope<KtPropertyAccessor> =
    if (value != null && value.isGetter) {
      """var $name $returnType $initializer 
              $visibility get $`(params)`$bodyExpression""".propertyAccessorGet
    } else if (value != null && value.isSetter) {
      """var  $name $returnType $initializer
              $visibility set $`(params)` $bodyExpression""".propertyAccessorSet
    } else {
      empty<KtPropertyAccessor>()
    }
}

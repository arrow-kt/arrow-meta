package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.body
import arrow.meta.phases.analysis.bodySourceAsExpression
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>""" $modality $visibility fun $`(typeParameters)` $receiver.$name $`(args)` : $returnType = { $body } """.namedFunction</code>
 *
 * A template destructuring [Scope] for a [KtNamedFunction].
 *
 * ```
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.namedFunction
 *
 * val Meta.reformatNamedFunction: CliPlugin
 *    get() =
 *      "Reformat Named Function" {
 *        meta(
 *          namedFunction(this, { true }) { typeParameterListOwner ->
 *            Transform.replace(
 *              replacing = typeParameterListOwner,
 *              newDeclaration = """ $modifiers fun $receiver $name $`(params)` $returnType = $body """.function
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class NamedFunction(
  override val value: KtNamedFunction,
  val modality: Name? = value.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value.visibilityModifierType()?.value?.let(Name::identifier),
  val modifiers: Scope<KtModifierList> = Scope(value.modifierList),
  val `(typeParameters)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value.typeParameters, postfix = ">"),
  val receiver: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.receiverTypeReference), postfix = "."),
  val name: Name? = value.nameAsName,
  val `(params)`: ScopedList<KtParameter> = ScopedList(
    prefix = "(",
    value = value.valueParameters,
    postfix = ")",
    forceRenderSurroundings = true
  ),
  val returnType: ScopedList<KtTypeReference> = ScopedList(listOfNotNull(value.typeReference), prefix = " : "),
  val body: FunctionBody? = value.body()?.let { FunctionBody(it) }
) : TypeParameterListOwner<KtNamedFunction>(value) {
    override fun ElementScope.identity(): Scope<KtNamedFunction> {
        return """ $modifiers fun $receiver $name $`(params)` $returnType = $body """.function
    }
}

class FunctionBody(override val value: KtExpression) : Scope<KtExpression>(value) {
  override fun toString(): String =
    value.bodySourceAsExpression() ?: ""
}
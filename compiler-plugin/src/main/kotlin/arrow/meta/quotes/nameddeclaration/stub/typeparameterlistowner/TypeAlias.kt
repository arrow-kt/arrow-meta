package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.modifierlistowner.TypeReference
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtTypeAlias

/**
 * <code>typeAlias("""$name""", `(typeParameters)`.toStringList() , """$type""")</code>
 *
 * A template destructuring [Scope] for a [KtTypeAlias].
 *
 * * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.typeAlias
 *
 * val Meta.reformatTypeAlias: CliPlugin
 *    get() =
 *      "Reformat Type Alias" {
 *        typeAlias(this, { true }) { typeParameterListOwner ->
 *          Transform.replace(
 *            replacing = typeParameterListOwner,
 *            newDeclaration = typeAlias("""$name""", `(typeParameters)`.toStringList() , """$type""")
 *          )
 *        }
 *      )
 *    }
 * ```
 */
class TypeAlias(
  override val value: KtTypeAlias,
  override val descriptor: TypeAliasDescriptor?,
  val name: Name? = value.nameAsName,
  val type: TypeReference = TypeReference(value.getTypeReference())
) : TypeParameterListOwner<KtTypeAlias, TypeAliasDescriptor>(value, descriptor) {

  override fun ElementScope.identity(descriptor: TypeAliasDescriptor?): TypeAlias =
    typeAlias("""$name""", `(typeParams)`.toStringList() , """$type""", descriptor)

}

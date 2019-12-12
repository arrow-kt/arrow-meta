package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * <code>typeAlias("""$name""", `(typeParameters)`.toStringList() , """$type""")</code>
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
 *    typeAlias({ true }) { typeAlias ->
 *      Transform.replace(
 *       replacing = typeAlias,
 *       newDeclaration = typeAlias("""$name""", `(typeParameters)`.toStringList() , """$type""")
 *      )
 *      }
 *     )
 *    }
 * ```
 */
class TypeAlias(
  override val value: KtTypeAlias,
  val name: Name? = value.nameAsName,
  val type: Scope<KtTypeReference> = Scope(value.getTypeReference())
) : TypeParameterListOwner<KtTypeAlias>(value)

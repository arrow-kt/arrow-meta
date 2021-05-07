package arrow.meta.quotes.element

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * <code>""" $`(params)` """.parameterList</code>
 *
 * A template destructuring [Scope] for a [KtParameterList].
 *
 * ``kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.CliPlugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameterList
 *
 * val Meta.reformatModifier: CliPlugin
 *  get() =
 *  "ReformatParameter" {
 *   meta(
 *    parameterList({ true }) { l ->
 *     Transform.replace(
 *      replacing = l,
 *      newDeclaration = """ $`(params)` """.parameterList
 *     )
 *    }
 *   )
 *  }
 *```
 */
class ParameterList(
  override val value: KtParameterList?,
  val `(params)`: ScopedList<KtParameter> = ScopedList(value?.parameters.orEmpty())
) : Scope<KtParameterList>(value)
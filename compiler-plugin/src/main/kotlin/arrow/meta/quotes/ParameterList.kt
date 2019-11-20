package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList

/**
 * A [KtParameterList] [Quote] with a custom template destructuring [ParameterListScope]. See below:
 *
 *```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameterList
 *
 * val Meta.reformatParameterList: Plugin
 *  get() =
 *  "ReformatParameterList" {
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
 *
 * * @param match designed to to feed in any kind of [KtParameterList] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.parameterList(
  match: KtParameterList.() -> Boolean,
  map: ParameterListScope.(KtParameterList) -> Transform<KtParameterList>
): ExtensionPhase =
  quote(match, map) { ParameterListScope(it) }

/**
 * A template destructuring [Scope] for a [KtParameterList]
 */
class ParameterListScope(
  override val value: KtParameterList?,
  val `(params)`: ScopedList<KtParameter> = ScopedList(value?.parameters ?: listOf())
) : Scope<KtParameterList>(value)
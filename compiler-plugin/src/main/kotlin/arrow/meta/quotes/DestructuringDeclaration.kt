package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtExpression

/**
 * A [KtDestructuringDeclaration] [Quote] with a custom template destructuring [DestructuringDeclarationScope]. See below:
 *
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.destructuringDeclaration
 *
 * val Meta.reformatDestructuringDeclaration: Plugin
 *  get() =
 *   "ReformatDestructuringDeclaration" {
 *    meta(
 *     destructuringDeclaration({ true }) { c ->
 *      Transform.replace(
 *       replacing = c,
 *       newDeclaration = """ $`(vars)` = $initializer """.destructuringDeclaration
 *      )
 *     }
 *    )
 *   }
 *```
 *
 * @param match designed to to feed in any kind of [KtDestructuringDeclaration] predicate returning a [Boolean]
 * @param map map a function that maps over the resulting action from matching on the transformation at the PSI level.
 */
fun Meta.destructuringDeclaration(
  match: KtDestructuringDeclaration.() -> Boolean,
  map: DestructuringDeclarationScope.(KtDestructuringDeclaration) -> Transform<KtDestructuringDeclaration>
): ExtensionPhase =
  quote(match, map) { DestructuringDeclarationScope(it) }

/**
 * A template destructuring [Scope] for a [KtDestructuringDeclaration]
 */
class DestructuringDeclarationScope(
  override val value: KtDestructuringDeclaration?,
  val `(vars)`: ScopedList<KtDestructuringDeclarationEntry> = ScopedList(value?.entries ?: listOf()),
  val initializer: Scope<KtExpression> = Scope(value?.initializer)
) : Scope<KtDestructuringDeclaration>(value)
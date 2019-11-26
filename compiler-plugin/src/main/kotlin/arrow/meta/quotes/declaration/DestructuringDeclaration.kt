package arrow.meta.quotes.declaration

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code>```""" $`(vars)` = $initializer """.destructuringDeclaration```</code>
 *
 * A template destructuring [Scope] for a [KtDestructuringDeclaration].
 *
 *  ```kotlin:ank:silent
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
 * ```
 */
class DestructuringDeclaration(
  override val value: KtDestructuringDeclaration?,
  val `(vars)`: ScopedList<KtDestructuringDeclarationEntry> = ScopedList(value?.entries
    ?: listOf()),
  val initializer: Scope<KtExpression> = Scope(value?.initializer)
) : Scope<KtDestructuringDeclaration>(value)
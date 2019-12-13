package arrow.meta.quotes.declaration

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtExpression

/**
 * <code>"""$valOrVar ($entries) = $initializer """.destructuringDeclaration</code>
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
 *    get() =
 *      "Reformat Destructuring Declaration" {
 *        meta(
 *          destructuringDeclaration({ true }) { declaration ->
 *            Transform.replace(
 *              replacing = declaration,
 *              newDeclaration = """$valOrVar ($entries) = $initializer """.destructuringDeclaration
 *            )
 *          }
 *        )
 *      }
 * ```
 */
class DestructuringDeclaration(
  override val value: KtDestructuringDeclaration?,
  val valOrVar: Name? = when {
    value?.isVar == true -> "var"
    value?.isVar != true -> "val"
    else -> ""
  }.let(Name::identifier),
  val entries: ScopedList<KtDestructuringDeclarationEntry> = ScopedList(value?.entries.orEmpty()),
  val initializer: Scope<KtExpression> = Scope(value?.initializer)
) : Scope<KtDestructuringDeclaration>(value) {
  override fun ElementScope.identity(): DestructuringDeclaration =
    """$valOrVar ($entries) = $initializer""".destructuringDeclaration
}